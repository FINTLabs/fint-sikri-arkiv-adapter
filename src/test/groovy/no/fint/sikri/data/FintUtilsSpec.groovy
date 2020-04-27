package no.fint.sikri.data

import no.fint.model.felles.Person
import no.fint.model.resource.Link
import no.fint.sikri.data.utilities.FintUtils
import spock.lang.Specification

class FintUtilsSpec extends Specification {

    def "Create identifikator"() {

        when:
        def identifikator = FintUtils.createIdentifikator("123")

        then:
        identifikator.identifikatorverdi.equals("123")
    }

    def "Validate identifikator"() {

        when:
        def invalidIdentifikatorNull = FintUtils.validIdentifikator(null)
        def invalidIdentifikatorBlank = FintUtils.validIdentifikator(FintUtils.createIdentifikator(""))
        def validIdentifikator = FintUtils.validIdentifikator(FintUtils.createIdentifikator("test"))

        then:
        !invalidIdentifikatorBlank
        !invalidIdentifikatorNull
        validIdentifikator
    }

    def "Get Id from Link"() {
        when:
        def id = FintUtils.getIdFromLink(Collections.singletonList(Link.with(Person.class, "test", "id")))

        then:
        id.get() == "id"
    }
}
