package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.arkiv.kulturminnevern.TilskuddFredaBygningPrivatEieResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.noark.common.NoarkService;
import no.fint.sikri.service.CaseQueryService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TilskuddFredaBygningPrivatEieService {
    private final NoarkService noarkService;
    private final TilskuddFredaBygningPrivatEieFactory tilskuddFredaBygningPrivatEieFactory;
    private final CaseQueryService caseQueryService;
    private final CaseDefaults caseDefaults;

    public TilskuddFredaBygningPrivatEieService(NoarkService noarkService, TilskuddFredaBygningPrivatEieFactory tilskuddFredaBygningPrivatEieFactory, CaseQueryService caseQueryService, CaseDefaults caseDefaults) {
        this.noarkService = noarkService;
        this.tilskuddFredaBygningPrivatEieFactory = tilskuddFredaBygningPrivatEieFactory;
        this.caseQueryService = caseQueryService;
        this.caseDefaults = caseDefaults;
    }

    public TilskuddFredaBygningPrivatEieResource updateTilskuddFredaBygningPrivatEieCase(String query, TilskuddFredaBygningPrivatEieResource tilskuddFredaBygningPrivatEieResource) throws CaseNotFound {
        noarkService.updateCase(caseDefaults.getTilskuddfredabygningprivateie(), query, tilskuddFredaBygningPrivatEieResource);
        return caseQueryService
                .query(query)
                .map(tilskuddFredaBygningPrivatEieFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable to find updated case for query " + query));
    }

    public TilskuddFredaBygningPrivatEieResource createTilskuddFredaBygningPrivatEieCase(TilskuddFredaBygningPrivatEieResource tilskuddFredaBygningPrivatEieResource) {
        log.info("Create tilskudd fartøy");
        final CaseType caseType = noarkService.createCase(
                tilskuddFredaBygningPrivatEieFactory.toCaseType(tilskuddFredaBygningPrivatEieResource),
                tilskuddFredaBygningPrivatEieResource);
        noarkService.createExternalSystemLink(caseType.getId(), tilskuddFredaBygningPrivatEieResource.getSoknadsnummer());
        return tilskuddFredaBygningPrivatEieFactory.toFintResource(caseType);
    }
}
