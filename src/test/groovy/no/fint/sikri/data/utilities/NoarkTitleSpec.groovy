package no.fint.sikri.data.utilities

import spock.lang.Specification

import static no.fint.sikri.data.utilities.SikriUtils.getMarkedTitle
import static no.fint.sikri.data.utilities.SikriUtils.getShieldedTitle

class NoarkTitleSpec extends Specification {

    def "Shield title with marked words"() {
        when:
        def publicCaseTitle = "Sak 1 - @Ola Norman@ og @Lisa Norman@ oppføring av garasje"
        def result = getShieldedTitle(publicCaseTitle)

        then:
        result == "Sak 1 - ***** ***** og ***** ***** oppføring av garasje"
    }

    def "Shield title with null value"() {
        when:
        def publicCaseTitle = null
        def result = getShieldedTitle(publicCaseTitle)

        then:
        result == null
    }

    def "Shield title with empty string"() {
        when:
        def publicCaseTitle = ""
        def resultat = getShieldedTitle(publicCaseTitle)

        then:
        resultat == null
    }

    def "Title without shielded text"() {
        when:
        def publicCaseTitle = "Uten skjerming"
        def resultat = getShieldedTitle(publicCaseTitle)

        then:
        resultat == "Uten skjerming"
    }

    def "Marked title with marked words"() {
        when:
        def publicCaseTitle = "Sak 1 - #Ola Norman# og #Lisa Norman# oppføring av garasje"
        def result = getMarkedTitle(publicCaseTitle)

        then:
        result == "Sak 1 - ##### ####_ og ##### ####_ oppføring av garasje"
    }

    def "Marked title with empty string"() {
        when:
        def publicCaseTitle = ""
        def result = getMarkedTitle(publicCaseTitle)

        then:
        result == null
    }

    def "Marked title with null value"() {
        when:
        def publicCaseTitle = null
        def result = getMarkedTitle(publicCaseTitle)

        then:
        result == null
    }

    def "Title without marked text"() {
        when:
        def publicCaseTitle = "Uten markering"
        def result = getMarkedTitle(publicCaseTitle)

        then:
        result == "Uten markering"
    }

}
