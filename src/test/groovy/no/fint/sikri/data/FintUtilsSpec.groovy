package no.fint.sikri.data

import no.fint.documaster.data.utilities.FintUtils
import spock.lang.Specification

class FintUtilsSpec extends Specification {

    def "Create identifikator"() {

        when:
        def identifikator = FintUtils.createIdentifikator("123")

        then:
        identifikator.identifikatorverdi.equals("123")
    }
}
