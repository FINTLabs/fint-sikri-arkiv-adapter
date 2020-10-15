package no.fint.sikri.data.noark.korrespondansepart;

import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.arkiv.kodeverk.KorrespondansepartType;
import no.fint.model.felles.kodeverk.iso.Landkode;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.KorrespondansepartResource;
import no.fint.model.resource.felles.kompleksedatatyper.AdresseResource;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
}
