package no.fint.documaster;

import lombok.Data;
import no.fint.documaster.data.CaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "fint.documaster.defaults")
public class CaseDefaults {
    private Map<String, CaseProperties> casetype;
}
