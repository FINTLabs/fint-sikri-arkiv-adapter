package no.fint.sikri.handler.drosjeloyve;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.model.arkiv.samferdsel.SamferdselActions;
import no.fint.model.resource.FintLinks;
import no.fint.sikri.handler.Handler;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class GetDrosjeloyveHandler implements Handler {
    @Override
    public void accept(Event<FintLinks> fintLinksEvent) {
        log.info("Handle GET_DROSJELOYVE");
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(SamferdselActions.GET_DROSJELOYVE.name());
    }
}
