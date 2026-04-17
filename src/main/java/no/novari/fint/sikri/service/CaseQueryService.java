package no.novari.fint.sikri.service;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.sikri.oms.CaseType;
import no.novari.fint.sikri.model.SikriIdentity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;
import java.util.stream.Stream;

@Service
@Slf4j
public class CaseQueryService {
    private final ImmutableMap<String, BiFunction<SikriIdentity, String, Stream<CaseType>>> queryMap;
    private final String[] validQueries;

    public CaseQueryService(CaseService caseService) {
        queryMap = new ImmutableMap.Builder<String, BiFunction<SikriIdentity, String, Stream<CaseType>>>()
                .put("mappeid/", caseService::getCaseByCaseNumber)
                .put("systemid/", caseService::getCaseBySystemId)
                .put("soknadsnummer/", caseService::getCaseByExternalKey)
                .put("$filter=", caseService::getCaseByODataFilter)
                .put("?", caseService::getCaseByFilter)
                .build();
        validQueries = queryMap.keySet().toArray(new String[0]);
    }

    public boolean isValidQuery(String query) {
        log.debug("Currently validating this query: {}", query);
        return StringUtils.startsWithAny(StringUtils.lowerCase(query), validQueries);
    }

    public Stream<CaseType> query(SikriIdentity identity, String query) {
        for (String prefix : queryMap.keySet()) {
            if (StringUtils.startsWithIgnoreCase(query, prefix)) {
                return queryMap.get(prefix).apply(identity, StringUtils.removeStartIgnoreCase(query, prefix));
            }
        }

        throw new IllegalArgumentException("Invalid query: " + query);
    }
}
