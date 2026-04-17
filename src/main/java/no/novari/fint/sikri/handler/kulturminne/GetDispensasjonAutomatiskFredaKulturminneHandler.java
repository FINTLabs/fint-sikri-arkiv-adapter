package no.novari.fint.sikri.handler.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.novari.fint.model.arkiv.kulturminnevern.KulturminnevernActions;
import no.novari.fint.model.resource.FintLinks;
import no.novari.fint.model.resource.arkiv.kulturminnevern.DispensasjonAutomatiskFredaKulturminneResource;
import no.novari.fint.sikri.data.kulturminne.DispensasjonAutomatiskFredaKulturminneFactory;
import no.novari.fint.sikri.handler.Handler;
import no.novari.fint.sikri.service.CaseQueryService;
import no.novari.fint.sikri.service.SikriIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@Slf4j
public class GetDispensasjonAutomatiskFredaKulturminneHandler implements Handler {
    @Autowired
    private DispensasjonAutomatiskFredaKulturminneFactory dispensasjonAutomatiskFredaKulturminneFactory;

    @Autowired
    private CaseQueryService caseQueryService;

    @Autowired
    private SikriIdentityService identityService;

    @Override
    public void accept(Event<FintLinks> response) {
        String query = response.getQuery();
        response.getData().clear();

        if (!caseQueryService.isValidQuery(query)) {
            throw new IllegalArgumentException("Invalid query: " + query);
        }

        caseQueryService
                .query(identityService.getIdentityForClass(DispensasjonAutomatiskFredaKulturminneResource.class), query)
                .map(dispensasjonAutomatiskFredaKulturminneFactory::toFintResource)
                .forEach(response::addData);
        response.setResponseStatus(ResponseStatus.ACCEPTED);

        if (response.getData().isEmpty()) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setStatusCode("NOT_FOUND");
            response.setMessage("No case found for query: " + query);
        }
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(KulturminnevernActions.GET_DISPENSASJONAUTOMATISKFREDAKULTURMINNE.name());
    }
}
