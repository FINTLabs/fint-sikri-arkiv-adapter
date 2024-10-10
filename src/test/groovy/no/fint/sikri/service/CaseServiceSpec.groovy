package no.fint.sikri.service


import no.fint.sikri.data.exception.IllegalOdataFilter
import no.fint.sikri.model.SikriIdentity
import spock.lang.Specification

class CaseServiceSpec extends Specification {

    private CaseService caseService
    private SikriObjectModelService sikriObjectModelService

    void setup() {
        sikriObjectModelService = Mock(SikriObjectModelService)
        caseService = new CaseService(sikriObjectModelService,
                Mock(ExternalSystemLinkService))
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
        "saksdato eq '01-01-1970'"                        || "CaseDate='01-01-1970'"
        "oppdatert eq '01-01-1970'"                       || "LastRecordDate='01-01-1970'"
        "arkivdel eq 'Drosje'"                            || "SeriesId='Drosje'"
        "administrativenhet eq '42'"                      || "AdministrativeUnitId='42'"
        "tilgangskode eq 'UO'"                            || "AccessCodeId='UO'"
        "saksmappetype eq 'SAK'"                          || "FileTypeId='SAK'"
        "tittel eq 'Drosjeløyvesøknad'"                   || "Title='Drosjeløyvesøknad'"
        "mappeid eq '2023/12345'"                         || "CaseYear='2023' AND SequenceNumber='12345'"
        "systemid eq '123456'"                            || "Id='123456'"
        "klassifikasjon/primar/ordning eq 'ORG'"          || "PrimaryClassification.ClassificationSystemId='ORG'"
        "klassifikasjon/primar/verdi eq '888888888'"      || "PrimaryClassification.ClassId='888888888'"
        "klassifikasjon/sekundar/ordning eq 'EMNE'"       || "SecondaryClassification.ClassificationSystemId='EMNE'"
        "klassifikasjon/sekundar/verdi eq 'N12'"          || "SecondaryClassification.ClassId='N12'"
    }

    def "When unsupported ODataFilter property exception is thrown"() {
        when:
        caseService.getCaseByODataFilter(Mock(SikriIdentity), "neitakk eq '2000'")

        then:
        thrown(IllegalOdataFilter)
    }

    def "When unsupported ODataFilter operator exception is thrown"() {
        when:
        caseService.getCaseByODataFilter(Mock(SikriIdentity), "tittel ne 'NAV'")

        then:
        thrown(IllegalOdataFilter)
    }

}
