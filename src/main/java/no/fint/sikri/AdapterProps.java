package no.fint.sikri;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Getter
@Component
public class AdapterProps {

    @Value("${fint.sikri.database}")
    private String database;

    @Value("${fint.sikri.endpoint-base-url}")
    private String endpointBaseUrl;

    @Value("${fint.file-cache.directory:file-cache}")
    private Path cacheDirectory;

    @Value("${fint.file-cache.spec:expireAfterAccess=5m,expireAfterWrite=7m}")
    private String cacheSpec;

    @Value("${fint.validation.fatal:false}")
    private boolean fatalValidation;

}