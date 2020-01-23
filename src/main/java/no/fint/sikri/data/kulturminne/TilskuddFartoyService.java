package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.sikri.data.exception.*;
import no.fint.sikri.data.noark.sak.SakFactory;
import no.fint.model.resource.kultur.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TilskuddFartoyService {

    @Autowired
    private TilskuddFartoyFactory tilskuddFartoyFactory;

    @Autowired
    private SakFactory sakFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

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

    public TilskuddFartoyResource updateTilskuddFartoyCase(String caseNumber, TilskuddFartoyResource tilskuddFartoyResource) {
        throw new NotImplementedException("updateTilskuddFartoyCase");
    }
     */

    public TilskuddFartoyResource createTilskuddFartoyCase(TilskuddFartoyResource tilskuddFartoyResource) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        log.info("Create tilskudd fartøy");
        return tilskuddFartoyFactory.toFintResource((CaseType) sikriObjectModelService.createDataObject(tilskuddFartoyFactory.toSikri(tilskuddFartoyResource)));

    }
}
