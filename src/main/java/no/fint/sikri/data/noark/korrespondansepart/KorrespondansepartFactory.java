package no.fint.sikri.data.noark.korrespondansepart;

import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.arkiv.kodeverk.KorrespondansepartType;
import no.fint.model.felles.kodeverk.iso.Landkode;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.KorrespondansepartResource;
import no.fint.model.resource.felles.kompleksedatatyper.AdresseResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.SikriUtils.optionalValue;

@Service
public class KorrespondansepartFactory {
    public KorrespondansepartResource toFintResource(SenderRecipientType input) {
        KorrespondansepartResource output = new KorrespondansepartResource();

        optionalValue(input.getName()).ifPresent(output::setKorrespondansepartNavn);
        optionalValue(input.getEncryptedSsn()).ifPresent(output::setFodselsnummer);
        optionalValue(input.getAttention()).ifPresent(output::setKontaktperson);

        Kontaktinformasjon kontakt = new Kontaktinformasjon();
        optionalValue(input.getTelephone()).ifPresent(kontakt::setTelefonnummer);
        optionalValue(input.getEmail()).ifPresent(kontakt::setEpostadresse);
        if (!kontakt.equals(new Kontaktinformasjon())) {
            output.setKontaktinformasjon(kontakt);
        }

        AdresseResource adresse = new AdresseResource();
        optionalValue(input.getTwoLetterCountryCode()).map(Link.apply(Landkode.class, "systemid")).ifPresent(adresse::addLand);
        optionalValue(input.getPostalAddress()).map(Collections::singletonList).ifPresent(adresse::setAdresselinje);
        optionalValue(input.getPostalCode()).ifPresent(adresse::setPostnummer);
        optionalValue(input.getCity()).ifPresent(adresse::setPoststed);
        if (!adresse.equals(new AdresseResource())) {
            output.setAdresse(adresse);
        }

        if (input.isIsRecipient()) {
            output.addKorrespondanseparttype(Link.with(KorrespondansepartType.class, "systemid", "EM"));
        } else {
            output.addKorrespondanseparttype(Link.with(KorrespondansepartType.class, "systemid", "EA"));
        }

        return output;
    }

    public SenderRecipientType createSenderRecipient(KorrespondansepartResource input) {
        SenderRecipientType output = new SenderRecipientType();

        optionalValue(input.getKorrespondansepartNavn()).ifPresent(output::setName);
        optionalValue(input.getKontaktperson()).ifPresent(output::setAttention);

        if (StringUtils.isNotBlank(input.getFodselsnummer())) {
            output.setIdTypeId("FNR");
            output.setExternalId(input.getFodselsnummer());
        } else if (StringUtils.isNotBlank(input.getOrganisasjonsnummer())) {
            output.setIdTypeId("ORG");
            output.setExternalId(input.getOrganisasjonsnummer());
        }

        optionalValue(input.getKontaktinformasjon()).map(Kontaktinformasjon::getEpostadresse).ifPresent(output::setEmail);
        optionalValue(input.getKontaktinformasjon()).map(Kontaktinformasjon::getTelefonnummer).ifPresent(output::setTelephone);

        optionalValue(input.getAdresse()).map(AdresseResource::getPostnummer).ifPresent(output::setPostalCode);
        optionalValue(input.getAdresse()).map(AdresseResource::getPoststed).ifPresent(output::setCity);
        optionalValue(input.getAdresse()).map(AdresseResource::getAdresselinje).map(i -> String.join("\n")).ifPresent(output::setPostalAddress);
        optionalValue(input.getAdresse())
                .map(AdresseResource::getLand)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(Link::getHref)
                .map(i -> StringUtils.substringAfterLast(i, "/"))
                .findFirst()
                .ifPresent(output::setTwoLetterCountryCode);

        optionalValue(input.getKorrespondanseparttype())
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(Link::getHref)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .map("EM"::equals)
                .findFirst()
                .ifPresent(output::setIsRecipient);

        return output;
    }
}
