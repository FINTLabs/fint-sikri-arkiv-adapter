package no.fint.sikri.data.noark.part;

import no.fint.arkiv.sikri.oms.CasePartyType;
import no.fint.model.felles.kodeverk.iso.Landkode;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.kodeverk.PartRolleResource;
import no.fint.model.resource.arkiv.noark.PartResource;
import no.fint.model.resource.felles.kompleksedatatyper.AdresseResource;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static no.fint.sikri.data.utilities.SikriUtils.optionalValue;

@Service
public class PartFactory {

    public PartResource toFintResource(CasePartyType input) {

        if (input == null) {
            return null;
        }

        PartResource output = new PartResource();

        optionalValue(input.getName()).ifPresent(output::setPartNavn);
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


        output.addPartRolle(Link.with(PartRolleResource.class, "systemid", input.getCasePartyRoleId()));
        return output;
    }
}
