package no.novari.fint.sikri.data.utilities;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CasePartyType;
import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.novari.fint.model.felles.kompleksedatatyper.Identifikator;
import no.novari.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.novari.fint.model.felles.kompleksedatatyper.Personnavn;
import no.novari.fint.model.resource.Link;
import no.novari.fint.model.resource.felles.kompleksedatatyper.AdresseResource;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class FintUtils {

    public static Identifikator createIdentifikator(Integer id) {
        if (id == null) {
            return null;
        }
        return createIdentifikator(String.valueOf(id));
    }

    public static Identifikator createIdentifikator(String value) {
        if (value == null) {
            return null;
        }
        Identifikator identifikator = new Identifikator();
        identifikator.setIdentifikatorverdi(value);
        return identifikator;
    }

    public static boolean validIdentifikator(Identifikator identifikator) {
        return Objects.nonNull(identifikator) && StringUtils.isNotBlank(identifikator.getIdentifikatorverdi());
    }

    public static AdresseResource createAdresse(CasePartyType caseParty) {
        AdresseResource adresseResource = new AdresseResource();


        adresseResource.setAdresselinje(Collections.singletonList(caseParty.getPostalAddress()));
        adresseResource.setPostnummer(caseParty.getPostalCode());
        adresseResource.setPoststed(caseParty.getCity());

        return adresseResource;
    }

    public static AdresseResource createAdresse(SenderRecipientType senderRecipient) {
        AdresseResource adresseResource = new AdresseResource();


        adresseResource.setAdresselinje(Collections.singletonList(senderRecipient.getPostalAddress()));
        adresseResource.setPostnummer(senderRecipient.getPostalCode());
        adresseResource.setPoststed(senderRecipient.getCity());

        return adresseResource;
    }

    public static Kontaktinformasjon createKontaktinformasjon(CasePartyType result) {
        return getKontaktinformasjon(result.getEmail(), result.getTelephone());
    }

    public static Kontaktinformasjon createKontaktinformasjon(SenderRecipientType result) {
        return getKontaktinformasjon(result.getEmail(), result.getTelephone());
    }

    private static Kontaktinformasjon getKontaktinformasjon(String email, String phoneNumber) {
        if (StringUtils.isBlank(email) && StringUtils.isBlank(phoneNumber)) {
            return null;
        }

        Kontaktinformasjon kontaktinformasjon = new Kontaktinformasjon();
        SikriUtils.optionalValue(email).ifPresent(kontaktinformasjon::setEpostadresse);
        SikriUtils.optionalValue(phoneNumber).ifPresent(kontaktinformasjon::setTelefonnummer);

        return kontaktinformasjon;
    }

    public static String getFullnameFromPersonnavn(Personnavn personnavn) {
        return Stream.<String>builder()
                .add(personnavn.getFornavn())
                .add(personnavn.getMellomnavn())
                .add(personnavn.getEtternavn())
                .build()
                .filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
    }

    public static void applyIdFromLink(List<Link> links, Consumer<String> consumer) {
        links.stream()
                .filter(Objects::nonNull)
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .forEach(consumer);
    }

    public static Optional<String> getIdFromLink(List<Link> links){
        return links
                .stream()
                .filter(Objects::nonNull)
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .findFirst();
    }
}
