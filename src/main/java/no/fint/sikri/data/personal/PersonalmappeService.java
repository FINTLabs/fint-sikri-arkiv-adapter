package no.fint.sikri.data.personal;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.model.resource.administrasjon.personal.PersonalmappeResource;
import no.fint.sikri.CaseDefaults;
import no.fint.sikri.data.CaseProperties;
import no.fint.sikri.data.exception.*;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
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

    private CaseProperties properties;


    @PostConstruct
    public void init() {
        properties = caseDefaults.getCasetype().get("personalmappe");
    }


    private Optional<CaseType> getCaseByMappeId(String mappeId) throws IllegalCaseNumberFormat {
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.CASE,
                "SequenceNumber=" + NOARKUtils.getCaseSequenceNumber(mappeId)
                        + " AND CaseYear=" + NOARKUtils.getCaseYear(mappeId)
                        + " AND FileTypeId=" + properties.getSaksmappeType()
                        + " AND CaseStatusId<>" + properties.getSaksStatusAvsluttetId(),
                Collections.singletonList(SikriObjectTypes.PRIMARY_CLASSIFICATION))
                .stream()
                .map(CaseType.class::cast)
                .findAny();
    }

    private Optional<CaseType> getCaseBySystemId(String systemId) {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CASE,
                "Id=" + systemId
                        + " AND FileTypeId=" + properties.getSaksmappeType()
                        + " AND CaseStatusId<>" + properties.getSaksStatusAvsluttetId(),
                Collections.singletonList(SikriObjectTypes.PRIMARY_CLASSIFICATION))
                .stream()
                .map(CaseType.class::cast)
                .findAny();
    }

    private Optional<ClassificationType> getClassificationBySystemId(String classId) {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CLASSIFICATION,
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

    public PersonalmappeResource updatePersonalmappeByCaseNumber(String caseNumber, PersonalmappeResource personalmappeResource) throws IllegalCaseNumberFormat, UnableToGetIdFromLink, ClassificationNotFound, GetPersonalmappeNotFoundException, ClassificationIsNotPartOfPersonalFile {
        CaseType caseType = getCaseByMappeId(caseNumber).orElseThrow(() -> new GetPersonalmappeNotFoundException(caseNumber));

        return updatePersonalmappe(caseType, personalmappeResource);
    }

    public PersonalmappeResource updatePersonalmappeBySystemId(String systemId, PersonalmappeResource personalmappeResource) throws GetPersonalmappeNotFoundException, UnableToGetIdFromLink, ClassificationNotFound, ClassificationIsNotPartOfPersonalFile {
        CaseType caseType = getCaseBySystemId(systemId).orElseThrow(() -> new GetPersonalmappeNotFoundException(systemId));

        return updatePersonalmappe(caseType, personalmappeResource);
    }

    private PersonalmappeResource updatePersonalmappe(CaseType caseType, PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, ClassificationNotFound, ClassificationIsNotPartOfPersonalFile {
        String nin = getIdFromLink(personalmappeResource.getPerson());
        if (!nin.equals(caseType.getPrimaryClassification().getValue().getClassId().getValue())) {
            throw new ClassificationIsNotPartOfPersonalFile(nin + " classId is not part of this personal file");
        }
        ClassificationType classificationType = getClassificationBySystemId(nin).orElseThrow(() -> new ClassificationNotFound(caseType.getId().toString()));

        if (needsUpdate(caseType, personalmappeResource)) {
            DataObject dataObject = sikriObjectModelService.updateDataObject(personalmappeFactory.toSikriUpdate(caseType, personalmappeResource));
            sikriObjectModelService.updateDataObject(personalmappeFactory.toSikriUpdate(classificationType, personalmappeResource));
            return personalmappeFactory.toFintResource((CaseType) dataObject);
        }

        return personalmappeFactory.toFintResource(caseType);
    }

    private boolean needsUpdate(CaseType caseType, PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink {
        String fullName = getFullnameFromPersonnavn(personalmappeResource.getNavn());

        try {

            return !caseType.getOfficerNameId().getValue()
                    .equals(personalmappeFactory.getOfficerId(personalmappeResource))
                    || !caseType.getAdministrativeUnitId().getValue()
                    .equals(personalmappeFactory.getAdministrativeUnitTypeIdFromArbeidssted(personalmappeResource))
                    || !caseType.getTitle().getValue().contains(fullName);

        } catch (OfficerNotFound | AdministrativeUnitNotFound e) {
            return true;
        }
    }


    public PersonalmappeResource createPersonalmappe(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink, OfficerNotFound, AdministrativeUnitNotFound {
        log.info("Create personalmappe");


        CaseType caseType = personalmappeFactory.toSikri(personalmappeResource);
        DataObject caseResponse = sikriObjectModelService.createDataObject(caseType);
        Integer caseId = ((CaseType) caseResponse).getId();

        ClassificationType classificationType = personalmappeFactory.createClassificationType(personalmappeResource, caseId);
        sikriObjectModelService.createDataObject(classificationType);

        List<DataObject> dataObjects = sikriObjectModelService.getDataObjects(SikriObjectTypes.CASE, "Id=" + caseId);

        return personalmappeFactory.toFintResource((CaseType) dataObjects.get(0));

    }

    public Optional<PersonalmappeResource> personalmappeExists(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink {
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.CASE,
                "PrimaryClassification.ClassId=" + getIdFromLink(personalmappeResource.getPerson())
                        + " AND FileTypeId=" + properties.getSaksmappeType()
                        + " AND CaseStatusId<>" + properties.getSaksStatusAvsluttetId())
                .stream()
                .map(CaseType.class::cast)
                .map(personalmappeFactory::toFintResource)
                .findAny();
    }
}
