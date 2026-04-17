package no.novari.fint.sikri.handler.personal;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.novari.fint.model.arkiv.personal.PersonalActions;
import no.novari.fint.model.resource.FintLinks;
import no.novari.fint.sikri.data.exception.GetPersonalmappeNotFoundException;
import no.novari.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.novari.fint.sikri.data.personal.PersonalmappeService;
import no.novari.fint.sikri.handler.Handler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@Slf4j
public class GetPersonalmappeHandler implements Handler {
    @Autowired
    private PersonalmappeService personalmappeService;

    @Override
    public void accept(Event<FintLinks> response) {
        String query = response.getQuery();
        try {
            response.getData().clear();
            if (StringUtils.startsWithIgnoreCase(query, "mappeid/")) {
                response.addData(personalmappeService.getPersonalmappeCaseByMappeId(StringUtils.removeStartIgnoreCase(query, "mappeid/")));
            } else if (StringUtils.startsWithIgnoreCase(query, "systemid/")) {
                response.addData(personalmappeService.getPersonalmappeCaseBySystemId(StringUtils.removeStartIgnoreCase(query, "systemid/")));
            } else {
                throw new IllegalArgumentException("Invalid query: " + query);
            }
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (GetPersonalmappeNotFoundException e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setStatusCode("NOT_FOUND");
            response.setMessage(e.getMessage());
        } catch (IllegalCaseNumberFormat illegalCaseNumberFormat) {
            illegalCaseNumberFormat.printStackTrace();
        }

    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(PersonalActions.GET_PERSONALMAPPE.name());
    }

}
