package no.fint.sikri.data.personal;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.*;
import no.fint.model.administrasjon.organisasjon.Organisasjonselement;
import no.fint.model.administrasjon.personal.Personalressurs;
import no.fint.model.felles.Person;
import no.fint.model.felles.kompleksedatatyper.Personnavn;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.personal.PersonalmappeResource;
import no.fint.sikri.CaseDefaults;
import no.fint.sikri.data.CaseProperties;
import no.fint.sikri.data.exception.AdministrativeUnitNotFound;
import no.fint.sikri.data.exception.OfficerNotFound;
import no.fint.sikri.data.exception.UnableToGetIdFromLink;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static no.fint.sikri.data.utilities.FintUtils.getIdFromLink;
import static no.fint.sikri.data.utilities.SikriUtils.applyParameterFromLink;


@Slf4j
@Service
public class PersonalmappeFactory {


    @Autowired
    private NoarkFactory noarkFactory;

    @Autowired
    private PersonalmappeDefaults personalmappeDefaults;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private CaseDefaults caseDefaults;

    private ObjectFactory objectFactory;
    private CaseProperties properties;

    @PostConstruct
    public void init() {
        properties = caseDefaults.getCasetype().get("personalmappe");
        objectFactory = new ObjectFactory();
    }

    public CaseType toSikri(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, AdministrativeUnitNotFound, OfficerNotFound {
        CaseType caseType = objectFactory.createCaseType();
        personalmappeDefaults.applyDefaultsToCaseType(personalmappeResource, caseType);

        String fullName = FintUtils.getFullnameFromPersonnavn(personalmappeResource.getNavn());

        caseType.setTitle(objectFactory.createCaseTypeTitle("Personalmappe - " + fullName));
        caseType.setAccessCodeId(objectFactory.createCaseTypeAccessCodeId(properties.getTilgangskode()));
        caseType.setFileTypeId(objectFactory.createCaseTypeFileTypeId(properties.getSaksmappeType()));
        caseType.setSeriesId(objectFactory.createCaseTypeSeriesId(properties.getArkivdel()));
        caseType.setRegistryManagementUnitId(objectFactory.createCaseTypeRegistryManagementUnitId(properties.getJournalenhet()));
        applyParameterFromLink(
                personalmappeResource.getSaksstatus(),
                s -> objectFactory.createCaseTypeCaseStatusId(s),
                caseType::setCaseStatusId
        );
        caseType.setAdministrativeUnitId(objectFactory.createCaseTypeAdministrativeUnitId(getAdministrativeUnitTypeIdFromArbeidssted(personalmappeResource)));
        caseType.setOfficerNameId(objectFactory.createCaseTypeOfficerNameId(getOfficerId(personalmappeResource)));

        return caseType;
    }

    public CaseType toSikriUpdate(CaseType caseType, PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, AdministrativeUnitNotFound, OfficerNotFound {
        String fullName = FintUtils.getFullnameFromPersonnavn(personalmappeResource.getNavn());

        caseType.setTitle(objectFactory.createCaseTypeTitle("Personalmappe - " + fullName));

        //try {
            caseType.setAdministrativeUnitId(objectFactory.createCaseTypeAdministrativeUnitId(getAdministrativeUnitTypeIdFromArbeidssted(personalmappeResource)));
            caseType.setOfficerNameId(objectFactory.createCaseTypeOfficerNameId(getOfficerId(personalmappeResource)));
        //} catch (AdministrativeUnitNotFound | OfficerNotFound | UnableToGetIdFromLink e) {
        //    caseType.setAdministrativeUnitId(objectFactory.createCaseTypeAdministrativeUnitId(properties.getUfordeltAdministrativEnhet()));
        //    caseType.setOfficerNameId(objectFactory.createCaseTypeOfficerNameId(properties.getUfordeltSaksbehandler()));
        //}

        return caseType;
    }

    public ClassificationType toSikriUpdate(ClassificationType classificationType, PersonalmappeResource personalmappeResource) {
        String fullName = FintUtils.getFullnameFromPersonnavn(personalmappeResource.getNavn());

        classificationType.setDescription(objectFactory.createClassificationTypeDescription(fullName));

        return classificationType;
    }

    public Integer getAdministrativeUnitTypeIdFromArbeidssted(PersonalmappeResource personalmappeResource) throws AdministrativeUnitNotFound {
        return getIdFromLink(personalmappeResource.getArbeidssted()).map(this::getAdministrativeUnitTypeId).orElseThrow(() -> new AdministrativeUnitNotFound("Finner ikke arbeidssted for " + personalmappeResource.getTittel()));
    }

    private Integer getAdministrativeUnitTypeId(String shortCodeThisLevel) throws AdministrativeUnitNotFound {

        List<DataObject> dataObjects = sikriObjectModelService.getDataObjects(
                SikriObjectTypes.ADMINISTRATIVE_UNIT,
                "ShortCodeThisLevel=" + shortCodeThisLevel
                + " AND ClosedDate=@"
        );

        if (dataObjects.size() > 1) {
            throw new AdministrativeUnitNotFound( shortCodeThisLevel + " har flere administrative enheter.");
        }
        if (dataObjects.size() == 0) {
            throw new AdministrativeUnitNotFound("Finner ikke administrativ enhet med kode " + shortCodeThisLevel);
        }

        return ((AdministrativeUnitType) dataObjects.get(0)).getId();
    }

    public Integer getOfficerId(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, OfficerNotFound {
        String officerUserId = getIdFromLink(personalmappeResource.getLeder()).orElseThrow(() -> new UnableToGetIdFromLink("Finner ikke leder fra " + personalmappeResource.getTittel()));
        List<DataObject> dataObjects = sikriObjectModelService.getDataObjects(
                SikriObjectTypes.USER,
                "UserId=" + officerUserId,
                Collections.singletonList("CurrentUserName")
        );
        if (dataObjects.size() != 1) {
            throw new OfficerNotFound("Finner ikke leder (saksbehandler) med brukernavn " + officerUserId);
        }
        return ((UserType) dataObjects.get(0)).getCurrentUserName().getValue().getId();

    }

    public ClassificationType createClassificationType(PersonalmappeResource personalmappeResource, Integer caseId) throws UnableToGetIdFromLink {
        String fullName = FintUtils.getFullnameFromPersonnavn(personalmappeResource.getNavn());

        ClassificationType classificationType = objectFactory.createClassificationType();
        getIdFromLink(personalmappeResource.getPerson()).map(objectFactory::createClassificationTypeClassId).ifPresent(classificationType::setClassId);
        classificationType.setClassId(objectFactory.createClassificationTypeClassId(getIdFromLink(personalmappeResource.getPerson()).orElseThrow(() -> new UnableToGetIdFromLink("Finner ikke person fra " + personalmappeResource.getTittel()))));
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

    private Personnavn getPersonnavnFromTitle(String title) {
        String name = StringUtils.substringAfter(title, "-").trim();
        String firstName = StringUtils.substringAfter(name, " ");
        String lastName = StringUtils.substringBefore(name, " ");
        Personnavn personnavn = new Personnavn();
        personnavn.setFornavn(firstName);
        personnavn.setEtternavn(lastName);
        return personnavn;
    }
    public PersonalmappeResource toFintResource(CaseType input) {


        PersonalmappeResource personalmappe = noarkFactory.applyValuesForSaksmappe(input, new PersonalmappeResource());

        personalmappe.setNavn(getPersonnavnFromTitle(input.getTitle().getValue()));

        personalmappe.addArbeidssted(Link.with(
                Organisasjonselement.class,
                "systemid",
                input.getAdministrativeUnit()
                        .getValue()
                        .getShortCodeThisLevel()
                        .getValue()
        ));

        personalmappe.addLeder(Link.with(
                Personalressurs.class,
                "brukernavn",
                input.getOfficerName()
                        .getValue()
                        .getInitials()
                        .getValue()
        ));

        personalmappe.addPerson(Link.with(
                Person.class,
                "fodselsnummer",
                input.getPrimaryClassification()
                        .getValue()
                        .getClassId()
                        .getValue()
        ));
        personalmappe.addSelf(
                Link.with(
                        PersonalmappeResource.class,
                        "mappeid",
                        NOARKUtils.getMappeId(
                                input.getCaseYear().getValue().toString(),
                                input.getSequenceNumber().getValue().toString()
                        )
                )
        );
        personalmappe.addSelf(
                Link.with(
                        PersonalmappeResource.class,
                        "systemid",
                        input.getId().toString()
                )
        );

        return personalmappe;
    }
}
