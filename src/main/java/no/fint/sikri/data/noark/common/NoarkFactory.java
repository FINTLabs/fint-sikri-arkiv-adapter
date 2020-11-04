package no.fint.sikri.data.noark.common;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.AdditionalFieldService;
import no.fint.arkiv.CaseProperties;
import no.fint.arkiv.TitleService;
import no.fint.arkiv.sikri.oms.AdministrativeUnitType;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ExternalSystemLinkCaseType;
import no.fint.model.FintComplexDatatypeObject;
import no.fint.model.arkiv.kodeverk.Saksstatus;
import no.fint.model.arkiv.noark.AdministrativEnhet;
import no.fint.model.arkiv.noark.Arkivdel;
import no.fint.model.arkiv.noark.Arkivressurs;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.SaksmappeResource;
import no.fint.sikri.data.noark.journalpost.JournalpostService;
import no.fint.sikri.data.noark.klasse.KlasseFactory;
import no.fint.sikri.data.noark.merknad.MerknadService;
import no.fint.sikri.data.noark.part.PartService;
import no.fint.sikri.data.noark.skjerming.SkjermingService;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.data.utilities.SikriUtils;
import no.fint.sikri.service.SikriCaseDefaultsService;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.SikriUtils.applyParameterFromLink;
import static no.fint.sikri.data.utilities.SikriUtils.optionalValue;

@Slf4j
@Service
public class NoarkFactory {

    @Autowired
    private JournalpostService journalpostService;

    @Autowired
    private PartService partService;

    @Autowired
    private MerknadService merknadService;

    @Autowired
    private KlasseFactory klasseFactory;

    @Autowired
    private SikriCaseDefaultsService caseDefaultsService;

    @Autowired
    private TitleService titleService;

    @Autowired
    private AdditionalFieldService additionalFieldService;

    @Autowired
    private SkjermingService skjermingService;

    public ExternalSystemLinkCaseType externalSystemLink(Integer caseId, String externalKey) {
        ExternalSystemLinkCaseType externalSystemLinkCaseType = new ExternalSystemLinkCaseType();
        externalSystemLinkCaseType.setCaseId(caseId);
        externalSystemLinkCaseType.setExternalKey(externalKey);
        externalSystemLinkCaseType.setExternalSystemCode(4);

        return externalSystemLinkCaseType;
    }

    public <T extends SaksmappeResource> T applyValuesForSaksmappe(CaseProperties caseProperties, CaseType input, T resource) {
        applyFieldsForSaksmappe(input, resource);
        queryNestedResources(caseProperties, input, resource);
        addLinksToSaksmappe(input, resource);
        parseTitleAndFields(caseProperties, input, resource);
        return resource;
    }

    <T extends SaksmappeResource> void addLinksToSaksmappe(CaseType input, T resource) {
        optionalValue(input.getAdministrativeUnit())
                .map(AdministrativeUnitType::getShortCodeThisLevel)
                .flatMap(SikriUtils::optionalValue)
                .map(Link.apply(AdministrativEnhet.class, "systemid"))
                .ifPresent(resource::addAdministrativEnhet);

        optionalValue(input.getSeriesId())
                .map(Link.apply(Arkivdel.class, "systemid"))
                .ifPresent(resource::addArkivdel);

        optionalValue(input.getCreatedByUserNameId())
                .map(String::valueOf)
                .map(Link.apply(Arkivressurs.class, "systemid"))
                .ifPresent(resource::addOpprettetAv);

        optionalValue(input.getOfficerNameId())
                .map(String::valueOf)
                .map(Link.apply(Arkivressurs.class, "systemid"))
                .ifPresent(resource::addSaksansvarlig);

        optionalValue(input.getCaseStatusId())
                .map(String::valueOf)
                .map(Link.apply(Saksstatus.class, "systemid"))
                .ifPresent(resource::addSaksstatus);
    }

