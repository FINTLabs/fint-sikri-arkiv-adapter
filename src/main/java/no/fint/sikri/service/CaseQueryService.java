package no.fint.sikri.service;

import com.google.common.collect.ImmutableMap;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.sikri.model.ElementsIdentity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;
import java.util.stream.Stream;

@Service
public class CaseQueryService {
    private final ImmutableMap<String, BiFunction<ElementsIdentity, String, Stream<CaseType>>> queryMap;
    private final String[] validQueries;

    public CaseQueryService(CaseService caseService) {
        queryMap = new ImmutableMap.Builder<String, BiFunction<ElementsIdentity, String, Stream<CaseType>>>()
                .put("mappeid/", caseService::getCaseByCaseNumber)
                .put("systemid/", caseService::getCaseBySystemId)
                .put("soknadsnummer/", caseService::getCaseByExternalKey)
                .put("?", caseService::getCaseByFilter)
                .build();
        validQueries = queryMap.keySet().toArray(new String[0]);
    }

    public boolean isValidQuery(String query) {
        return StringUtils.startsWithAny(StringUtils.lowerCase(query), validQueries);
    }

    public Stream<CaseType> query(ElementsIdentity identity, String query) {
        for (String prefix : queryMap.keySet()) {
            if (StringUtils.startsWithIgnoreCase(query, prefix)) {
                return queryMap.get(prefix).apply(identity, StringUtils.removeStartIgnoreCase(query, prefix));
            }
        }
        throw new IllegalArgumentException("Invalid query: " + query);
    }

}
