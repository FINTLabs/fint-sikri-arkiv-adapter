package no.fint.documaster.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.fint.documaster.data.exception.*;
import no.fint.documaster.data.noark.sak.SakFactory;
import no.fint.documaster.service.Noark5WebService;
import no.fint.model.resource.kultur.kulturminnevern.TilskuddFartoyResource;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private Noark5WebService noark5WebService;

    public TilskuddFartoyResource getTilskuddFartoyCaseByMappeId(String mappeId) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromMappeId(mappeId);
        return tilskuddFartoyFactory.toFintResourceList(noark5WebService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(mappeId));
    }

    public TilskuddFartoyResource getTilskuddFartoyCaseBySoknadsnummer(String soknadsnummer) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.createQueryInput("refEksternId.eksternID", soknadsnummer);
        return tilskuddFartoyFactory.toFintResourceList(noark5WebService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(soknadsnummer));
    }

    public TilskuddFartoyResource getTilskuddFartoyCaseBySystemId(String systemId) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromSystemId(systemId);
        return tilskuddFartoyFactory.toFintResourceList(noark5WebService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(systemId));
    }

    public List<TilskuddFartoyResource> searchTilskuddFartoyCaseByQueryParams(Map<String, Object> query) throws GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromQueryParams(query);
        return tilskuddFartoyFactory.toFintResourceList(noark5WebService.query(queryInput));
    }

    public TilskuddFartoyResource updateTilskuddFartoyCase(String caseNumber, TilskuddFartoyResource tilskuddFartoyResource) {
        throw new NotImplementedException("updateTilskuddFartoyCase");
    }

    public TilskuddFartoyResource createTilskuddFartoyCase(TilskuddFartoyResource tilskuddFartoyResource) {
        throw new NotImplementedException("createTilskuddFartoyCase");
    }
}
