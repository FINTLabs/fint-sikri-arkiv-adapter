package no.fint.sikri.handler.noark;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.model.arkiv.noark.NoarkActions;
import no.fint.model.resource.FintLinks;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.noark.sak.SakFactory;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.SikriIdentityService;
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
    private final Counter.Builder sakCounter;
    private final Timer.Builder sakTimer;

    public GetSakHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        sakCounter = Counter.builder("fint.sikri.sak.counter")
                .description("The Archive Abacus");
        sakTimer = Timer.builder("fint.sikri.sak.timer")
                .description("The Archive Timer");
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
            sakCounter.tag("status", response.getStatus().name())
                    .register(meterRegistry)
                    .increment();

            sample.stop(sakTimer.tag("status", response.getStatus().name())
                    .register(meterRegistry));
        }
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(NoarkActions.GET_SAK.name());
    }

}
