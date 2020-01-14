package no.fint.sikri.data.fint

import com.fasterxml.jackson.databind.ObjectMapper
import no.documaster.model.QueryResult
import no.fint.documaster.data.kulturminne.TilskuddFartoyFactory
import no.fint.documaster.data.noark.common.NoarkFactory
import no.fint.documaster.data.noark.journalpost.JournalpostService
import no.fint.documaster.data.noark.merknad.MerknadService
import no.fint.documaster.data.noark.nokkelord.NokkelordService
import no.fint.documaster.data.noark.part.PartService
import spock.lang.Specification

class TilskuddFartoyFactorySpec extends Specification {

    private TilskuddFartoyFactory tilskuddFartoyFactory
    private JournalpostService journalpostService
    private NoarkFactory noarkFactory
    private PartService partService
    private ObjectMapper objectMapper
    private QueryResult queryResult

    void setup() {
        journalpostService = Mock()
        partService = Mock()
        noarkFactory = new NoarkFactory(
                journalpostService: journalpostService,
                partService:  partService,
                merknadService: Mock(MerknadService),
                nokkelordService: Mock(NokkelordService)
        )
        tilskuddFartoyFactory = new TilskuddFartoyFactory(
                noarkFactory: noarkFactory
        )
        objectMapper = new ObjectMapper()
        queryResult = objectMapper.readValue(this.getClass().getResourceAsStream('/json/query-result.json'), QueryResult)
    }

    def "Convert from Documaster case to Tilskudd fartoy"() {
        given:
        def caseResult = queryResult.results.find { it.type == 'Saksmappe' && it.fields.mappeIdent == '2019/1' }

        when:
        def fint = tilskuddFartoyFactory.toFintResource(caseResult)

        then:
        fint
        fint.getMappeId().identifikatorverdi == "2019/1"
    }
}
