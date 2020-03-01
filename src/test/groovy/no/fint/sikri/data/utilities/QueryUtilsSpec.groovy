package no.fint.sikri.data.utilities

import spock.lang.Specification

class QueryUtilsSpec extends Specification {

    def "Get query parameters"() {

        when:
        def params = QueryUtils.getQueryParams("?test1=test1&test2=test2")

        then:
        params
        params.size() == 2
        params.get("test1") == "test1"

    }
}
