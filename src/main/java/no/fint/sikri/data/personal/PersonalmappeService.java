package no.fint.sikri.data.personal;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.CaseProperties;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.model.resource.arkiv.personal.PersonalmappeResource;
import no.fint.sikri.data.exception.*;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.SikriIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Optional;

import static no.fint.sikri.data.utilities.FintUtils.getFullnameFromPersonnavn;
import static no.fint.sikri.data.utilities.FintUtils.getIdFromLink;

@Slf4j
@Service
public class PersonalmappeService {

    @Autowired
    private PersonalmappeFactory personalmappeFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private CaseDefaults caseDefaults;

    @Autowired
    private SikriIdentityService identityService;

    private CaseProperties properties;


    @PostConstruct
    public void init() {
        properties = caseDefaults.getPersonalmappe();
    }


    private Optional<CaseType> getCaseByMappeId(String mappeId) throws IllegalCaseNumberFormat {
        return sikriObjectModelService.getDataObjects(
                identityService.getIdentityForClass(PersonalmappeResource.class),
                SikriObjectTypes.CASE,
                "SequenceNumber=" + NOARKUtils.getCaseSequenceNumber(mappeId)
                        + " AND CaseYear=" + NOARKUtils.getCaseYear(mappeId)
                        + " AND FileTypeId=" + properties.getSaksmappeType()
                        + " AND CaseStatusId<>A",
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION,
                        SikriObjectTypes.ADMINISTRATIVE_UNIT,
                        SikriObjectTypes.OFFICER_NAME))
                .stream()
                .map(CaseType.class::cast)
                .findAny();
    }

    private Optional<CaseType> getCaseBySystemId(String systemId) {
        return sikriObjectModelService.getDataObjects(
                identityService.getIdentityForClass(PersonalmappeResource.class),
                SikriObjectTypes.CASE,
                "Id=" + systemId
                        + " AND FileTypeId=" + properties.getSaksmappeType()
                        + " AND CaseStatusId<>A",
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION,
                        SikriObjectTypes.ADMINISTRATIVE_UNIT,
                        SikriObjectTypes.OFFICER_NAME))
                .stream()
                .map(CaseType.class::cast)
                .findAny();
    }

    private Optional<ClassificationType> getClassificationBySystemId(String classId) {
        return sikriObjectModelService.getDataObjects(
                identityService.getIdentityForClass(PersonalmappeResource.class),
                SikriObjectTypes.CLASSIFICATION,
                "ClassId=" + classId
                        + " AND ClassificationSystemId=FNRP")
                .stream()
                .map(ClassificationType.class::cast)
                .findAny();
    }

    public PersonalmappeResource getPersonalmappeCaseByMappeId(String mappeId) throws IllegalCaseNumberFormat, GetPersonalmappeNotFoundException {
        return getCaseByMappeId(mappeId)
                .map(personalmappeFactory::toFintResource)
                .orElseThrow(() -> new GetPersonalmappeNotFoundException(mappeId));
    }

    public PersonalmappeResource getPersonalmappeCaseBySystemId(String systemId) throws GetPersonalmappeNotFoundException {

        return getCaseBySystemId(systemId)
                .map(personalmappeFactory::toFintResource)
                .orElseThrow(() -> new GetPersonalmappeNotFoundException(systemId));
    }

    public PersonalmappeResource updatePersonalmappeByCaseNumber(String caseNumber, PersonalmappeResource personalmappeResource) throws IllegalCaseNumberFormat, UnableToGetIdFromLink, ClassificationNotFound, GetPersonalmappeNotFoundException, ClassificationIsNotPartOfPersonalFile, OfficerNotFound, AdministrativeUnitNotFound {
        CaseType caseType = getCaseByMappeId(caseNumber).orElseThrow(() -> new GetPersonalmappeNotFoundException(caseNumber));

        return updatePersonalmappe(caseType, personalmappeResource);
    }

    public PersonalmappeResource updatePersonalmappeBySystemId(String systemId, PersonalmappeResource personalmappeResource) throws GetPersonalmappeNotFoundException, UnableToGetIdFromLink, ClassificationNotFound, ClassificationIsNotPartOfPersonalFile, OfficerNotFound, AdministrativeUnitNotFound {
        CaseType caseType = getCaseBySystemId(systemId).orElseThrow(() -> new GetPersonalmappeNotFoundException(systemId));

        return updatePersonalmappe(caseType, personalmappeResource);
    }

    private PersonalmappeResource updatePersonalmappe(CaseType caseType, PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, ClassificationNotFound, ClassificationIsNotPartOfPersonalFile, GetPersonalmappeNotFoundException, OfficerNotFound, AdministrativeUnitNotFound {
        String nin = getIdFromLink(personalmappeResource.getPerson()).orElseThrow(() -> new UnableToGetIdFromLink("Finner ikke person fra " + personalmappeResource.getTittel()));
        if (!nin.equals(caseType.getPrimaryClassification().getClassId())) {
            throw new ClassificationIsNotPartOfPersonalFile(nin + " classId is not part of this personal file");
        }
        final SikriIdentity identity = identityService.getIdentityForClass(PersonalmappeResource.class);
        ClassificationType classificationType = getClassificationBySystemId(nin).orElseThrow(() -> new ClassificationNotFound(caseType.getId().toString()));

        if (needsUpdate(caseType, personalmappeResource)) {
            sikriObjectModelService.updateDataObject(identity, personalmappeFactory.toSikriUpdate(caseType, personalmappeResource));
            sikriObjectModelService.updateDataObject(identity, personalmappeFactory.toSikriUpdate(classificationType, personalmappeResource));
            return personalmappeFactory.toFintResource(
                    getCaseBySystemId(caseType.getId().toString())
                            .orElseThrow(() -> new GetPersonalmappeNotFoundException("Unable get case from Sikri after update"))
            );
        }

        return personalmappeFactory.toFintResource(caseType);
    }

    private boolean needsUpdate(CaseType caseType, PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink {
        String fullName = getFullnameFromPersonnavn(personalmappeResource.getNavn());

        try {

            return !caseType.getOfficerNameId()
                    .equals(personalmappeFactory.getOfficerId(personalmappeResource))
                    || !caseType.getAdministrativeUnitId()
                    .equals(personalmappeFactory.getAdministrativeUnitTypeIdFromArbeidssted(personalmappeResource))
                    || !caseType.getTitle().contains(fullName)
                    || caseType.getPublicTitle().contains(fullName)
                    || caseType.getPublicTitleNames().contains(fullName);

        } catch (OfficerNotFound | AdministrativeUnitNotFound e) {
            return true;
        }
    }


    public PersonalmappeResource createPersonalmappe(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, OfficerNotFound, AdministrativeUnitNotFound, GetPersonalmappeNotFoundException {
        log.info("Create personalmappe");

        final SikriIdentity identity = identityService.getIdentityForClass(PersonalmappeResource.class);

        CaseType caseType = personalmappeFactory.toSikri(personalmappeResource);
        DataObject caseResponse = sikriObjectModelService.createDataObject(identity, caseType);
        Integer caseId = ((CaseType) caseResponse).getId();

        ClassificationType classificationType = personalmappeFactory.createClassificationType(personalmappeResource, caseId);
        sikriObjectModelService.createDataObject(identity, classificationType);

        return personalmappeFactory.toFintResource(
                getCaseBySystemId(caseId.toString())
                        .orElseThrow(() -> new GetPersonalmappeNotFoundException("Unable get case from Sikri after update"))
        );

    }

    public Optional<PersonalmappeResource> personalmappeExists(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink {
        return sikriObjectModelService.getDataObjects(
                identityService.getIdentityForClass(PersonalmappeResource.class),
                SikriObjectTypes.CASE,
                "PrimaryClassification.ClassId=" + getIdFromLink(personalmappeResource.getPerson()).orElseThrow(IllegalStateException::new)
                        + " AND FileTypeId=" + properties.getSaksmappeType()
                        + " AND CaseStatusId<>A",
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION,
                        SikriObjectTypes.ADMINISTRATIVE_UNIT,
                        SikriObjectTypes.OFFICER_NAME))
                .stream()
                .map(CaseType.class::cast)
                .map(personalmappeFactory::toFintResource)
                .findAny();
    }
}
