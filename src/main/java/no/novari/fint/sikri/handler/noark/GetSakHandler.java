package no.novari.fint.sikri.handler.noark;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.novari.fint.model.arkiv.noark.NoarkActions;
import no.novari.fint.model.resource.FintLinks;
import no.novari.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.novari.fint.sikri.data.noark.sak.SakFactory;
import no.novari.fint.sikri.handler.Handler;
import no.novari.fint.sikri.service.CaseQueryService;
import no.novari.fint.sikri.service.SikriIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

@Slf4j
@Service
public class GetSakHandler implements Handler {

    @Autowired
    private CaseQueryService caseQueryService;

    @Autowired
    private SakFactory sakFactory;

    @Autowired
    private SikriIdentityService identityService;

    private final MeterRegistry meterRegistry;
    private final Timer.Builder getSakTimer;

    public GetSakHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        getSakTimer = Timer.builder("fint.arkiv.sak.timer")
                .description("The Sikri Archive Sak Timer");
    }

    @Override
    public void accept(Event<FintLinks> response) {
        String query = response.getQuery();
        log.debug("Try to get a sak based on this query (and we do even counting and do some time analysis): {}", query);

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            response.setData(new LinkedList<>());

            if (!caseQueryService.isValidQuery(query)) {
                throw new IllegalArgumentException("Invalid query: " + query);
            }

            caseQueryService
                    .query(identityService.getDefaultIdentity(), query)
                    .map(sakFactory::toFintResource)
                    .forEach(response::addData);
            response.setResponseStatus(ResponseStatus.ACCEPTED);

            if (response.getData().isEmpty()) {
                response.setResponseStatus(ResponseStatus.REJECTED);
                response.setStatusCode("NOT_FOUND");
                response.setMessage("No case found for query: " + query);
            }
        } catch (IllegalCaseNumberFormat e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        } finally {
            sample.stop(getSakTimer.tag("request", "getCase")
                    .tag("status", response.getStatus().name())
                    .tag("statusCode", response.getStatusCode() != null ? response.getStatusCode() : "N/A")
                    .register(meterRegistry));
        }
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(NoarkActions.GET_SAK.name());
    }

}
