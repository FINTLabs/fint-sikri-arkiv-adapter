package no.fint.sikri.data.noark.sak;


import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.data.utilities.QueryUtils;
import no.fint.model.resource.administrasjon.arkiv.SakResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class SakFactory {

    @Autowired
    private NoarkFactory noarkFactory;

    public SakResource toFintResource(CaseType result) {
        return noarkFactory.applyValuesForSaksmappe(result, new SakResource());
    }

    public List<SakResource> toFintResourceList(List<CaseType> cases) {

        return cases.stream().map(this::toFintResource).collect(Collectors.toList());

//        List<SakResource> result = new ArrayList<>(cases.size());
//        for (CaseType caseResult : cases) {
//            result.add(toFintResource(caseResult));
//        }
//        return result;
    }
    /*

    public QueryInput getQueryInputFromQueryParams(Map<String, Object> params) {
        QueryInput queryInput = new QueryInput();
        queryInput.setType("Saksmappe");
        queryInput.setOffset(0);
        Parameters parameters = new Parameters();
        Stream.Builder<String> query = Stream.builder();
        if (params.containsKey("title")) {
            query.add("tittel %= @title");
            parameters.setAdditionalProperty("@title", params.get("title"));
        }
        if (params.containsKey("date")) {
            query.add("saksdato = [@start:@end]");
            parameters.setAdditionalProperty("@start", params.get("date"));
            parameters.setAdditionalProperty("@end", "*");
        }
        if (params.containsKey("status")) {
            query.add("saksstatus = @status");
            parameters.setAdditionalProperty("@status", params.get("status"));
        }
        queryInput.setQuery(query.build().collect(Collectors.joining(" && ")));
        queryInput.setParameters(parameters);
        queryInput.setLimit(Integer.valueOf(String.valueOf(params.getOrDefault("maxResult", "10"))));
        log.debug("QueryInput: {}", queryInput);
        return queryInput;
    }

    public QueryInput getQueryInputFromMappeId(String mappeId) {
        return createQueryInput("mappeIdent", mappeId);
    }

    public QueryInput createQueryInput(String field, String value) {
        return QueryUtils.createQueryInput("Saksmappe", field, value);
    }

    public QueryInput getQueryInputFromSystemId(String systemId) {
        return createQueryInput("id", systemId);
    }
    
     */
}
