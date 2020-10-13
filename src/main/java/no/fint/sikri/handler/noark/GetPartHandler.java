package no.fint.sikri.handler.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.sikri.data.exception.PartNotFound;
import no.fint.sikri.data.noark.part.PartService;
import no.fint.sikri.handler.Handler;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.arkiv.noark.ArkivActions;
import no.fint.model.resource.FintLinks;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@Slf4j
public class GetPartHandler implements Handler {
    @Autowired
    private PartService partService;

    @Override
    public void accept(Event<FintLinks> response) {
        String query = response.getQuery();
        try {
            if (StringUtils.startsWithIgnoreCase(query, "partid/")) {
                response.setData(
                        Collections.singletonList(
                                partService.getPartByPartId(StringUtils.removeStartIgnoreCase(query, "partid/"))
                        )
                );
            } else {
                throw new IllegalArgumentException("Invalid query: " + query);
            }
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (PartNotFound e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setStatusCode("NOT_FOUND");
            response.setMessage(e.getMessage());
        }

    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(ArkivActions.GET_PART.name());
    }
}
