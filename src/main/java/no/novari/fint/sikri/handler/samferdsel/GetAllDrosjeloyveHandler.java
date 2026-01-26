package no.novari.fint.sikri.handler.samferdsel;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.novari.fint.model.arkiv.samferdsel.SamferdselActions;
import no.novari.fint.model.resource.FintLinks;
import no.novari.fint.sikri.data.samferdsel.SoknadDrosjeloyveService;
import no.novari.fint.sikri.handler.Handler;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class GetAllDrosjeloyveHandler implements Handler {

    private final SoknadDrosjeloyveService soknadDrosjeloyveService;

    public GetAllDrosjeloyveHandler(SoknadDrosjeloyveService soknadDrosjeloyveService) {
        this.soknadDrosjeloyveService = soknadDrosjeloyveService;
    }

    @Override
    public void accept(Event<FintLinks> response) {

        soknadDrosjeloyveService.getAllDrosjeloyve()
                .forEach(response::addData);

        response.setResponseStatus(ResponseStatus.ACCEPTED);

    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(SamferdselActions.GET_ALL_SOKNADDROSJELOYVE.name());
    }
}
