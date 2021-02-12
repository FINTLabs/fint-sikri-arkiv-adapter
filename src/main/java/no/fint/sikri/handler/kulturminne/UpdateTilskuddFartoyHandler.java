package no.fint.sikri.handler.kulturminne;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.event.model.Event;
import no.fint.event.model.Operation;
import no.fint.event.model.Problem;
import no.fint.event.model.ResponseStatus;
import no.fint.model.arkiv.kulturminnevern.KulturminnevernActions;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.arkiv.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.AdapterProps;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.kulturminne.TilskuddFartoyService;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.service.SikriCaseDefaultsService;
import no.fint.sikri.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UpdateTilskuddFartoyHandler implements Handler {
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
    private TilskuddFartoyService tilskuddfartoyService;

    @Override
    public void accept(Event<FintLinks> response) {
        if (response.getData().size() != 1) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Invalid request");
            return;
        }

        Operation operation = response.getOperation();

        TilskuddFartoyResource tilskuddFartoyResource = objectMapper.convertValue(response.getData().get(0), TilskuddFartoyResource.class);

        if (operation == Operation.CREATE) {
            createCase(response, tilskuddFartoyResource);
        } else if (operation == Operation.UPDATE) {
            updateCase(response, response.getQuery(), tilskuddFartoyResource);
        } else {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }

    }

    private void updateCase(Event<FintLinks> response, String query, TilskuddFartoyResource tilskuddFartoyResource) {
        if (tilskuddFartoyResource.getJournalpost() == null ||
                tilskuddFartoyResource.getJournalpost().isEmpty()) {
            throw new IllegalArgumentException("Update must contain at least one Journalpost");
        }
        caseDefaultsService.applyDefaultsForUpdate(caseDefaults.getTilskuddfartoy(), tilskuddFartoyResource);
        log.info("Complete document for update: {}", tilskuddFartoyResource);
        List<Problem> problems = validationService.getProblems(tilskuddFartoyResource.getJournalpost()).collect(Collectors.toList());
        if (!problems.isEmpty()) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Payload fails validation!");
            response.setProblems(problems);
            log.info("Validation problems!\n{}\n{}\n", tilskuddFartoyResource, problems);
            if (props.isFatalValidation()) {
                return;
            }
        }
        try {
            TilskuddFartoyResource result = tilskuddfartoyService.updateTilskuddFartoyCase(query, tilskuddFartoyResource);
            response.setData(ImmutableList.of(result));
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (CaseNotFound e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        }
    }

    private void createCase(Event<FintLinks> response, TilskuddFartoyResource tilskuddFartoyResource) {
        caseDefaultsService.applyDefaultsForCreation(caseDefaults.getTilskuddfartoy(), tilskuddFartoyResource);
        log.info("Complete document for creation: {}", tilskuddFartoyResource);
        List<Problem> problems = validationService.getProblems(tilskuddFartoyResource).collect(Collectors.toList());
        if (!problems.isEmpty()) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Payload fails validation!");
            response.setProblems(problems);
            log.info("Validation problems!\n{}\n{}\n", tilskuddFartoyResource, problems);
            if (props.isFatalValidation()) {
                return;
            }
        }
        TilskuddFartoyResource tilskuddFartoy = tilskuddfartoyService.createTilskuddFartoyCase(tilskuddFartoyResource);
        response.setData(ImmutableList.of(tilskuddFartoy));
        response.setResponseStatus(ResponseStatus.ACCEPTED);
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(KulturminnevernActions.UPDATE_TILSKUDDFARTOY.name());
    }
}
