package no.fint.sikri.data.personal;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.model.resource.administrasjon.personal.PersonalmappeResource;
import no.fint.sikri.CaseDefaults;
import no.fint.sikri.data.CaseProperties;
import no.fint.sikri.data.exception.GetPersonalmappeNotFoundException;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.exception.UnableToGetIdFromLink;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

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

    public PersonalmappeResource getPersonalmappeCaseByMappeId(String mappeId) throws IllegalCaseNumberFormat, GetPersonalmappeNotFoundException {
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.CASE,
                "SequenceNumber=" + NOARKUtils.getCaseSequenceNumber(mappeId)
                        + " AND CaseYear=" + NOARKUtils.getCaseYear(mappeId)
                        + " AND FileTypeId=" + properties.getSaksmappeType()
                        + " AND CaseStatusId<>" + properties.getSaksstatusAvsluttetId())
                .stream()
                .map(CaseType.class::cast)
                .map(personalmappeFactory::toFintResource)
                .findAny()
                .orElseThrow(() -> new GetPersonalmappeNotFoundException(mappeId));
    }

    public PersonalmappeResource getPersonalmappeCaseBySystemId(String systemId) throws GetPersonalmappeNotFoundException {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CASE,
                "Id=" + systemId
                        + " AND FileTypeId=" + properties.getSaksmappeType()
                        + " AND CaseStatusId<>" + properties.getSaksstatusAvsluttetId())
                .stream()
                .map(CaseType.class::cast)
                .map(personalmappeFactory::toFintResource)
                .findAny()
                .orElseThrow(() -> new GetPersonalmappeNotFoundException(systemId));

    }

    public PersonalmappeResource updatePersonalmappeByCaseNumber(String caseNumber, PersonalmappeResource personalmappeResource) {
        throw new NotImplementedException();
    }

    public PersonalmappeResource updatePersonalmappeBySystemId(String systemId, PersonalmappeResource personalmappeResource) {
        throw new NotImplementedException();
    }

    public PersonalmappeResource createPersonalmappe(PersonalmappeResource personalmappeResource) throws UnableToGetIdFromLink {
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
                        + " AND CaseStatusId<>" + properties.getSaksstatusAvsluttetId())
                .stream()
                .map(CaseType.class::cast)
                .map(personalmappeFactory::toFintResource)
                .findAny();
    }
}
