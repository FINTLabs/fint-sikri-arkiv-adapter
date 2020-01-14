package no.fint.sikri.data.utilities;

import org.jooq.lambda.Unchecked;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.util.Map;
import java.util.stream.Collectors;

public enum QueryUtils {
    ;

    public static Map<String, Object> getQueryParams(String query) {
        return UriComponentsBuilder.fromUriString(query)
                .build()
                .getQueryParams()
                .toSingleValueMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Unchecked.function(e -> URLDecoder.decode(e.getValue(), "UTF-8"))));
    }

//    public static QueryInput createQueryInput(String type, Map<String, Object> criteria) {
//        QueryInput queryInput = new QueryInput();
//        queryInput.setType(type);
//        queryInput.setOffset(0);
//        queryInput.setLimit(100);
//
//        Parameters parameters = new Parameters();
//        Stream.Builder<String> query = Stream.builder();
//        AtomicInteger p = new AtomicInteger();
//
//        criteria.forEach((field, value) -> {
//            String k = String.format("@parm%d", p.incrementAndGet());
//            query.add(field + " = " + k);
//            parameters.setAdditionalProperty(k, value);
//        });
//
//        queryInput.setQuery(query.build().collect(Collectors.joining(" && ")));
//        queryInput.setParameters(parameters);
//
//        return queryInput;
//    }
//
//    public static QueryInput createQueryInput(String type, String field, Object value) {
//        return createQueryInput(type, Collections.singletonMap(field, value));
//    }
}
