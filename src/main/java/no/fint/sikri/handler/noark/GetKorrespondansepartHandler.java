package no.fint.sikri.handler.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.administrasjon.arkiv.ArkivActions;
import no.fint.model.resource.FintLinks;
import no.fint.sikri.data.exception.KorrespondansepartNotFound;
import no.fint.sikri.data.noark.korrespondansepart.KorrespondansepartService;
import no.fint.sikri.handler.Handler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class GetKorrespondansepartHandler implements Handler {
    @Autowired
    private KorrespondansepartService korrespondansepartService;

    @Override
    public void accept(Event<FintLinks> response) {
        String query = response.getQuery();
        try {
            if (StringUtils.startsWithIgnoreCase(query, "systemid/")) {
                response.addData(
                        korrespondansepartService.getKorrespondansepartBySystemId(
                                StringUtils.removeStartIgnoreCase(query, "systemid/")));
                // FIXME: 20/01/2020 Check if we can get korrpart from orgnr and fnr
//            } else if (StringUtils.startsWith(query, "organisasjonsnummer/")) {
//                response.addData(
//                        korrespondansepartService.getKorrespondansepartByOrganisasjonsnummer(
//                                StringUtils.removeStart(query, "organisasjonsnummer/")));
//            } else if (StringUtils.startsWith(query, "fodselsnummer/")) {
//                response.addData(
//                        korrespondansepartService.getKorrespondansepartByFodselsnummer(
//                                StringUtils.removeStart(query, "fodselsnummer/")
//                        )
//                );
//            } else if (StringUtils.startsWith(query, "?")) {
//                korrespondansepartService.search(getQueryParams(query)).forEach(response::addData);
//            } else {
//                throw new IllegalArgumentException("Invalid query: " + query);
            }

            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (KorrespondansepartNotFound e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setStatusCode("NOT_FOUND");
            response.setMessage(e.getMessage());
        }

    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(ArkivActions.GET_KORRESPONDANSEPART.name());
    }

}
