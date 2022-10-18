package no.fint.sikri.data.noark.sak;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.CaseProperties;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.arkiv.noark.SakResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.noark.common.NoarkService;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.SikriIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SakService {

    @Autowired
    private CaseQueryService caseQueryService;

    @Autowired
    private SakFactory sakFactory;

    @Autowired
    private NoarkService noarkService;

    @Autowired
    private CaseDefaults caseDefaults;

    @Autowired
    private SikriIdentityService identityService;

    /*
    public SakResource getTilskuddFartoyCaseByMappeId(String mappeId) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromMappeId(mappeId);
        return sakFactory.toFintResourceList(sikriObjectModelService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(mappeId));
    }

    public SakResource getTilskuddFartoyCaseBySoknadsnummer(String soknadsnummer) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.createQueryInput("refEksternId.eksternID", soknadsnummer);
        return sakFactory.toFintResourceList(sikriObjectModelService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(soknadsnummer));
    }

    public SakResource getTilskuddFartoyCaseBySystemId(String systemId) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromSystemId(systemId);
        return sakFactory.toFintResourceList(sikriObjectModelService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(systemId));
    }

    public List<SakResource> searchTilskuddFartoyCaseByQueryParams(Map<String, Object> query) throws GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromQueryParams(query);
        return sakFactory.toFintResourceList(sikriObjectModelService.query(queryInput));
    }

    public SakResource updateTilskuddFartoyCase(String caseNumber, SakResource sakResource) {
        throw new NotImplementedException("updateTilskuddFartoyCase");
    }
     */

    public SakResource createGenericCase(SakResource sakResource) {
        SikriIdentity identity = identityService.getIdentityForClass(SakResource.class);
        final CaseType caseType = noarkService.createCase(
                identity,
                sakFactory.toCaseType(sakResource),
                sakResource);
        return sakFactory.toFintResource(caseType);

    }

    public SakResource updateGenericCase(String query, SakResource sakResource) throws CaseNotFound {
        SikriIdentity identity = identityService.getIdentityForClass(SakResource.class);
        noarkService.updateCase(identity, new CaseProperties(), query, sakResource);
        return caseQueryService
                .query(identity, query)
                .map(sakFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable to find updated case for query " + query));
    }

}
