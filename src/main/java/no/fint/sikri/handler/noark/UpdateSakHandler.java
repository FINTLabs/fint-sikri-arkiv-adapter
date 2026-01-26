package no.fint.sikri.handler.noark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.CaseDefaults;
import no.fint.event.model.Event;
import no.fint.event.model.Operation;
import no.fint.event.model.ResponseStatus;
import no.novari.fint.model.arkiv.noark.NoarkActions;
import no.novari.fint.model.resource.FintLinks;
import no.novari.fint.model.resource.arkiv.noark.SakResource;
import no.fint.sikri.AdapterProps;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.noark.sak.SakService;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.service.SikriCaseDefaultsService;
import no.fint.sikri.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@Slf4j
public class UpdateSakHandler implements Handler {
    @Autowired
    private AdapterProps props;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private SikriCaseDefaultsService caseDefaultsService;

    @Autowired
    private CaseDefaults caseDefaults;

    @Autowired
    private SakService sakService;

    private final MeterRegistry meterRegistry;
    private final Timer.Builder updateSakTimer;


    public UpdateSakHandler(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        updateSakTimer = Timer.builder("fint.arkiv.update-sak.timer")
                .description("The Sikri Archive Update Timer");
    }

    @Override
    public void accept(Event<FintLinks> response) {
        if (response.getData().size() != 1) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Invalid request");
            return;
        }

        Operation operation = response.getOperation();

        SakResource sakResource = objectMapper.convertValue(response.getData().get(0), SakResource.class);

        if (operation == Operation.CREATE) {
            createCase(response, sakResource);
        } else if (operation == Operation.UPDATE) {
            updateCase(response, response.getQuery(), sakResource);
        } else {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }

    }

    private void updateCase(Event<FintLinks> response, String query, SakResource sakResource) {
        if (sakResource.getJournalpost() == null ||
                sakResource.getJournalpost().isEmpty()) {
            throw new IllegalArgumentException("Update must contain at least one Journalpost");
        }
        //TODO caseDefaultsService.applyDefaultsForUpdate(caseDefaults.getXXX(), sakResource);
        log.debug("Complete document for update: {}", sakResource);
        if (!validationService.validate(response, sakResource.getJournalpost())) {
            return;
        }

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            SakResource result = sakService.updateGenericCase(query, sakResource);
            response.setData(ImmutableList.of(result));
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (CaseNotFound e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        } finally {
            sample.stop(updateSakTimer.tag("request", "updateCase")
                    .tag("status", response.getStatus().name())
                    .tag("statusCode", response.getStatusCode() != null ? response.getStatusCode() : "N/A")
                    .register(meterRegistry));
        }
    }

    private void createCase(Event<FintLinks> response, SakResource sakResource) {
        //TODO caseDefaultsService.applyDefaultsForCreation(caseDefaults.getXXX(), sakResource);
        log.debug("Complete document for creation: {}", sakResource);
        if (!validationService.validate(response, sakResource)) {
            return;
        }

        Timer.Sample sample = Timer.start(meterRegistry);

        SakResource sak = sakService.createGenericCase(sakResource);
        response.setData(ImmutableList.of(sak));
        response.setResponseStatus(ResponseStatus.ACCEPTED);

        sample.stop(updateSakTimer.tag("request", "createCase")
                .tag("status", response.getStatus().name())
                .tag("statusCode", response.getStatusCode() != null ? response.getStatusCode() : "N/A")
                .register(meterRegistry));
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(NoarkActions.UPDATE_SAK.name());
    }
}
