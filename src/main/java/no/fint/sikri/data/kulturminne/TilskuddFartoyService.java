package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.CaseDefaults;
import no.fint.arkiv.sikri.oms.CaseType;
import no.novari.fint.model.resource.arkiv.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.noark.common.NoarkService;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.SikriIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TilskuddFartoyService {

    @Autowired
    private CaseQueryService caseQueryService;

    @Autowired
    private TilskuddFartoyFactory tilskuddFartoyFactory;

    @Autowired
    private NoarkService noarkService;

    @Autowired
    private CaseDefaults caseDefaults;

    @Autowired
    private SikriIdentityService identityService;

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

    public TilskuddFartoyResource createTilskuddFartoyCase(TilskuddFartoyResource tilskuddFartoyResource) {
        log.info("Create tilskudd fartøy");
        SikriIdentity identity = identityService.getIdentityForClass(TilskuddFartoyResource.class);
        final CaseType caseType = noarkService.createCase(
                identity,
                tilskuddFartoyFactory.toCaseType(tilskuddFartoyResource),
                tilskuddFartoyResource);
        noarkService.createExternalSystemLink(identity, caseType.getId(), tilskuddFartoyResource.getSoknadsnummer());
        return tilskuddFartoyFactory.toFintResource(caseType);

    }

    public TilskuddFartoyResource updateTilskuddFartoyCase(String query, TilskuddFartoyResource tilskuddFartoyResource) throws CaseNotFound {
        SikriIdentity identity = identityService.getIdentityForClass(TilskuddFartoyResource.class);
        noarkService.updateCase(identity, caseDefaults.getTilskuddfartoy(), query, tilskuddFartoyResource);
        return caseQueryService
                .query(identity, query)
                .map(tilskuddFartoyFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable to find updated case for query " + query));
    }

}
