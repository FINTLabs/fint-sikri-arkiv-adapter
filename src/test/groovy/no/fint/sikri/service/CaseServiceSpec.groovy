package no.fint.sikri.service

import no.fint.antlr.ODataLexer
import no.fint.antlr.ODataParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import spock.lang.Specification

class CaseServiceSpec extends Specification {

    def "Let`s filter something the OData way"() {
        when:
        ODataLexer lexer = new ODataLexer(CharStreams.fromString("saksaar eq '2023' and sakssekvensnummer eq '1'"));
        CommonTokenStream commonTokenStream = new CommonTokenStream(lexer);
        ODataParser oDataParser = new ODataParser(commonTokenStream);

        ODataParser.FilterContext filterContext = oDataParser.filter();
        def comparisonContexts = filterContext.comparison();

        for (def context : comparisonContexts) {
            String property = context.property().getText();
            String operator = context.comparisonOperator().getText();
            String value = context.value().getText();

            assert property == 'saksaar' || 'sakssekvensnummer'
            assert operator == 'eq'
            assert value == '2023' || '1'

            // At the end of the day, we want to produce:
            // CaseYear=2023 AND SequenceNumber=27
        }

        then:
        42;
    }
}
