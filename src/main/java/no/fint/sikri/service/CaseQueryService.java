package no.fint.sikri.service;

import com.google.common.collect.ImmutableMap;
import no.fint.antlr.FintFilterService;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.sikri.model.SikriIdentity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;
import java.util.stream.Stream;

@Service
public class CaseQueryService {
    private static final String ODATA_FILTER_QUERY_OPTION = "$filter=";
    private final ImmutableMap<String, BiFunction<SikriIdentity, String, Stream<CaseType>>> queryMap;
    private final String[] validQueries;
    private final FintFilterService oDataFilterService;

    public CaseQueryService(CaseService caseService, FintFilterService oDataFilterService) {
        this.oDataFilterService = oDataFilterService;

        queryMap = new ImmutableMap.Builder<String, BiFunction<SikriIdentity, String, Stream<CaseType>>>()
                .put("mappeid/", caseService::getCaseByCaseNumber)
                .put("systemid/", caseService::getCaseBySystemId)
                .put("soknadsnummer/", caseService::getCaseByExternalKey)
                .put(ODATA_FILTER_QUERY_OPTION, caseService::getCaseByODataFilter)
                .put("?", caseService::getCaseByFilter)
                .build();
        validQueries = queryMap.keySet().toArray(new String[0]);
    }

    public boolean isValidQuery(String query) {
        return StringUtils.startsWithAny(StringUtils.lowerCase(query), validQueries) || isODataQuery(query);
    }

    public Stream<CaseType> query(SikriIdentity identity, String query) {
        for (String prefix : queryMap.keySet()) {
            if (StringUtils.startsWithIgnoreCase(query, prefix)) {
                return queryMap.get(prefix).apply(identity, StringUtils.removeStartIgnoreCase(query, prefix));
            }

            if (ODATA_FILTER_QUERY_OPTION.equals(prefix)) {
                return queryMap.get(prefix).apply(identity, query);
            }
        }

        throw new IllegalArgumentException("Invalid query: " + query);
    }

    private boolean isODataQuery(String query) {
        return oDataFilterService.validate(query);
    }
}
