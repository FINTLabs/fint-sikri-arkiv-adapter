package no.fint.sikri.data.noark.common;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.AdditionalFieldService;
import no.fint.arkiv.TitleService;
import no.fint.arkiv.sikri.oms.AdministrativeUnitType;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.model.administrasjon.arkiv.*;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import no.fint.model.resource.kultur.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.data.noark.journalpost.JournalpostService;
import no.fint.sikri.data.noark.merknad.MerknadService;
import no.fint.sikri.data.noark.part.PartService;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.data.utilities.SikriUtils;
import no.fint.sikri.service.SikriCaseDefaultsService;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

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
    private SikriCaseDefaultsService caseDefaultsService;

    @Autowired
    private TitleService titleService;

    @Autowired
    private AdditionalFieldService additionalFieldService;

    private ObjectFactory objectFactory;

    @PostConstruct
    public void init() {
        objectFactory = new ObjectFactory();
    }

    public <T extends SaksmappeResource> T applyValuesForSaksmappe(CaseType input, T resource) {
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

        resource.setJournalpost(journalpostService.queryForSaksmappe(resource));
        resource.setPart(partService.queryForSaksmappe(resource));

        resource.setMerknad(merknadService.getRemarkForCase(input.getId().toString()));

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

        optionalValue(input.getPrimaryClassification())
                .ifPresent(c -> resource.addKlasse(
                        Link.with(
                                Klasse.class,
                                "systemid",
                                String.valueOf(input.getPrimaryClassification().getClassId()))
                        )
                );
        optionalValue(input.getSecondaryClassification())
                .ifPresent(c -> resource.addKlasse(
                        Link.with(
                                Klasse.class,
                                "systemid",
                                String.valueOf(input.getSecondaryClassification().getClassId()))
                        )
                );

        titleService.parseTitle(resource, input.getTitle());
        additionalFieldService.setFieldsForResource(resource,
                Arrays.stream(PropertyUtils.getPropertyDescriptors(input))
                        .filter(p -> p.getName().startsWith("customAttribute"))
                        .map(p -> new AdditionalFieldService.Field(p.getName(), readProperty(input, p)))
                        .collect(Collectors.toList()));

        return resource;
    }

    private String readProperty(CaseType input, java.beans.PropertyDescriptor p) {
        try {
            return (String) p.getReadMethod().invoke(input);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends SaksmappeResource> CaseType toCaseType(T resource) {
        CaseType caseType = objectFactory.createCaseType();
        caseDefaultsService.applyDefaultsToCaseType(resource, caseType);

        caseType.setTitle(titleService.getTitle(resource));

        //TODO -> CaseDefaultsService
        caseType.setFileTypeId("TS");

        additionalFieldService.getFieldsForResource(resource)
                .forEach(field ->
                        setProperty(caseType, field));

        applyParameterFromLink(
                resource.getAdministrativEnhet(),
                Integer::valueOf,
                caseType::setAdministrativeUnitId
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
