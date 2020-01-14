package no.fint.sikri.data.utilities;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.felles.kompleksedatatyper.AdresseResource;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public final class FintUtils {

    private static final DateTimeFormatter formatter = createDateTimeFormatter();

    private static DateTimeFormatter createDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS][x]");
    }

    public static Identifikator createIdentifikator(String value) {
        Identifikator identifikator = new Identifikator();
        identifikator.setIdentifikatorverdi(value);
        return identifikator;
    }

    public static boolean validIdentifikator(Identifikator input) {
        return Objects.nonNull(input) && StringUtils.isNotBlank(input.getIdentifikatorverdi());
    }

    public static Date parseIsoDate(String value) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(value, formatter);
        return Date.from(zonedDateTime.toInstant());
    }

    public static Date parseDate(String value) {
        LocalDate localDate = LocalDate.parse(value);
        return Date.from(localDate.atStartOfDay().toInstant(ZoneOffset.UTC));
    }

//    public static Kontaktinformasjon createKontaktinformasjon(Fields fields) {
//        return getKontaktinformasjon(fields.getEpostadresse(), null, String.valueOf(fields.getAdditionalProperties().get("telefonnummer")));
//    }
//
//    public static AdresseResource createAdresse(Fields fields) {
//        AdresseResource adresse = new AdresseResource();
//        adresse.setAdresselinje(Collections.singletonList(fields.getPostadresse()));
//        adresse.setPostnummer(fields.getPostnummer());
//        adresse.setPoststed(fields.getPoststed());
//        return adresse;
//    }

    public static <T> Optional<T> optionalValue(T value) {
        return Optional.ofNullable(value);
    }

    // FIXME: 2019-05-08 Must handle if all three elements is empty. Then we should return null
    private static Kontaktinformasjon getKontaktinformasjon(String email, String mobilePhone, String phoneNumber) {
        Kontaktinformasjon kontaktinformasjon = new Kontaktinformasjon();
        optionalValue(email).ifPresent(kontaktinformasjon::setEpostadresse);
        optionalValue(mobilePhone).ifPresent(kontaktinformasjon::setMobiltelefonnummer);
        optionalValue(phoneNumber).ifPresent(kontaktinformasjon::setTelefonnummer);
        return kontaktinformasjon;
    }

}
