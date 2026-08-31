package no.novari.fint.sikri.data.noark.journalpost

import no.novari.fint.model.resource.arkiv.noark.JournalpostResource
import no.novari.fint.sikri.data.noark.arkivressurs.ArkivressursService
import no.novari.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory
import no.novari.fint.sikri.data.noark.korrespondansepart.KorrespondansepartFactory
import no.novari.fint.sikri.data.noark.skjerming.SkjermingService
import no.novari.fint.sikri.data.utilities.XmlUtils
import spock.lang.Specification

import java.time.Instant

class JournalpostFactorySpec extends Specification {

    def "Should map dokumentetsDato to registry entry document date"() {
        given:

        Date dokumentetsDato = Date.from(Instant.parse("2026-08-27T00:00:00Z"))

        JournalpostResource journalpost = new JournalpostResource(
                tittel: "Journalpost med dokumentdato",
                dokumentetsDato: dokumentetsDato,
                dokumentbeskrivelse: [],
                korrespondansepart: []
        )
        JournalpostFactory factory = new JournalpostFactory(
                xmlUtils: new XmlUtils(),
                dokumentbeskrivelseFactory: Mock(DokumentbeskrivelseFactory),
                korrespondansepartFactory: Mock(KorrespondansepartFactory),
                skjermingService: Mock(SkjermingService),
                arkivressursService: Mock(ArkivressursService)
        )

        when:
        RegistryEntryDocuments result = factory.toRegistryEntryDocuments(123, journalpost, "", "")

        then:
        result.registryEntry.documentDate.toGregorianCalendar().time == dokumentetsDato
    }
}
