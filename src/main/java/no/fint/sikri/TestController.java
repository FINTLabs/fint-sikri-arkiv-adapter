package no.fint.sikri;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.event.model.Event;
import no.fint.model.arkiv.kodeverk.KodeverkActions;
import no.fint.model.resource.FintLinks;
import no.fint.sikri.handler.noark.KodeverkHandler;
import no.fint.sikri.service.EventHandlerService;
import no.fint.sikri.service.SikriIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private EventHandlerService eventHandlerService;

    @Autowired
    private SikriObjectModelService objectModelService;

    @Autowired
    private KodeverkHandler kodeverkHandler;

    @Autowired
    private SikriIdentityService identityService;

    @PostMapping
    public Event<FintLinks> handleEvent(@RequestBody Event<FintLinks> input) {
        Event<FintLinks> response = new Event<>(input);
        eventHandlerService.getActionsHandlerMap().get(input.getAction()).accept(response);
        return response;
    }

    @GetMapping(path = "dataobjects")
    public List<DataObject> getDataObjects(@RequestParam String objectName) {
        return objectModelService.getDataObjects(identityService.getDefaultIdentity(), objectName);
    }

    @GetMapping(path = "kodeverk/{navn}")
    public List<? extends FintLinks> getKodeverk(@PathVariable String navn) {
        return kodeverkHandler
                .actions()
                .stream()
                .filter(it -> StringUtils.containsIgnoreCase(it, navn))
                .map(KodeverkActions::valueOf)
                .peek(action -> log.info("Get kodeverk {}", action))
                .findFirst()
                .map(kodeverkHandler::getCodes)
                .get();
    }
}
