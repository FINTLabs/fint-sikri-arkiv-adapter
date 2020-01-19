package no.fint.sikri.data.utilities;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public final class FintUtils {


    public static Identifikator createIdentifikator(String value) {
        Identifikator identifikator = new Identifikator();
        identifikator.setIdentifikatorverdi(value);
        return identifikator;
    }

    public static boolean validIdentifikator(Identifikator input) {
        return Objects.nonNull(input) && StringUtils.isNotBlank(input.getIdentifikatorverdi());
    }

    public static <T> Optional<T> optionalValue(JAXBElement<T> element) {
        if (!element.isNil()) {
            return Optional.of(element.getValue());
        }
        return Optional.empty();
    }


}
