package no.fint.sikri.data.utilities

import spock.lang.Specification

import java.util.regex.Matcher
import java.util.regex.Pattern

import static no.fint.sikri.data.utilities.SikriUtils.getShieldedTitle


class NoarkTitleSpec extends Specification {

    void setup() {

    }

    def "Shield title with marked words"(){
        when:
        def publicCaseTitle = "Sak 1 - @Ola Norman@ og @Lisa Norman@ oppføring av garasje"
        def resultat = getShieldedTitle(publicCaseTitle)

        then:
        resultat == "Sak 1 - ***** ***** og ***** ***** oppføring av garasje"
    }

    def "Shield title with null-value"(){
        when:
        def publicCaseTitle = null
        def resultat = getShieldedTitle(publicCaseTitle)

        then:
        resultat == null
    }

}
