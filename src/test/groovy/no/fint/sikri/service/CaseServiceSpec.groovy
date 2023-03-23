package no.fint.sikri.service

import no.fint.antlr.FintFilterService
import no.fint.sikri.data.exception.IllegalOdataFilterProperty
import no.fint.sikri.model.SikriIdentity
import spock.lang.Specification

class CaseServiceSpec extends Specification {

    private CaseService caseService
    private SikriObjectModelService sikriObjectModelService

    void setup() {
        sikriObjectModelService = Mock(SikriObjectModelService)
        caseService = new CaseService(sikriObjectModelService,
                Mock(ExternalSystemLinkService), Mock(FintFilterService))
    }

    def "Validate mapping for supported ODataFilters"() {
        when:
        caseService.getCaseByODataFilter(Mock(SikriIdentity), odataFilter)

        then:
        1 * sikriObjectModelService.getDataObjects(_, _, sikriFilter, _, _) >> Arrays.asList()
        0 * sikriObjectModelService.getDataObjects(_, _, _, _, _) >> Arrays.asList()

        where:
        odataFilter                                       || sikriFilter
        "saksaar eq '2023'"                               || "CaseYear='2023'"
        "saksaar eq '2023' and sakssekvensnummer eq '27'" || "CaseYear='2023' AND SequenceNumber='27'"
        "sakssekvensnummer eq '27'"                       || "SequenceNumber='27'"
    }

    def "When unsupported ODataFilter property exception is thrown"() {
        when:
        caseService.getCaseByODataFilter(Mock(SikriIdentity), "neitakk eq '2000'")

        then:
        thrown(IllegalOdataFilterProperty)
    }

}
