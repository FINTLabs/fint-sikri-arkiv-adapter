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

    def "Shield title with marked email address"() {
        expect:
        shieldedTitle == getShieldedTitle(publicCaseTitle)

        where:
        publicCaseTitle                                                      | shieldedTitle
        "Sak 1 - @ola@normann.no@ og @lisa@normann.no@ oppføring av garasje" | "Sak 1 - ***** og ***** oppføring av garasje"
        "Sak 2 - @ola@normann.no@"                                           | "Sak 2 - *****"
        "Sak 3 - @Ola Normann@ med epost @ola@normann.no@"                   | "Sak 3 - ***** ***** med epost *****"
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

    def "Public title with marked text and without shielded text"() {
        when:
        def publicCaseTitle = "Sak 1 - #Ole Nordmann# og #Lisa Nordmann# oppføring av garasje"
        def result = getShieldedTitle(publicCaseTitle)

        then:
        result == "Sak 1 - Ole Nordmann og Lisa Nordmann oppføring av garasje"
    }

    def "Public title with shielded text and without marked text"() {
        when:
        def publicCaseTitle = "Sak 1 - @Ole Nordmann@ (ole@norge.no) oppføring av garasje"
        def result = getMarkedTitle(publicCaseTitle)

        then:
        result == "Sak 1 - Ole Nordmann (ole@norge.no) oppføring av garasje"
    }

    def "Public title with marked text and without shielded text and with hashtag"() {
        when:
        def publicCaseTitle = "Sak 1 - #Ole Nordmann# og #Lisa Nordmann# søker tilskudd til #vixenawards"
        def result = getShieldedTitle(publicCaseTitle)

        then:
        result == "Sak 1 - Ole Nordmann og Lisa Nordmann søker tilskudd til #vixenawards"
    }
}
