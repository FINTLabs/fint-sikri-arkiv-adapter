package no.fint.sikri.handler.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.administrasjon.arkiv.ArkivActions;
import no.fint.model.resource.FintLinks;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.noark.sak.SakFactory;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.service.CaseQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class GetSakHandler implements Handler {

    @Autowired
    private CaseQueryService caseQueryService;

    @Autowired
    private SakFactory sakFactory;

    @Override
    public void accept(Event<FintLinks> response) {
        String query = response.getQuery();
        try {
            response.getData().clear();
            if (!caseQueryService.isValidQuery(query)) {
                throw new IllegalArgumentException("Invalid query: " + query);
            }
            caseQueryService
                    .query(query)
                    .map(sakFactory::toFintResource)
                    .forEach(response::addData);
            response.setResponseStatus(ResponseStatus.ACCEPTED);
            if (response.getData().isEmpty()) {
                response.setResponseStatus(ResponseStatus.REJECTED);
                response.setStatusCode("NOT_FOUND");
                response.setMessage("No case found for query: " + query);
            }
        } catch (IllegalCaseNumberFormat e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        }
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(ArkivActions.GET_SAK.name());
    }

}
