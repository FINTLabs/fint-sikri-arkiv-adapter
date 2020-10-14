package no.fint.sikri.handler.personal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.event.model.*;
import no.fint.model.arkiv.personal.PersonalActions;
import no.fint.model.resource.FintLinks;
import no.fint.model.resource.arkiv.personal.PersonalmappeResource;
import no.fint.sikri.data.exception.*;
import no.fint.sikri.data.personal.PersonalmappeService;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.service.SikriCaseDefaultsService;
import no.fint.sikri.service.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UpdatePersonalmappeHandler implements Handler {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private SikriCaseDefaultsService caseDefaultsService;

    @Autowired
    private CaseDefaults caseDefaults;

    @Autowired
    private PersonalmappeService personalmappeService;

    @Override
    public void accept(Event<FintLinks> response) {
        if (response.getData().size() != 1) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage("Invalid request");
            return;
        }

        Operation operation = response.getOperation();

        PersonalmappeResource personalmappeResource = objectMapper.convertValue(response.getData().get(0), PersonalmappeResource.class);

        if (operation == Operation.CREATE) {
            createCase(response, personalmappeResource);
        } else if (operation == Operation.UPDATE) {

            updateCase(response, response.getQuery(), personalmappeResource);

        } else {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }

    }

    private void updateCase(Event<FintLinks> response, String query, PersonalmappeResource personalmappeResource) {
        if (StringUtils.startsWithIgnoreCase(query, "mappeid/")) {
            try {
                String caseNumber = StringUtils.removeStartIgnoreCase(query, "mappeid/");
                PersonalmappeResource result = personalmappeService.updatePersonalmappeByCaseNumber(caseNumber, personalmappeResource);
                response.setData(ImmutableList.of(result));
                response.setResponseStatus(ResponseStatus.ACCEPTED);
            } catch (IllegalCaseNumberFormat
                    | GetPersonalmappeNotFoundException
                    | UnableToGetIdFromLink
                    | ClassificationNotFound
                    | ClassificationIsNotPartOfPersonalFile
                    | OfficerNotFound
                    | AdministrativeUnitNotFound e) {
                response.setResponseStatus(ResponseStatus.REJECTED);
                response.setMessage(e.getMessage());
            }
        } else if (StringUtils.startsWithIgnoreCase(query, "systemid/")) {
            try {
                String systemid = StringUtils.removeStartIgnoreCase(query, "systemid/");
                PersonalmappeResource result = personalmappeService.updatePersonalmappeBySystemId(systemid, personalmappeResource);
                response.setData(ImmutableList.of(result));
                response.setResponseStatus(ResponseStatus.ACCEPTED);
            } catch (GetPersonalmappeNotFoundException
                    | UnableToGetIdFromLink
                    | ClassificationNotFound
                    | ClassificationIsNotPartOfPersonalFile
                    | OfficerNotFound
                    | AdministrativeUnitNotFound e) {
                response.setResponseStatus(ResponseStatus.REJECTED);
                response.setMessage(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Invalid query: " + query);
        }
    }

    private void createCase(Event<FintLinks> response, PersonalmappeResource personalmappeResource) {
        try {
            caseDefaultsService.applyDefaultsForCreation(caseDefaults.getPersonalmappe(), personalmappeResource);
            log.info("Complete document for creation: {}", personalmappeResource);
            List<Problem> problems = validationService.getProblems(personalmappeResource);
            if (!problems.isEmpty()) {
                response.setResponseStatus(ResponseStatus.REJECTED);
                response.setMessage("Payload fails validation!");
                response.setProblems(problems);
                log.info("Validation problems!\n{}\n{}\n", personalmappeResource, problems);
                return;
            }
            Optional<PersonalmappeResource> personalmappeExists = personalmappeService.personalmappeExists(personalmappeResource);
            if (personalmappeExists.isPresent()) {
                response.setData(ImmutableList.of(personalmappeExists.get()));
                response.setStatus(Status.ADAPTER_REJECTED);
                response.setResponseStatus(ResponseStatus.CONFLICT);
                return;
            }

            PersonalmappeResource personalmappe = personalmappeService.createPersonalmappe(personalmappeResource);
            response.setData(ImmutableList.of(personalmappe));
            response.setResponseStatus(ResponseStatus.ACCEPTED);
        } catch (UnableToGetIdFromLink | AdministrativeUnitNotFound | OfficerNotFound | GetPersonalmappeNotFoundException e) {
            response.setResponseStatus(ResponseStatus.REJECTED);
            response.setMessage(e.getMessage());
        }
    }

    @Override
    public Set<String> actions() {
        return Collections.singleton(PersonalActions.UPDATE_PERSONALMAPPE.name());
    }
}
