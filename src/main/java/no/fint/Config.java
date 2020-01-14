package no.fint;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;

@Configuration
public class Config {

    @Bean
    @Qualifier("Documaster")
    public RestTemplate createDocumasterRestTemplate(
            @Value("${fint.documaster.baseuri}") String baseuri,
            @Value("${fint.documaster.idpuri}") String idpuri,
            @Value("${fint.documaster.clientid}") String clientid,
            @Value("${fint.documaster.secret}") String secret,
            @Value("${fint.documaster.username}") String username,
            @Value("${fint.documaster.password}") String password,
            @Value("${fint.documaster.scopes}") String... scopes
    ) {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setAccessTokenUri(idpuri);
        resourceDetails.setClientId(clientid);
        resourceDetails.setClientSecret(secret);
        resourceDetails.setUsername(username);
        resourceDetails.setPassword(password);
        resourceDetails.setScope(Arrays.asList(scopes));
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails);
        restTemplate.setUriTemplateHandler(new RootUriTemplateHandler(baseuri));
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                System.err.println(response);
                super.handleError(response);
            }
        });
        return restTemplate;
    }

    @Bean
    @Qualifier("oauth2RestTemplate")
    @ConditionalOnProperty(name = "fint.oauth.enabled", havingValue = "false", matchIfMissing = true)
    public RestTemplate createProviderRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
