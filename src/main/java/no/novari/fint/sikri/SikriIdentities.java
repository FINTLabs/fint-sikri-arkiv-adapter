package no.novari.fint.sikri;

import lombok.Data;
import no.novari.fint.sikri.model.SikriIdentity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties("fint.sikri.identity")
public class SikriIdentities {
    private Map<String, SikriIdentity> account;
    private Map<String, String> casetype;

}
