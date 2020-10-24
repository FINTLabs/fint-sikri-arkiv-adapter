package no.fint.sikri.handler.drosjeloyve;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.event.model.Event;
import no.fint.event.model.Operation;
import no.fint.event.model.Problem;
import no.fint.event.model.ResponseStatus;
import no.fint.model.arkiv.samferdsel.SamferdselActions;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.arkiv.samferdsel.DrosjeloyveResource;
import no.fint.sikri.data.drosjeloyve.DrosjeloyveService;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.service.SikriCaseDefaultsService;
import no.fint.sikri.service.ValidationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UpdateDrosjeloyveHandler implements Handler {

    private final ObjectMapper objectMapper;
    private final ValidationService validationService;
    private final SikriCaseDefaultsService caseDefaultsService;
    private final CaseDefaults caseDefaults;
    private final DrosjeloyveService drosjeloyveService;


    public UpdateDrosjeloyveHandler(ObjectMapper objectMapper, ValidationService validationService, SikriCaseDefaultsService caseDefaultsService, CaseDefaults caseDefaults, DrosjeloyveService drosjeloyveService) {
        this.objectMapper = objectMapper;
        this.validationService = validationService;
        this.caseDefaultsService = caseDefaultsService;
        this.caseDefaults = caseDefaults;
        this.drosjeloyveService = drosjeloyveService;
    }


    @Override
    public void accept(Event<FintLinks> response) {
        if (response.getData().size() != 1) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Invalid request");
            return;
        }

        Operation operation = response.getOperation();

        DrosjeloyveResource drosjeloyveResource = objectMapper.convertValue(response.getData().get(0), DrosjeloyveResource.class);

        if (operation == Operation.CREATE) {
            try {
                createCase(response, drosjeloyveResource);
            } catch (CaseNotFound caseNotFound) {
                caseNotFound.printStackTrace();
            }
        } else if (operation == Operation.UPDATE) {
            updateCase(response, response.getQuery(), drosjeloyveResource);
        } else {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }

    }

    private void updateCase(Event<FintLinks> response, String query, DrosjeloyveResource tilskuddFartoyResource) {
        if (tilskuddFartoyResource.getJournalpost() == null ||
                tilskuddFartoyResource.getJournalpost().isEmpty()) {
            throw new IllegalArgumentException("Update must contain at least one Journalpost");
        }
        caseDefaultsService.applyDefaultsForUpdate(caseDefaults.getTilskuddfartoy(), tilskuddFartoyResource);
        log.info("Complete document for update: {}", tilskuddFartoyResource);
        List<Problem> problems = validationService.getProblems(tilskuddFartoyResource.getJournalpost());
        if (!problems.isEmpty()) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Payload fails validation!");
            response.setProblems(problems);
            log.info("Validation problems!\n{}\n{}\n", tilskuddFartoyResource, problems);
            return;
        }
        try {
            DrosjeloyveResource result = drosjeloyveService.updateDrosjeloyve(query, tilskuddFartoyResource);
            response.setData(ImmutableList.of(result));
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (CaseNotFound e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        }
    }

    private void createCase(Event<FintLinks> response, DrosjeloyveResource drosjeloyveResource) throws CaseNotFound {
        caseDefaultsService.applyDefaultsForCreation(caseDefaults.getDrosjeloyve(), drosjeloyveResource);
        log.info("Complete document for creation: {}", drosjeloyveResource);
        List<Problem> problems = validationService.getProblems(drosjeloyveResource);
        if (!problems.isEmpty()) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Payload fails validation!");
            response.setProblems(problems);
            log.info("Validation problems!\n{}\n{}\n", drosjeloyveResource, problems);
            return;
        }
        try {
            DrosjeloyveResource tilskuddFartoy = drosjeloyveService.createDrosjeloyve(drosjeloyveResource);
            response.setData(ImmutableList.of(tilskuddFartoy));
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (CaseNotFound | ClassNotFoundException e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        }
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(SamferdselActions.UPDATE_DROSJELOYVE.name());
    }
}
