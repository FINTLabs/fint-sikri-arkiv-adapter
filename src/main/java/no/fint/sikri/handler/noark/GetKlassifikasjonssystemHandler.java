package no.fint.sikri.handler.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.model.arkiv.noark.NoarkActions;
import no.fint.model.resource.FintLinks;
import no.fint.sikri.data.noark.klasse.KlassifikasjonssystemService;
import no.fint.sikri.handler.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

@Slf4j
@Service
public class GetKlassifikasjonssystemHandler implements Handler {

    @Autowired
    private KlassifikasjonssystemService klassifikasjonssystemService;

    @Override
    public void accept(Event<FintLinks> response) {
        response.setData(new LinkedList<>());
        klassifikasjonssystemService.getKlassifikasjonssystem().forEach(response::addData);
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(NoarkActions.GET_ALL_KLASSIFIKASJONSSYSTEM.name());
    }

}
