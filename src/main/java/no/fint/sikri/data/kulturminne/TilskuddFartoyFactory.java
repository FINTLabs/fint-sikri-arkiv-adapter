package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ExternalSystemLinkCaseType;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.model.resource.kultur.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.data.noark.journalpost.JournalpostFactory;
import no.fint.sikri.data.noark.korrespondansepart.KorrespondansepartFactory;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.SikriUtils;
import no.fint.sikri.repository.KodeverkRepository;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.SikriUtils.applyParameterFromLink;

@Slf4j
@Service
public class TilskuddFartoyFactory {

    @Autowired
    private KodeverkRepository kodeverkRepository;

    @Autowired
    private NoarkFactory noarkFactory;

    @Autowired
    private KorrespondansepartFactory korrespondansepartFactory;

    @Autowired
    private JournalpostFactory journalpostFactory;

    @Autowired
    private TilskuddFartoyDefaults tilskuddFartoyDefaults;

    @Value("${fint.sikri.custom-attributes.casetype.tilskudd-fartoy.kallesignal:customAttribute1}")
    String kallesignalAttribute;

    @Value("${fint.sikri.custom-attributes.casetype.tilskudd-fartoy.fartoynavn:customAttribute2}")
    String fartoyNavnAttribute;

    @Value("${fint.sikri.custom-attributes.casetype.tilskudd-fartoy.soknadsnummer:customAttribute3}")
    String soknadsnummerAttribute;

    @Value("${fint.sikri.custom-attributes.casetype.tilskudd-fartoy.kulturminneid:customAttribute4}")
    String kulturminneIdAttribute;

    private ObjectFactory objectFactory;

    public TilskuddFartoyFactory() {
        objectFactory = new ObjectFactory();
    }

    public CaseType toCaseType(TilskuddFartoyResource tilskuddFartoy) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        CaseType caseType = new CaseType();
        tilskuddFartoyDefaults.applyDefaultsToCaseType(tilskuddFartoy, caseType);

        caseType.setTitle(tilskuddFartoy.getTittel());

        caseType.setFileTypeId("TS");

        PropertyUtils.setSimpleProperty(caseType, kallesignalAttribute, createValue(kallesignalAttribute, tilskuddFartoy.getKallesignal()));
        PropertyUtils.setSimpleProperty(caseType, fartoyNavnAttribute, createValue(fartoyNavnAttribute, tilskuddFartoy.getFartoyNavn()));
        PropertyUtils.setSimpleProperty(caseType, soknadsnummerAttribute, createValue(soknadsnummerAttribute, tilskuddFartoy.getSoknadsnummer().getIdentifikatorverdi()));
        PropertyUtils.setSimpleProperty(caseType, kulturminneIdAttribute, createValue(kulturminneIdAttribute, tilskuddFartoy.getKulturminneId()));

        applyParameterFromLink(
                tilskuddFartoy.getAdministrativEnhet(),
                s -> Integer.valueOf(s),
                caseType::setAdministrativeUnitId
        );

        applyParameterFromLink(
                tilskuddFartoy.getArkivdel(),
                caseType::setRegistryManagementUnitId
        );

        applyParameterFromLink(
                tilskuddFartoy.getSaksstatus(),
                caseType::setCaseStatusId
        );

        return caseType;
    }

    public ExternalSystemLinkCaseType externalSystemLink(Integer caseId, String externalKey) {
        ExternalSystemLinkCaseType externalSystemLinkCaseType = new ExternalSystemLinkCaseType();
        externalSystemLinkCaseType.setCaseId(caseId);
        externalSystemLinkCaseType.setExternalKey(externalKey);
        externalSystemLinkCaseType.setExternalSystemCode(4);

        return externalSystemLinkCaseType;
    }

    @SuppressWarnings("unchecked")
    private String createValue(String attribute, String value) throws InvocationTargetException, IllegalAccessException {
        Method method = BeanUtils.findMethodWithMinimalParameters(objectFactory.getClass(), "createCaseType" + StringUtils.capitalize(attribute));

        return (String) method.invoke(objectFactory, value);
    }

    public TilskuddFartoyResource toFintResource(CaseType input) {
//        if (input.getFields().getVirksomhetsspesifikkeMetadata() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getKulturminneid() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getFartoynavn() == null) {
//            throw new NotTilskuddfartoyException(input.getFields().getMappeIdent());
//        }

        TilskuddFartoyResource tilskuddFartoy = noarkFactory.applyValuesForSaksmappe(input, new TilskuddFartoyResource());
        applyParameter(input, kallesignalAttribute, tilskuddFartoy::setKallesignal);
        applyParameter(input, fartoyNavnAttribute, tilskuddFartoy::setFartoyNavn);
        applyParameter(input, kulturminneIdAttribute, tilskuddFartoy::setKulturminneId);
        applyParameter(input, soknadsnummerAttribute, tilskuddFartoy::setSoknadsnummer, FintUtils::createIdentifikator);

//        tilskuddFartoy.setFartoyNavn(input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getFartoynavn().().get(0));
//        tilskuddFartoy.setKallesignal(input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getKallesignal().().get(0));
//        tilskuddFartoy.setSoknadsnummer(createIdentifikator(input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getSoeknadsnummer().().get(0)));
//        tilskuddFartoy.setKulturminneId(input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getKulturminneid().().get(0));

//        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "soknadsnummer", tilskuddFartoy.getSoknadsnummer().getIdentifikatorverdi()));
//        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "mappeid", input.getFields().getMappeIdent()));
//        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "systemid", input.getId()));

        return tilskuddFartoy;
    }

    public void applyParameter(CaseType input, String attribute, Consumer<String> consumer) {
        applyParameter(input, attribute, consumer, Function.identity());
    }

    public <T> void applyParameter(CaseType input, String attribute, Consumer<T> consumer, Function<String, T> mapper) {
        try {
            Stream.of(PropertyUtils.getProperty(input, attribute))
                    .filter(Objects::nonNull)
                    .filter(JAXBElement.class::isInstance)
                    .map(JAXBElement.class::cast)
                    .filter(SikriUtils::notNil)
                    .map(String::valueOf)
                    .filter(StringUtils::isNotBlank)
                    .map(mapper)
                    .forEach(consumer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.trace("Error when retrieving property {} from {}", attribute, input, e);
        }
    }


/*


    public List<TilskuddFartoyResource> toFintResourceList(QueryResult results) throws GetDocumentException, IllegalCaseNumberFormat {
        List<TilskuddFartoyResource> resources = new ArrayList<>(results.getResults().size());
        for (Result__1 result : results.getResults()) {
            resources.add(toFintResource(result));
        }
        return resources;
    }

 */


}
