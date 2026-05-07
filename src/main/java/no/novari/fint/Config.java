package no.novari.fint;

import com.fasterxml.jackson.core.StreamReadConstraints;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    static {
        StreamReadConstraints
                .overrideDefaultStreamReadConstraints(StreamReadConstraints.builder()
                        .maxStringLength(Integer.MAX_VALUE).build());
    }
}
