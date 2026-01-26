package no.novari.fint.sikri.data.personal;

import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.CaseDefaults;
import no.fint.arkiv.sikri.oms.*;
import no.novari.fint.arkiv.CaseProperties;
import no.novari.fint.model.administrasjon.organisasjon.Organisasjonselement;
import no.novari.fint.model.administrasjon.personal.Personalressurs;
import no.novari.fint.model.felles.Person;
import no.novari.fint.model.felles.kompleksedatatyper.Personnavn;
import no.novari.fint.model.resource.Link;
import no.novari.fint.model.resource.arkiv.personal.PersonalmappeResource;
import no.novari.fint.sikri.data.exception.AdministrativeUnitNotFound;
import no.novari.fint.sikri.data.exception.OfficerNotFound;
import no.novari.fint.sikri.data.exception.UnableToGetIdFromLink;
import no.novari.fint.sikri.data.noark.common.NoarkFactory;
import no.novari.fint.sikri.data.utilities.FintUtils;
import no.novari.fint.sikri.data.utilities.NOARKUtils;
import no.novari.fint.sikri.service.SikriCaseDefaultsService;
import no.novari.fint.sikri.service.SikriIdentityService;
import no.novari.fint.sikri.service.SikriObjectModelService;
import no.novari.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static no.novari.fint.sikri.data.utilities.FintUtils.getIdFromLink;
import static no.novari.fint.sikri.data.utilities.SikriUtils.applyParameterFromLink;
import static no.novari.fint.sikri.data.utilities.SikriUtils.getMarkedTitle;


@Slf4j
@Service
public class PersonalmappeFactory {


    @Autowired
    private NoarkFactory noarkFactory;

    @Autowired
    private SikriCaseDefaultsService caseDefaultsService;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private CaseDefaults caseDefaults;

    @Autowired
    private SikriIdentityService identityService;

    private CaseProperties properties;

    @Value("${fint.sikri.case.personalmappe.skjermetNavn:false}")
    private boolean skjermetNavn;

    @PostConstruct
    public void init() {
        properties = caseDefaults.getPersonalmappe();
    }

