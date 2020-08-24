package no.fint.sikri;

import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.sikri.service.EventHandlerService;
import no.fint.event.model.Event;
import no.fint.model.resource.FintLinks;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TestController {

    @Autowired
    private EventHandlerService eventHandlerService;

    @Autowired
    private SikriObjectModelService objectModelService;

    @PostMapping
    public Event<FintLinks> handleEvent(@RequestBody Event<FintLinks> input) {
        Event<FintLinks> response = new Event<>(input);
        eventHandlerService.getActionsHandlerMap().get(input.getAction()).accept(response);
        return response;
    }

    @GetMapping(path = "dataobjects")
    public List<DataObject> getDataObjects(@RequestParam String objectName) {
        return objectModelService.getDataObjects(objectName);
    }
}
