package no.fint.sikri;

import no.fint.sikri.service.EventHandlerService;
import no.fint.event.model.Event;
import no.fint.model.resource.FintLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

@Autowired
    private EventHandlerService eventHandlerService;

    @PostMapping
    public Event<FintLinks> handleEvent(@RequestBody Event<FintLinks> input) {
        Event<FintLinks> response = new Event<>(input);
        eventHandlerService.getActionsHandlerMap().get(input.getAction()).accept(response);
        return response;
    }

}
