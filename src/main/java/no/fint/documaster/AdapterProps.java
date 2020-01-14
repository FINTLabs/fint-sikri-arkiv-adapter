package no.fint.documaster;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Getter
@Component
public class AdapterProps {

    @Value("${fint.file-cache.directory:file-cache}")
    private Path cacheDirectory;

    @Value("${fint.file-cache.spec:expireAfterAccess=5m,expireAfterWrite=7m}")
    private String cacheSpec;

}