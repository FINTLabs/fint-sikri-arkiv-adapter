package no.novari.fint.sikri.data.noark.journalpost

import no.novari.fint.model.resource.arkiv.noark.JournalpostResource
import no.novari.fint.sikri.data.noark.arkivressurs.ArkivressursService
import no.novari.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory
import no.novari.fint.sikri.data.noark.korrespondansepart.KorrespondansepartFactory
import no.novari.fint.sikri.data.noark.skjerming.SkjermingService
import no.novari.fint.sikri.data.utilities.XmlUtils
import spock.lang.Specification

import java.time.Instant
import java.util.Date
import java.util.stream.Stream
import javax.xml.datatype.DatatypeFactory
import no.novari.fint.arkiv.sikri.oms.RegistryEntryType
import no.novari.fint.arkiv.sikri.oms.UserNameType
import no.novari.fint.sikri.model.SikriIdentity
import no.novari.fint.sikri.data.noark.dokument.DokumentbeskrivelseService
import no.novari.fint.sikri.data.noark.korrespondansepart.KorrespondansepartService
import no.novari.fint.sikri.data.noark.merknad.MerknadService

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

    def "Should map journalDato and dokumentetsDato to noon UTC"() {
        given:
        def xmlUtils = new XmlUtils()
        def sourceDate = DatatypeFactory.newInstance().newXMLGregorianCalendar("2026-08-27")
        def identity = Mock(SikriIdentity)

        def registryEntry = new RegistryEntryType(
                createdDate: xmlUtils.xmlDate(new Date()),
                registryDate: sourceDate,
                documentDate: sourceDate,
                id: 1,
                createdByUserNameId: 1,
                officerNameId: 1,
                officerName: new UserNameType(name: "Saksbehandler"),
                registerYear: 2026,
                documentNumber: 1,
                registryEntryTypeId: "J",
                recordStatusId: "F",
                title: "Test"
        )

        JournalpostFactory factory = new JournalpostFactory(
                xmlUtils: xmlUtils,
                dokumentbeskrivelseFactory: Mock(DokumentbeskrivelseFactory),
                dokumentbeskrivelseService: Mock(DokumentbeskrivelseService),
                korrespondansepartFactory: Mock(KorrespondansepartFactory),
                korrespondansepartService: Mock(KorrespondansepartService),
                merknadService: Mock(MerknadService),
                skjermingService: Mock(SkjermingService),
                arkivressursService: Mock(ArkivressursService)
        )
        factory.korrespondansepartService.queryForRegistrering(identity, "1") >> Stream.empty()
        factory.dokumentbeskrivelseService.queryForJournalpost(identity, "1") >> []
        factory.merknadService.getRemarkForRegistryEntry(identity, "1") >> []
        factory.skjermingService.getSkjermingResource(_, _) >> null

        when:
        def resource = factory.toFintResource(identity, registryEntry)

        then:
        Date expected = Date.from(Instant.parse("2026-08-27T12:00:00Z"))
        resource.getJournalDato() == expected
        resource.getDokumentetsDato() == expected
    }
}
