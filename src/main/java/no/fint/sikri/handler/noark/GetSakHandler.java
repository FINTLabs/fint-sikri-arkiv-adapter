package no.fint.sikri.handler.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.exception.GetCaseException;
import no.fint.sikri.data.exception.GetDocumentException;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.noark.sak.SakService;
import no.fint.sikri.handler.Handler;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.administrasjon.arkiv.ArkivActions;
import no.fint.model.resource.FintLinks;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

import static no.fint.sikri.data.utilities.QueryUtils.getQueryParams;

@Slf4j
@Service
public class GetSakHandler implements Handler {
    @Autowired
    private SakService sakService;

    @Override
    public void accept(Event<FintLinks> response) {
        String query = response.getQuery();
        try {
            response.getData().clear();
            if (StringUtils.startsWithIgnoreCase(query, "mappeid/")) {
                response.addData(sakService.getSakByCaseNumber(StringUtils.removeStartIgnoreCase(query, "mappeid/")));
            } else if (StringUtils.startsWithIgnoreCase(query, "systemid/")) {
                response.addData(sakService.getSakBySystemId(StringUtils.removeStartIgnoreCase(query, "systemid/")));
            } else if (StringUtils.startsWith(query, "?")) {
                sakService.searchSakByQueryParams(getQueryParams(query)).forEach(response::addData);
            } else {
                throw new IllegalArgumentException("Invalid query: " + query);
            }
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (CaseNotFound e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setStatusCode("NOT_FOUND");
            response.setMessage(e.getMessage());
        } catch (GetCaseException | GetDocumentException | IllegalCaseNumberFormat e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        }

    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(ArkivActions.GET_SAK.name());
    }

}
