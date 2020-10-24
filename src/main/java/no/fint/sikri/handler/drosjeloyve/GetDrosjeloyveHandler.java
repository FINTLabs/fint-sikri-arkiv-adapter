package no.fint.sikri.handler.drosjeloyve;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.arkiv.samferdsel.SamferdselActions;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.arkiv.samferdsel.DrosjeloyveResource;
import no.fint.sikri.data.drosjeloyve.DrosjeloyveFactory;
import no.fint.sikri.data.drosjeloyve.DrosjeloyveService;
import no.fint.sikri.data.exception.DrosjeloyveNotFoundException;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.service.CaseQueryService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class GetDrosjeloyveHandler implements Handler {

    private final CaseQueryService caseQueryService;
    private final DrosjeloyveFactory drosjeloyveFactory;
    private final DrosjeloyveService drosjeloyveService;

    public GetDrosjeloyveHandler(DrosjeloyveFactory drosjeloyveFactory, CaseQueryService caseQueryService, DrosjeloyveService drosjeloyveService) {
        this.drosjeloyveFactory = drosjeloyveFactory;
        this.caseQueryService = caseQueryService;
        this.drosjeloyveService = drosjeloyveService;
    }

    @Override
    public void accept(Event<FintLinks> response) {
        String query = response.getQuery();
        response.getData().clear();
        String[] split = query.split("/");
        try {
            if (split[0].toLowerCase().equals("systemid")) {
                DrosjeloyveResource drosjeloyve = drosjeloyveService.getDrosjeloyveBySystemId(split[1]);
                response.addData(drosjeloyve);
                response.setResponseStatus(ResponseStatus.ACCEPTED);
            } else if (split[0].toLowerCase().equals("mappeid")) {
                DrosjeloyveResource drosjeloyve = drosjeloyveService.getDrosjeloyveByMappeId(split[1], split[2]);
                response.addData(drosjeloyve);
                response.setResponseStatus(ResponseStatus.ACCEPTED);
            } else {
                throw new DrosjeloyveNotFoundException();
            }

        } catch (DrosjeloyveNotFoundException e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setStatusCode("NOT_FOUND");
            response.setMessage("No Drosjeløyve søknad found for query: " + query);
        }
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(SamferdselActions.GET_DROSJELOYVE.name());
    }
}
