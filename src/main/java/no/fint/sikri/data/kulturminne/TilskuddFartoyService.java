package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.arkiv.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.noark.common.NoarkService;
import no.fint.sikri.service.CaseQueryService;
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

    public TilskuddFartoyResource createTilskuddFartoyCase(TilskuddFartoyResource tilskuddFartoyResource) {
        log.info("Create tilskudd fartøy");
        final CaseType caseType = noarkService.createCase(
                tilskuddFartoyFactory.toCaseType(tilskuddFartoyResource),
                tilskuddFartoyResource);
        noarkService.createExternalSystemLink(caseType.getId(), tilskuddFartoyResource.getSoknadsnummer());
        return tilskuddFartoyFactory.toFintResource(caseType);

    }

    public TilskuddFartoyResource updateTilskuddFartoyCase(String query, TilskuddFartoyResource tilskuddFartoyResource) throws CaseNotFound {
        noarkService.updateCase(query, tilskuddFartoyResource);
        return caseQueryService
                .query(query)
                .map(tilskuddFartoyFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable to find updated case for query " + query));
    }

}
