package no.fint.sikri.handler.kulturminne;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.event.model.Event;
import no.fint.event.model.Operation;
import no.fint.event.model.ResponseStatus;
import no.fint.model.arkiv.kulturminnevern.KulturminnevernActions;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.arkiv.kulturminnevern.TilskuddFredaBygningPrivatEieResource;
import no.fint.sikri.AdapterProps;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.kulturminne.TilskuddFredaBygningPrivatEieService;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.service.SikriCaseDefaultsService;
import no.fint.sikri.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@Slf4j
public class UpdateTilskuddFredaBygningPrivatEieHandler implements Handler {
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
    private TilskuddFredaBygningPrivatEieService tilskuddFredaBygningPrivatEieService;

    @Override
    public void accept(Event<FintLinks> response) {
        if (response.getData().size() != 1) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Invalid request");
            return;
        }

        Operation operation = response.getOperation();

        TilskuddFredaBygningPrivatEieResource tilskuddFredaBygningPrivatEieResource = objectMapper.convertValue(response.getData().get(0), TilskuddFredaBygningPrivatEieResource.class);

        if (operation == Operation.CREATE) {
            createCase(response, tilskuddFredaBygningPrivatEieResource);
        } else if (operation == Operation.UPDATE) {
            updateCase(response, response.getQuery(), tilskuddFredaBygningPrivatEieResource);
        } else {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }

    }

    private void updateCase(Event<FintLinks> response, String query, TilskuddFredaBygningPrivatEieResource tilskuddFredaBygningPrivatEieResource) {
        if (tilskuddFredaBygningPrivatEieResource.getJournalpost() == null ||
                tilskuddFredaBygningPrivatEieResource.getJournalpost().isEmpty()) {
            throw new IllegalArgumentException("Update must contain at least one Journalpost");
        }
        caseDefaultsService.applyDefaultsForUpdate(caseDefaults.getTilskuddfredabygningprivateie(), tilskuddFredaBygningPrivatEieResource);
        log.info("Complete document for update: {}", tilskuddFredaBygningPrivatEieResource);
        if (!validationService.validate(response, tilskuddFredaBygningPrivatEieResource.getJournalpost())) {
            return;
        }
        try {
            TilskuddFredaBygningPrivatEieResource result = tilskuddFredaBygningPrivatEieService.updateTilskuddFredaBygningPrivatEieCase(query, tilskuddFredaBygningPrivatEieResource);
            response.setData(ImmutableList.of(result));
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (CaseNotFound e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        }
    }

    private void createCase(Event<FintLinks> response, TilskuddFredaBygningPrivatEieResource tilskuddFredaBygningPrivatEieResource) {
        caseDefaultsService.applyDefaultsForCreation(caseDefaults.getTilskuddfredabygningprivateie(), tilskuddFredaBygningPrivatEieResource);
        log.info("Complete document for creation: {}", tilskuddFredaBygningPrivatEieResource);
        if (!validationService.validate(response, tilskuddFredaBygningPrivatEieResource)) {
            return;
        }
        TilskuddFredaBygningPrivatEieResource tilskuddFredaBygningPrivatEie = tilskuddFredaBygningPrivatEieService.createTilskuddFredaBygningPrivatEieCase(tilskuddFredaBygningPrivatEieResource);
        response.setData(ImmutableList.of(tilskuddFredaBygningPrivatEie));
        response.setResponseStatus(ResponseStatus.ACCEPTED);
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(KulturminnevernActions.UPDATE_TILSKUDDFREDABYGNINGPRIVATEIE.name());
    }
}