    <T extends SaksmappeResource> void applyFieldsForSaksmappe(CaseType input, T resource) {
        String caseNumber = NOARKUtils.getMappeId(
                input.getCaseYear().toString(),
                input.getSequenceNumber().toString()
        );
        Integer caseYear = input.getCaseYear();
        Integer sequenceNumber = input.getSequenceNumber();

        resource.setMappeId(FintUtils.createIdentifikator(caseNumber));
        resource.setSystemId(FintUtils.createIdentifikator(input.getId().toString()));
        resource.setSakssekvensnummer(String.valueOf(sequenceNumber));
        resource.setSaksaar(String.valueOf(caseYear));
        resource.setSaksdato(input.getCaseDate().toGregorianCalendar().getTime());
        resource.setOpprettetDato(input.getCreatedDate().toGregorianCalendar().getTime());
        resource.setTittel(input.getTitle());
        resource.setOffentligTittel(input.getPublicTitle());

        optionalValue(skjermingService.getSkjermingResource(input::getAccessCodeId, input::getPursuant))
                .ifPresent(resource::setSkjerming);

        resource.setKlasse(
                Stream.of(input.getPrimaryClassification(), input.getSecondaryClassification())
                        .filter(Objects::nonNull)
                        .map(klasseFactory::toFintResource)
                        .collect(Collectors.toList()));
    }

    <T extends SaksmappeResource> void queryNestedResources(CaseProperties caseProperties, CaseType input, T resource) {
        resource.setJournalpost(journalpostService.queryForSaksmappe(caseProperties, resource));
        resource.setPart(partService.queryForSaksmappe(resource));
        resource.setMerknad(merknadService.getRemarkForCase(input.getId().toString()));
    }

    <T extends SaksmappeResource> void parseTitleAndFields(CaseProperties caseProperties, CaseType input, T resource) {
        for (PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(resource)) {
            if (FintComplexDatatypeObject.class.isAssignableFrom(descriptor.getPropertyType())) {
                try {
                    if (descriptor.getReadMethod().invoke(resource)  == null) {
                        descriptor.getWriteMethod().invoke(resource, descriptor.getPropertyType().newInstance());
                    }
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException ignore) {
                }
            }
        }

        titleService.parseCaseTitle(caseProperties.getTitle(), resource, input.getTitle());
        additionalFieldService.setFieldsForResource(
                caseProperties.getField(),
                resource,
                Arrays.stream(PropertyUtils.getPropertyDescriptors(input))
                        .map(PropertyDescriptor::getName)
                        .filter(p -> p.startsWith("customAttribute"))
                        .map(p -> new AdditionalFieldService.Field(p, readProperty(input, p)))
                        .collect(Collectors.toList()));
    }

    private String readProperty(CaseType input, String name) {
        try {
            return (String) PropertyUtils.getProperty(input, name);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends SaksmappeResource> CaseType toCaseType(CaseProperties caseProperties, T resource) {
        CaseType caseType = new CaseType();
        caseDefaultsService.applyDefaultsToCaseType(caseProperties, resource, caseType);

        caseType.setTitle(titleService.getCaseTitle(caseProperties.getTitle(), resource));

        additionalFieldService.getFieldsForResource(caseProperties.getField(), resource)
                .forEach(field ->
                        setProperty(caseType, field));

        skjermingService.applyAccessCodeAndPursuant(resource.getSkjerming(), caseType::setAccessCodeId, caseType::setPursuant);

        applyParameterFromLink(
                resource.getAdministrativEnhet(),
                Integer::valueOf,
                caseType::setAdministrativeUnitId
        );

        applyParameterFromLink(
                resource.getJournalenhet(),
                caseType::setRegistryManagementUnitId
        );

        applyParameterFromLink(
                resource.getArkivdel(),
                caseType::setSeriesId
        );

        applyParameterFromLink(
                resource.getSaksstatus(),
                caseType::setCaseStatusId
        );

        return caseType;
    }

    private void setProperty(CaseType caseType, AdditionalFieldService.Field field) {
        try {
            PropertyUtils.setSimpleProperty(caseType, field.getName(), field.getValue());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
