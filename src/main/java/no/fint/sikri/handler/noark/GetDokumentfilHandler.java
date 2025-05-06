package no.fint.sikri.handler.noark;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.arkiv.noark.NoarkActions;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.arkiv.noark.DokumentfilResource;
import no.fint.sikri.data.exception.FileNotFound;
import no.fint.sikri.data.noark.dokument.DokumentfilService;
import no.fint.sikri.handler.Handler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class GetDokumentfilHandler implements Handler {

    @Autowired
    private DokumentfilService dokumentfilService;

    private final MeterRegistry meterRegistry;
    private final Timer.Builder getDokumentfilTimer;

    public GetDokumentfilHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        getDokumentfilTimer = Timer.builder("fint.arkiv.get-dokumentfil.timer")
                .description("The Sikri Archive Dokumentfil Timer");
    }

    @Override
    public void accept(Event<FintLinks> response) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            if (!StringUtils.startsWithIgnoreCase(response.getQuery(), "systemid/")) {
                response.setResponseStatus(ResponseStatus.REJECTED);
                response.setStatusCode("INVALID_QUERY");
                response.setMessage("Invalid query: " + response.getQuery());
                return;
            }
            String systemId = StringUtils.removeStartIgnoreCase(response.getQuery(), "systemid/");
            DokumentfilResource dokumentfilResource = dokumentfilService.getDokumentfil(systemId);
            response.addData(dokumentfilResource);
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (FileNotFound | IOException e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setStatusCode("NOT_FOUND");
            response.setMessage(e.getMessage());
        } finally {
            sample.stop(getDokumentfilTimer.tag("request", "getDokumentfil")
                    .tag("status", response.getStatus().name())
                    .tag("statusCode", response.getStatusCode() != null ? response.getStatusCode() : "N/A")
                    .register(meterRegistry));
        }
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(NoarkActions.GET_DOKUMENTFIL.name());
    }

}
