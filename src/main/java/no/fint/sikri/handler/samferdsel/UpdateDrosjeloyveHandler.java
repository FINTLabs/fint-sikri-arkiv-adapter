package no.fint.sikri.handler.samferdsel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.CaseDefaults;
import no.fint.event.model.Event;
import no.fint.event.model.Operation;
import no.fint.event.model.ResponseStatus;
import no.novari.fint.model.arkiv.samferdsel.SamferdselActions;
import no.novari.fint.model.resource.FintLinks;
import no.novari.fint.model.resource.arkiv.samferdsel.SoknadDrosjeloyveResource;
import no.fint.sikri.AdapterProps;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.samferdsel.SoknadDrosjeloyveService;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.service.SikriCaseDefaultsService;
import no.fint.sikri.service.ValidationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
public class UpdateDrosjeloyveHandler implements Handler {

    private final AdapterProps props;
    private final ObjectMapper objectMapper;
    private final ValidationService validationService;
    private final SikriCaseDefaultsService caseDefaultsService;
    private final CaseDefaults caseDefaults;
    private final SoknadDrosjeloyveService soknadDrosjeloyveService;


    public UpdateDrosjeloyveHandler(AdapterProps props, ObjectMapper objectMapper, ValidationService validationService, SikriCaseDefaultsService caseDefaultsService, CaseDefaults caseDefaults, SoknadDrosjeloyveService soknadDrosjeloyveService) {
        this.props = props;
        this.objectMapper = objectMapper;
        this.validationService = validationService;
        this.caseDefaultsService = caseDefaultsService;
        this.caseDefaults = caseDefaults;
        this.soknadDrosjeloyveService = soknadDrosjeloyveService;
    }


    @Override
    public void accept(Event<FintLinks> response) {
        if (response.getData().size() != 1) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Invalid request");
            return;
        }

        Operation operation = response.getOperation();

        SoknadDrosjeloyveResource SoknadDrosjeloyveResource = objectMapper.convertValue(response.getData().get(0), SoknadDrosjeloyveResource.class);

        if (operation == Operation.CREATE) {
            try {
                createCase(response, SoknadDrosjeloyveResource);
            } catch (CaseNotFound caseNotFound) {
                caseNotFound.printStackTrace();
            }
        } else if (operation == Operation.UPDATE) {
            updateCase(response, response.getQuery(), SoknadDrosjeloyveResource);
        } else {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }

    }

    private void updateCase(Event<FintLinks> response, String query, SoknadDrosjeloyveResource soknadDrosjeloyveResource) {
        if (soknadDrosjeloyveResource.getJournalpost() == null ||
                soknadDrosjeloyveResource.getJournalpost().isEmpty()) {
            throw new IllegalArgumentException("Update must contain at least one Journalpost");
        }
        caseDefaultsService.applyDefaultsForUpdate(caseDefaults.getSoknaddrosjeloyve(), soknadDrosjeloyveResource);
        log.info("Complete document for update: {}", soknadDrosjeloyveResource);
        if (!validationService.validate(response, soknadDrosjeloyveResource.getJournalpost())) {
            return;
        }
        try {
            SoknadDrosjeloyveResource result = soknadDrosjeloyveService.updateDrosjeloyve(query, soknadDrosjeloyveResource);
            response.setData(ImmutableList.of(result));
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (CaseNotFound e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        }
    }

    private void createCase(Event<FintLinks> response, SoknadDrosjeloyveResource soknadDrosjeloyveResource) throws CaseNotFound {
        caseDefaultsService.applyDefaultsForCreation(caseDefaults.getSoknaddrosjeloyve(), soknadDrosjeloyveResource);
        log.info("Complete document for creation: {}", soknadDrosjeloyveResource);
        if (!validationService.validate(response, soknadDrosjeloyveResource)) {
            return;
        }
        try {
            SoknadDrosjeloyveResource drosjeloyve = soknadDrosjeloyveService.createDrosjeloyve(soknadDrosjeloyveResource);
            response.setData(ImmutableList.of(drosjeloyve));
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (CaseNotFound | ClassNotFoundException e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        }
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(SamferdselActions.UPDATE_SOKNADDROSJELOYVE.name());
    }
}
