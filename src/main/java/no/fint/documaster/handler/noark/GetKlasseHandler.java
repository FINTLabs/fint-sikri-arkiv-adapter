package no.fint.documaster.handler.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.documaster.data.noark.klasse.KlasseService;
import no.fint.documaster.handler.Handler;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.administrasjon.arkiv.ArkivActions;
import no.fint.model.resource.FintLinks;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class GetKlasseHandler implements Handler {
    @Autowired
    private KlasseService klasseService;

    @Override
    public void accept(Event<FintLinks> response) {
        String query = response.getQuery();
        try {
            if (StringUtils.startsWithIgnoreCase(query, "systemid/")) {
                response.addData(
                        klasseService.getKlasseBySystemId(
                                StringUtils.removeStartIgnoreCase(query, "systemid/")));
            } else if (StringUtils.startsWithIgnoreCase(query, "klasseid/")) {
                response.addData(
                        klasseService.getKlasseByKlasseIdent(
                                StringUtils.removeStartIgnoreCase(query, "klasseid/")));
            } else {
                throw new IllegalArgumentException("Invalid query: " + query);
            }
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (IllegalArgumentException e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setStatusCode("NOT_FOUND");
            response.setMessage(e.getMessage());
        }
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(ArkivActions.GET_KLASSE.name());
    }

}
