package no.fint.sikri.data.personal;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.model.resource.administrasjon.personal.PersonalmappeResource;
import no.fint.sikri.data.exception.UnableToGetIdFromLink;
import no.fint.sikri.data.noark.sak.SakFactory;
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
    private SakFactory sakFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Value("${fint.sikri.defaults.casetype.peronalmappe.saksstatus-avsluttet-id:A}")
    String saksstatusAvsluttetId;

    /*
    public TilskuddFartoyResource getTilskuddFartoyCaseByMappeId(String mappeId) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromMappeId(mappeId);
        return tilskuddFartoyFactory.toFintResourceList(sikriObjectModelService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(mappeId));
    }

    public TilskuddFartoyResource getTilskuddFartoyCaseBySoknadsnummer(String soknadsnummer) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.createQueryInput("refEksternId.eksternID", soknadsnummer);
        return tilskuddFartoyFactory.toFintResourceList(sikriObjectModelService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(soknadsnummer));
    }

    public TilskuddFartoyResource getTilskuddFartoyCaseBySystemId(String systemId) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromSystemId(systemId);
        return tilskuddFartoyFactory.toFintResourceList(sikriObjectModelService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(systemId));
    }

    public List<TilskuddFartoyResource> searchTilskuddFartoyCaseByQueryParams(Map<String, Object> query) throws GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromQueryParams(query);
        return tilskuddFartoyFactory.toFintResourceList(sikriObjectModelService.query(queryInput));
    }

     */
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
                        + " AND FileTypeId=P")
                .stream()
                .map(CaseType.class::cast)
                .map(personalmappeFactory::toFintResource)
                .findAny();

    }
}
