package no.novari.fint.sikri.handler.samferdsel;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.novari.fint.model.arkiv.samferdsel.SamferdselActions;
import no.novari.fint.model.resource.FintLinks;
import no.novari.fint.model.resource.arkiv.samferdsel.SoknadDrosjeloyveResource;
import no.novari.fint.sikri.data.exception.DrosjeloyveNotFoundException;
import no.novari.fint.sikri.data.samferdsel.SoknadDrosjeloyveFactory;
import no.novari.fint.sikri.data.samferdsel.SoknadDrosjeloyveService;
import no.novari.fint.sikri.handler.Handler;
import no.novari.fint.sikri.service.CaseQueryService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class GetDrosjeloyveHandler implements Handler {

    private final CaseQueryService caseQueryService;
    private final SoknadDrosjeloyveFactory soknadDrosjeloyveFactory;
    private final SoknadDrosjeloyveService soknadDrosjeloyveService;

    public GetDrosjeloyveHandler(SoknadDrosjeloyveFactory soknadDrosjeloyveFactory, CaseQueryService caseQueryService, SoknadDrosjeloyveService soknadDrosjeloyveService) {
        this.soknadDrosjeloyveFactory = soknadDrosjeloyveFactory;
        this.caseQueryService = caseQueryService;
        this.soknadDrosjeloyveService = soknadDrosjeloyveService;
    }

    @Override
    public void accept(Event<FintLinks> response) {
        String query = response.getQuery();
        response.getData().clear();
        String[] split = query.split("/");
        try {
            if (split[0].equalsIgnoreCase("systemid")) {
                SoknadDrosjeloyveResource drosjeloyve = soknadDrosjeloyveService.getDrosjeloyveBySystemId(split[1]);
                response.addData(drosjeloyve);
                response.setResponseStatus(ResponseStatus.ACCEPTED);
            } else if (split[0].equalsIgnoreCase("mappeid")) {
                SoknadDrosjeloyveResource drosjeloyve = soknadDrosjeloyveService.getDrosjeloyveByMappeId(split[1], split[2]);
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
        return Collections.singleton(SamferdselActions.GET_SOKNADDROSJELOYVE.name());
    }
}
