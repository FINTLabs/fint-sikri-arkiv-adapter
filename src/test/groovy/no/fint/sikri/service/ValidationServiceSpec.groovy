package no.fint.sikri.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.event.model.Event
import no.fint.model.arkiv.kulturminnevern.KulturminnevernActions
import no.fint.model.resource.FintLinks
import no.fint.model.resource.arkiv.kulturminnevern.TilskuddFartoyResource
import spock.lang.Specification

import javax.validation.Validation
import javax.validation.ValidatorFactory

class ValidationServiceSpec extends Specification {

    private ValidatorFactory validatorFactory

    void setup(){
        validatorFactory = Validation.buildDefaultValidatorFactory()
    }

    def "Let`s validate"() {
        given:
        ValidationService service = new ValidationService(validatorFactory: validatorFactory)
        Event<FintLinks> event = new Event<FintLinks>('test.no', 'test', KulturminnevernActions.GET_TILSKUDDFREDABYGNINGPRIVATEIE, 'test')

        def om = new ObjectMapper()
        def resource = om.readValue("{}", TilskuddFartoyResource)

        when:
        boolean result = service.validate(event, resource)

        then:
        result == false
    }

}
