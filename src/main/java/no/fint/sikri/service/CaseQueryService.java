package no.fint.sikri.service;

import com.google.common.collect.ImmutableMap;
import no.fint.arkiv.sikri.oms.CaseType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Stream;

@Service
public class CaseQueryService {
    private final ImmutableMap<String, Function<String, Stream<CaseType>>> queryMap;
    private final String[] validQueries;

    public CaseQueryService(CaseService caseService) {
        queryMap = new ImmutableMap.Builder<String, Function<String, Stream<CaseType>>>()
                .put("mappeid/", caseService::getCaseByCaseNumber)
                .put("systemid/", caseService::getCaseBySystemId)
                .put("?", caseService::getCaseByFilter)
                .build();
        validQueries = queryMap.keySet().toArray(new String[0]);
    }

    public boolean isValidQuery(String query) {
        return StringUtils.startsWithAny(StringUtils.lowerCase(query), validQueries);
    }

    public Stream<CaseType> query(String query) {
        for (String prefix : queryMap.keySet()) {
            if (StringUtils.startsWithIgnoreCase(query, prefix)) {
                return queryMap.get(prefix).apply(StringUtils.removeStartIgnoreCase(query, prefix));
            }
        }
        throw new IllegalArgumentException("Invalid query: " + query);
    }

}
