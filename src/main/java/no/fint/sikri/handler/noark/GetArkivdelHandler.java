package no.fint.sikri.handler.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.arkiv.noark.NoarkActions;
import no.fint.model.resource.FintLinks;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.noark.arkivdel.ArkivdelService;
import no.fint.sikri.data.noark.sak.SakFactory;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.service.CaseQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

@Slf4j
@Service
public class GetArkivdelHandler implements Handler {

    @Autowired
    private ArkivdelService arkivdelService;

    @Override
    public void accept(Event<FintLinks> response) {
        response.setData(new LinkedList<>());
        arkivdelService.getArkivdeler().forEach(response::addData);
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(NoarkActions.GET_ALL_ARKIVDEL.name());
    }

}
