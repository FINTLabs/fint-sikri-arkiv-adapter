package no.fint.sikri.data.personal;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.model.resource.administrasjon.personal.PersonalmappeResource;
import no.fint.sikri.data.exception.GetPersonalmappeNotFoundException;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.exception.UnableToGetIdFromLink;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Optional;

import static no.fint.sikri.data.utilities.SikriUtils.getIdFromLink;

@Slf4j
@Service
public class PersonalmappeService {

    @Autowired
    private PersonalmappeFactory personalmappeFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Value("${fint.sikri.defaults.casetype.peronalmappe.saksstatus-avsluttet-id:A}")
    String saksstatusAvsluttetId;

    @Value("${fint.sikri.defaults.casetype.peronalmappe.saksmappetype:P}")
    String saksmappeType;

    public PersonalmappeResource getPersonalmappeCaseByMappeId(String mappeId) throws IllegalCaseNumberFormat, GetPersonalmappeNotFoundException {
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.CASE,
                "SequenceNumber=" + NOARKUtils.getCaseSequenceNumber(mappeId)
                        + " AND CaseYear=" + NOARKUtils.getCaseYear(mappeId)
                        + " AND FileTypeId=" + saksmappeType)
                .stream()
                .map(CaseType.class::cast)
                .map(personalmappeFactory::toFintResource)
                .findAny()
                .orElseThrow(() -> new GetPersonalmappeNotFoundException(mappeId));
    }

    public PersonalmappeResource getTilskuddFartoyCaseBySystemId(String systemId) throws GetPersonalmappeNotFoundException {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CASE, "Id=" + systemId)
                .stream()
                .map(CaseType.class::cast)
                .map(personalmappeFactory::toFintResource)
                .findAny()
                .orElseThrow(() -> new GetPersonalmappeNotFoundException(systemId));

    }

    public PersonalmappeResource updateTilskuddFartoyCase(String caseNumber, PersonalmappeResource personalmappeResource) {
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
                        + " AND CaseStatusId<>" + saksstatusAvsluttetId
                        + " AND FileTypeId=" + saksmappeType)
                .stream()
                .map(CaseType.class::cast)
                .map(personalmappeFactory::toFintResource)
                .findAny();
    }
}
