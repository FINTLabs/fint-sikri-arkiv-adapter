package no.novari.fint.sikri.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.event.model.Event;
import no.fint.event.model.Problem;
import no.fint.event.model.ResponseStatus;
import no.novari.fint.model.resource.FintLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ValidationService {

    @Autowired
    private ValidatorFactory validatorFactory;

    /**
     * Set to `false` to make validations result in errors.
     */
    @Value("${fint.validation.ignore:true}")
    private boolean ignoreValidation;

    public Stream<Problem> getProblems(Object resource) {
        if (resource instanceof Collection) {
            return ((Collection) resource).stream().flatMap(this::getProblems);
        }
        Validator validator = validatorFactory.getValidator();
        return validator.validate(resource)
                .stream()
                .map(violation -> new Problem() {{
                    setField(violation.getPropertyPath().toString());
                    setMessage(violation.getMessage());
                }});
    }

    public boolean validate(Event<FintLinks> event, Object resource) {
        if (resource == null) {
            event.setResponseStatus(ResponseStatus.REJECTED);
            event.setMessage("RESOURCE WAS NULL");
            return ignoreValidation;
        }
        final List<Problem> problems = getProblems(resource).collect(Collectors.toList());
        if (problems.isEmpty()) {
            return true;
        }
        event.setProblems(problems);
        event.setResponseStatus(ResponseStatus.REJECTED);
        event.setStatusCode("INVALID");
        event.setMessage("Payload fails validation");
        log.info("Validation problems!\n{}\n{}\n", resource, problems);
        return ignoreValidation;
    }

}