    public CaseType toSikri(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, AdministrativeUnitNotFound, OfficerNotFound {
        CaseType caseType = new CaseType();
        caseDefaultsService.applyDefaultsToCaseType(properties, personalmappeResource, caseType);

        String fullName = FintUtils.getFullnameFromPersonnavn(personalmappeResource.getNavn());
        caseType.setTitle("Personalmappe - " + fullName);

        caseType.setPublicTitle(getPersonalmappePublicTitle(fullName, false));
        caseType.setPublicTitleNames(getPersonalmappePublicTitle(fullName, true));

        caseType.setAccessCodeId(properties.getTilgangsrestriksjon());
        caseType.setFileTypeId(properties.getSaksmappeType());
        caseType.setSeriesId(properties.getArkivdel());
        caseType.setRegistryManagementUnitId(properties.getJournalenhet());
        applyParameterFromLink(
                personalmappeResource.getSaksstatus(),
                caseType::setCaseStatusId
        );
        caseType.setAdministrativeUnitId(getAdministrativeUnitTypeIdFromArbeidssted(personalmappeResource));
        caseType.setOfficerNameId(getOfficerId(personalmappeResource));

        return caseType;
    }

    public CaseType toSikriUpdate(CaseType caseType, PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, AdministrativeUnitNotFound, OfficerNotFound {
        String fullName = FintUtils.getFullnameFromPersonnavn(personalmappeResource.getNavn());
        caseType.setTitle("Personalmappe - " + fullName);

        caseType.setPublicTitle(getPersonalmappePublicTitle(fullName, false));
        caseType.setPublicTitleNames(getPersonalmappePublicTitle(fullName, true));

        //try {
        caseType.setAdministrativeUnitId(getAdministrativeUnitTypeIdFromArbeidssted(personalmappeResource));
        caseType.setOfficerNameId(getOfficerId(personalmappeResource));
        //} catch (AdministrativeUnitNotFound | OfficerNotFound | UnableToGetIdFromLink e) {
        //    caseType.setAdministrativeUnitId(objectFactory.createCaseTypeAdministrativeUnitId(properties.getUfordeltAdministrativEnhet()));
        //    caseType.setOfficerNameId(objectFactory.createCaseTypeOfficerNameId(properties.getUfordeltSaksbehandler()));
        //}

        return caseType;
    }

    private String getPersonalmappePublicTitle(String fullName, boolean markedName) {
        String shielded = skjermetNavn ? "@" : "";
        String marked = markedName ? "#" : "";
        return getMarkedTitle(String.format("Personalmappe - %s%s%s%s%s", shielded, marked, fullName, marked, shielded));
    }

    public ClassificationType toSikriUpdate(ClassificationType classificationType, PersonalmappeResource personalmappeResource) {
        String fullName = FintUtils.getFullnameFromPersonnavn(personalmappeResource.getNavn());

        classificationType.setDescription(fullName);

        return classificationType;
    }

    public Integer getAdministrativeUnitTypeIdFromArbeidssted(PersonalmappeResource personalmappeResource) throws AdministrativeUnitNotFound {
        return getIdFromLink(personalmappeResource.getArbeidssted()).map(this::getAdministrativeUnitTypeId).orElseThrow(() -> new AdministrativeUnitNotFound("Finner ikke arbeidssted for " + personalmappeResource.getTittel()));
    }

    private Integer getAdministrativeUnitTypeId(String shortCodeThisLevel) throws AdministrativeUnitNotFound {

        List<DataObject> dataObjects = sikriObjectModelService.getDataObjects(
                identityService.getIdentityForClass(PersonalmappeResource.class),
                SikriObjectTypes.ADMINISTRATIVE_UNIT,
                "ShortCodeThisLevel=" + shortCodeThisLevel
                        + " AND ClosedDate=@"
        );

        if (dataObjects.size() > 1) {
            throw new AdministrativeUnitNotFound(shortCodeThisLevel + " har flere administrative enheter.");
        }
        if (dataObjects.size() == 0) {
            throw new AdministrativeUnitNotFound("Finner ikke administrativ enhet med kode " + shortCodeThisLevel);
        }

        return ((AdministrativeUnitType) dataObjects.get(0)).getId();
    }

    public Integer getOfficerId(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, OfficerNotFound {
        String officerUserId = getIdFromLink(personalmappeResource.getLeder()).orElseThrow(() -> new UnableToGetIdFromLink("Finner ikke leder fra " + personalmappeResource.getTittel()));
        List<DataObject> dataObjects = sikriObjectModelService.getDataObjects(
                identityService.getIdentityForClass(PersonalmappeResource.class),
                SikriObjectTypes.USER,
                "UserId=" + officerUserId,
                Collections.singletonList("CurrentUserName")
        );
        if (dataObjects.size() != 1) {
            throw new OfficerNotFound("Finner ikke leder (saksbehandler) med brukernavn " + officerUserId);
        }
        return ((UserType) dataObjects.get(0)).getCurrentUserName().getId();

    }

    public ClassificationType createClassificationType(PersonalmappeResource personalmappeResource, Integer caseId) throws UnableToGetIdFromLink {
        String fullName = FintUtils.getFullnameFromPersonnavn(personalmappeResource.getNavn());

        ClassificationType classificationType = new ClassificationType();
        getIdFromLink(personalmappeResource.getPerson()).ifPresent(classificationType::setClassId);
        classificationType.setClassId(getIdFromLink(personalmappeResource.getPerson()).orElseThrow(() -> new UnableToGetIdFromLink("Finner ikke person fra " + personalmappeResource.getTittel())));
        classificationType.setClassificationSystemId("FNRP");
        classificationType.setDescription(fullName);
        classificationType.setCaseId(caseId);
        classificationType.setIsRestricted(true);
        classificationType.setSortOrder("1");

        return classificationType;
    }


    private Personnavn getPersonnavnFromTitle(String title) {
        String name = StringUtils.substringAfter(title, "-").trim();
        String firstName = StringUtils.substringBeforeLast(name, " ");
        String lastName = StringUtils.substringAfterLast(name, " ");

        Personnavn personnavn = new Personnavn();
        personnavn.setFornavn(firstName);
        personnavn.setEtternavn(lastName);

        return personnavn;
    }

    public PersonalmappeResource toFintResource(CaseType input) {

        PersonalmappeResource personalmappe = noarkFactory.applyValuesForSaksmappe(
                identityService.getIdentityForClass(PersonalmappeResource.class),
                caseDefaults.getPersonalmappe(),
                input, new PersonalmappeResource());

        personalmappe.setNavn(getPersonnavnFromTitle(input.getTitle()));

        Optional.ofNullable(input.getAdministrativeUnit())
                .map(AdministrativeUnitType::getShortCodeThisLevel)
                .ifPresent(shortCode -> personalmappe.addArbeidssted(Link.with(
                        Organisasjonselement.class,
                        "systemid",
                        shortCode
                )));

        Optional.ofNullable(input.getOfficerName())
                .map(UserNameType::getInitials)
                .ifPresent(initials -> personalmappe.addLeder(Link.with(
                        Personalressurs.class,
                        "brukernavn",
                        initials
                )));

        Optional.ofNullable(input.getPrimaryClassification())
                .map(ClassificationType::getClassId)
                .ifPresent(classId -> personalmappe.addPerson(Link.with(
                        Person.class,
                        "fodselsnummer",
                        classId
                )));

        personalmappe.addSelf(
                Link.with(
                        PersonalmappeResource.class,
                        "mappeid",
                        NOARKUtils.getMappeId(
                                input.getCaseYear().toString(),
                                input.getSequenceNumber().toString()
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
