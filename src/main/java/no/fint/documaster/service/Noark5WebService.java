package no.fint.documaster.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.documaster.model.CodeLists;
import no.documaster.model.QueryInput;
import no.documaster.model.QueryResult;
import no.fint.documaster.data.utilities.ContentDisposition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class Noark5WebService {

    @Autowired
    @Qualifier("Documaster")
    private RestTemplate restTemplate;

    public CodeLists getCodeLists(String type, String field) {
        return restTemplate.getForObject("/rms/api/public/noark5/v1/code-lists?type={type}&field={field}", CodeLists.class, type, field);
    }

    public QueryResult query(QueryInput query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        log.debug("query: {}", query);
        ResponseEntity<QueryResult> result = restTemplate.exchange("/rms/api/public/noark5/v1/query", HttpMethod.POST, new HttpEntity<>(query, headers), QueryResult.class);
        log.debug("result: {}", result);
        return result.getBody();
    }

    public ResponseEntity<byte[]> download(String docId) {
        return restTemplate.getForEntity("/rms/api/public/noark5/v1/download?id={docId}", byte[].class, docId);
    }

    public String upload(String filename, MediaType contentType, byte[] data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.builder("attachment")
                        .filename(filename, StandardCharsets.UTF_8)
                        .build()
                        .toString());
        HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);
        ResponseEntity<JsonNode> result = restTemplate.exchange("/rms/api/public/noark5/v1/upload", HttpMethod.POST, entity, JsonNode.class);
        return result.getBody().get("id").asText();
    }
}
