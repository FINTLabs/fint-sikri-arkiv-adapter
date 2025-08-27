package no.fint.sikri.data.utilities

import spock.lang.Specification

import static no.fint.sikri.data.utilities.SikriUtils.getMarkedTitle
import static no.fint.sikri.data.utilities.SikriUtils.getShieldedTitle

class NoarkTitleSpec extends Specification {


    def "Shield title with shielded words"() {
        expect:
        shieldedTitle == getShieldedTitle(publicCaseTitle)

        where:
        publicCaseTitle                                                             | shieldedTitle
        null                                                                        | null
        ""                                                                          | null
        "Uten skjerming"                                                            | "Uten skjerming"
        "Sak 1 - @ola@normann.no@ og @lisa@normann.no@ oppføring av garasje"        | "Sak 1 - ***** og ***** oppføring av garasje"
        "Sak 2 - @ola@normann.no@"                                                  | "Sak 2 - *****"
        "Sak 3 - @Ola Normann@ med epost @ola@normann.no@"                          | "Sak 3 - ***** ***** med epost *****"
        "Sak 1 - @Ola Norman@ og @Lisa Norman@ oppføring av garasje"                | "Sak 1 - ***** ***** og ***** ***** oppføring av garasje"
        "Sak 1 - #Ole Nordmann# og #Lisa Nordmann# søker tilskudd til #vixenawards" | "Sak 1 - Ole Nordmann og Lisa Nordmann søker tilskudd til #vixenawards"
        "Komp - @#Medalen Grethe Ragni# - 311280@"                                  | "Komp - ***** ***** ***** ***** *****"
        "Komp - @#Medalen  Grethe Ragni# - 311280@"                                 | "Komp - *****  ***** ***** ***** *****" // Legg merke til ekstra mellomrom
        "Søknad - Refnr 123 - @Ballestad Egil - @"                                  | "Søknad - Refnr 123 - ***** ***** ***** "
        "Søknad - @Ballestad Egil - @ - Telemark"                                   | "Søknad - ***** ***** *****  - Telemark" // Legg merke til ekstra mellomrom
        "Søknad - @Tim-Kristoffer Vadla Steen@"                                     | "Søknad - ***** ***** *****"
        "Søknad - @Tim Kristoffer Vadla Steen@"                                     | "Søknad - ***** ***** ***** *****"
        "Søknad - @@"                                                               | "Søknad - "
    }

    def "Mark title with marked words"() {
        expect:
        markedTitle == getMarkedTitle(publicCaseTitle)

        where:
        publicCaseTitle                                                  | markedTitle
        null                                                             | null
        ""                                                               | null
        "Uten markering"                                                 | "Uten markering"
        "Sak 1 - #Ola Norman# og #Lisa Norman# oppføring av garasje"     | "Sak 1 - ##### ####_ og ##### ####_ oppføring av garasje"
        "Sak 1 - #Ole Nordmann# og #Lisa Nordmann# oppføring av garasje" | "Sak 1 - ##### ####_ og ##### ####_ oppføring av garasje"
        "Sak 1 - @Ole Nordmann@ (ole@norge.no) oppføring av garasje"     | "Sak 1 - ***** ***** (ole@norge.no) oppføring av garasje"
        "Komp - @#Medalen Grethe Ragni# - 311280@"                       | "Komp - +++++ +++++ ++++_ ***** *****"
        "Komp - #Medalen @Grethe@ Ragni# - 311280"                       | "Komp - ##### ##### ####_ - 311280" // Skjerming inni markering gir ikke mening
        "Søknad - Refnr 123 - #Ballestad Egil - #"                       | "Søknad - Refnr 123 - ##### ##### ####_ "
        "Søknad - #Ballestad Egil - # - Telemark"                        | "Søknad - ##### ##### ####_  - Telemark"
        "Søknad - @##@"                                                  | "Søknad - "
        "Søknad - ##"                                                    | "Søknad - "
    }

}
