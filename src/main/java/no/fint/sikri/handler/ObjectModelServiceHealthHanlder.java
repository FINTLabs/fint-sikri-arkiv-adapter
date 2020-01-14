package no.fint.sikri.handler;

import no.fint.event.model.Event;
import no.fint.model.resource.FintLinks;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
public class ObjectModelServiceHealthHanlder implements Handler {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Override
    public Set<String> actions() {
        return Collections.emptySet();
    }

    @Override
    public boolean health() {
        return sikriObjectModelService.isHealty();
    }

    @Override
    public void accept(Event<FintLinks> fintLinksEvent) {

    }
}
