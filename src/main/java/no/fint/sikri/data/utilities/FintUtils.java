package no.fint.sikri.data.utilities;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CasePartyType;
import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.felles.kompleksedatatyper.Personnavn;
import no.fint.model.resource.Link;
import no.fint.model.resource.felles.kompleksedatatyper.AdresseResource;
import no.fint.sikri.data.exception.UnableToGetIdFromLink;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public final class FintUtils {

    public static Identifikator createIdentifikator(String value) {
        Identifikator identifikator = new Identifikator();
        identifikator.setIdentifikatorverdi(value);
        return identifikator;
    }

    public static boolean validIdentifikator(Identifikator identifikator) {
        return Objects.nonNull(identifikator) && StringUtils.isNotBlank(identifikator.getIdentifikatorverdi());
    }

    public static AdresseResource createAdresse(CasePartyType caseParty) {
        AdresseResource adresseResource = new AdresseResource();


        adresseResource.setAdresselinje(Collections.singletonList(caseParty.getPostalAddress().getValue()));
        adresseResource.setPostnummer(caseParty.getPostalCode().getValue());
        adresseResource.setPoststed(caseParty.getCity().getValue());

        return adresseResource;
    }

    public static AdresseResource createAdresse(SenderRecipientType senderRecipient) {
        AdresseResource adresseResource = new AdresseResource();


        adresseResource.setAdresselinje(Collections.singletonList(senderRecipient.getPostalAddress().getValue()));
        adresseResource.setPostnummer(senderRecipient.getPostalCode().getValue());
        adresseResource.setPoststed(senderRecipient.getCity().getValue());

        return adresseResource;
    }

    public static Kontaktinformasjon createKontaktinformasjon(CasePartyType result) {
        return getKontaktinformasjon(result.getEmail(), result.getTelephone());
    }

    public static Kontaktinformasjon createKontaktinformasjon(SenderRecipientType result) {
        return getKontaktinformasjon(result.getEmail(), result.getTelephone());
    }

    private static Kontaktinformasjon getKontaktinformasjon(JAXBElement<String> email, JAXBElement<String> phoneNumber) {
        if (email.isNil() && phoneNumber.isNil()) {
            return null;
        }

        Kontaktinformasjon kontaktinformasjon = new Kontaktinformasjon();
        SikriUtils.optionalValue(email).ifPresent(kontaktinformasjon::setEpostadresse);
        SikriUtils.optionalValue(phoneNumber).ifPresent(kontaktinformasjon::setTelefonnummer);

        return kontaktinformasjon;
    }

    public static String getFullnameFromPersonnavn(Personnavn personnavn) {
        return Stream.<String>builder()
                .add(personnavn.getEtternavn())
                .add(personnavn.getFornavn())
                .add(personnavn.getMellomnavn())
                .build()
                .filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
    }


    public static String getIdFromLink(List<Link> links) throws UnableToGetIdFromLink {
        return links
                .stream()
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .findAny()
                .orElseThrow(() -> new UnableToGetIdFromLink("Unable to get ID from link."));
    }
}
