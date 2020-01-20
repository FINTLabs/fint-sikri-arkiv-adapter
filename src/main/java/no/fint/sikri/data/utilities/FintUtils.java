package no.fint.sikri.data.utilities;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CasePartyType;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.felles.kompleksedatatyper.AdresseResource;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public static AdresseResource createAdresse(CasePartyType caseParty) {
        AdresseResource adresseResource = new AdresseResource();


        adresseResource.setAdresselinje(Collections.singletonList(caseParty.getPostalAddress().getValue()));
        adresseResource.setPostnummer(caseParty.getPostalCode().getValue());
        adresseResource.setPoststed(caseParty.getCity().getValue());

        return adresseResource;
    }
    public static Kontaktinformasjon createKontaktinformasjon(CasePartyType result) {
        return getKontaktinformasjon(result.getEmail(), result.getTelephone());
    }
    private static Kontaktinformasjon getKontaktinformasjon(JAXBElement<String> email, JAXBElement<String> phoneNumber) {
        if (email.isNil() && phoneNumber.isNil()) {
            return null;
        }

        Kontaktinformasjon kontaktinformasjon = new Kontaktinformasjon();
        optionalValue(email).ifPresent(kontaktinformasjon::setEpostadresse);
        optionalValue(phoneNumber).ifPresent(kontaktinformasjon::setTelefonnummer);

        return kontaktinformasjon;
    }


}
