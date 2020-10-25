package no.fint.sikri.service;

import no.fint.event.model.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collection;
import java.util.stream.Stream;

@Service
public class ValidationService {

    @Autowired
    private ValidatorFactory validatorFactory;

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


}
