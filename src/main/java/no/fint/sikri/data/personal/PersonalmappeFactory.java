package no.fint.sikri.data.personal;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.*;
import no.fint.model.resource.administrasjon.personal.PersonalmappeResource;
import no.fint.sikri.data.exception.AdministrativeUnitNotFound;
import no.fint.sikri.data.exception.OfficerNotFound;
import no.fint.sikri.data.exception.UnableToGetIdFromLink;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static no.fint.sikri.data.utilities.SikriUtils.getIdFromLink;


@Slf4j
@Service
public class PersonalmappeFactory {


    @Autowired
    private NoarkFactory noarkFactory;


    @Autowired
    private PersonalmappeDefaults personalmappeDefaults;


    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Value("${fint.sikri.defaults.casetype.personalmappe.tilgangskode:P}")
    String tilgangKode;

    @Value("${fint.sikri.defaults.casetype.peronalmappe.saksmappetype:P}")
    String sakmappeType;

    @Value("${fint.sikri.defaults.casetype.peronalmappe.saks-status:B}")
    String saksStatus;

    @Value("${fint.sikri.defaults.casetype.peronalmappe.arkivdel:PERS}")
    String arkivDel;

    @Value("${fint.sikri.defaults.casetype.peronalmappe.journalenhet:VFRÅD}")
    String journalenhet;

    @Value("${fint.sikri.defaults.casetype.peronalmappe.administrativenhet:28}")
    Integer ufordeltAdministrativEnhet;

    @Value("${fint.sikri.defaults.casetype.peronalmappe.saksbehandler:0}")
    Integer ufordeltSaksbehanlder;

    private ObjectFactory objectFactory;

    public PersonalmappeFactory() {
        objectFactory = new ObjectFactory();
    }

    public CaseType toSikri(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink {
        CaseType caseType = objectFactory.createCaseType();
        personalmappeDefaults.applyDefaultsToCaseType(personalmappeResource, caseType);

        String fullName = FintUtils.getFullnameFromPersonnavn(personalmappeResource.getNavn());

        caseType.setTitle(objectFactory.createCaseTypeTitle("Personalmappe - " + fullName));
        caseType.setAccessCodeId(objectFactory.createCaseTypeAccessCodeId(tilgangKode));
        caseType.setFileTypeId(objectFactory.createCaseTypeFileTypeId(sakmappeType));
        caseType.setCaseStatusId(objectFactory.createCaseTypeCaseStatusId(saksStatus));
        caseType.setSeriesId(objectFactory.createCaseTypeSeriesId(arkivDel));
        caseType.setRegistryManagementUnitId(objectFactory.createCaseTypeRegistryManagementUnitId(journalenhet));

        try {
            caseType.setAdministrativeUnitId(objectFactory.createCaseTypeAdministrativeUnitId(getAdministrativeUnitTypeIdFromArbeidssted(personalmappeResource)));
            caseType.setOfficerNameId(objectFactory.createCaseTypeOfficerNameId(getOfficerId(personalmappeResource)));
        } catch (AdministrativeUnitNotFound | OfficerNotFound e) {
            caseType.setAdministrativeUnitId(objectFactory.createCaseTypeAdministrativeUnitId(ufordeltAdministrativEnhet));
            caseType.setOfficerNameId(objectFactory.createCaseTypeOfficerNameId(ufordeltSaksbehanlder));
        }

        return caseType;
    }

    private Integer getAdministrativeUnitTypeIdFromArbeidssted(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, AdministrativeUnitNotFound {
        return getAdministrativeUnitTypeId(getIdFromLink(personalmappeResource.getArbeidssted()));
    }

    private Integer getAdministrativeUnitTypeId(String shortCodeThisLevel) throws AdministrativeUnitNotFound {

        List<DataObject> dataObjects = sikriObjectModelService.getDataObjects(
                SikriObjectTypes.ADMINISTRATIVE_UNIT,
                "ShortCodeThisLevel=" + shortCodeThisLevel
        );

        if (dataObjects.size() != 1) {
            throw new AdministrativeUnitNotFound(shortCodeThisLevel);
        }
        return ((AdministrativeUnitType) dataObjects.get(0)).getId();
    }

    private Integer getOfficerId(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, OfficerNotFound {
        String officerUserId = getIdFromLink(personalmappeResource.getLeder());
        List<DataObject> dataObjects = sikriObjectModelService.getDataObjects(
                SikriObjectTypes.USER,
                "UserId=" + officerUserId,
                Collections.singletonList("CurrentUserName")
        );
        if (dataObjects.size() != 1) {
            throw new OfficerNotFound(officerUserId);
        }
        return ((UserType) dataObjects.get(0)).getCurrentUserName().getValue().getId();

    }

    public ClassificationType createClassificationType(PersonalmappeResource personalmappeResource, Integer caseId) throws UnableToGetIdFromLink {
        String fullName = FintUtils.getFullnameFromPersonnavn(personalmappeResource.getNavn());

        ClassificationType classificationType = objectFactory.createClassificationType();
        classificationType.setClassId(objectFactory.createClassificationTypeClassId(getIdFromLink(personalmappeResource.getPerson())));
        classificationType.setClassificationSystemId(objectFactory.createClassificationTypeClassificationSystemId("FNRP"));
        classificationType.setDescription(objectFactory.createClassificationTypeDescription(fullName));
        classificationType.setCaseId(caseId);
        classificationType.setIsRestricted(objectFactory.createClassificationTypeIsRestricted(true));
        classificationType.setSortOrder(objectFactory.createClassificationTypeSortOrder("1"));

        return classificationType;
    }


    @SuppressWarnings("unchecked")
    private JAXBElement<String> createValue(String attribute, String value) throws InvocationTargetException, IllegalAccessException {
        Method method = BeanUtils.findMethodWithMinimalParameters(objectFactory.getClass(), "createCaseType" + StringUtils.capitalize(attribute));

        return (JAXBElement<String>) method.invoke(objectFactory, value);
    }

    public PersonalmappeResource toFintResource(CaseType input) {
//        if (input.getFields().getVirksomhetsspesifikkeMetadata() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getKulturminneid() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getFartoynavn() == null) {
//            throw new NotTilskuddfartoyException(input.getFields().getMappeIdent());
//        }

        PersonalmappeResource personalmappe = noarkFactory.applyValuesForSaksmappe(input, new PersonalmappeResource());

//        tilskuddFartoy.setFartoyNavn(input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getFartoynavn().getValues().get(0));
//        tilskuddFartoy.setKallesignal(input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getKallesignal().getValues().get(0));
//        tilskuddFartoy.setSoknadsnummer(createIdentifikator(input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getSoeknadsnummer().getValues().get(0)));
//        tilskuddFartoy.setKulturminneId(input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getKulturminneid().getValues().get(0));

//        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "soknadsnummer", tilskuddFartoy.getSoknadsnummer().getIdentifikatorverdi()));
//        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "mappeid", input.getFields().getMappeIdent()));
//        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "systemid", input.getId()));

        return personalmappe;
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
