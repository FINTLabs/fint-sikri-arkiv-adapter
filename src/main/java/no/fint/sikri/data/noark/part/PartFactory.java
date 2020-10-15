package no.fint.sikri.data.noark.part;

import no.fint.arkiv.sikri.oms.CasePartyType;
import no.fint.model.felles.kodeverk.iso.Landkode;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.kodeverk.PartRolleResource;
import no.fint.model.resource.arkiv.noark.PartResource;
import no.fint.model.resource.felles.kompleksedatatyper.AdresseResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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

    public CasePartyType createCaseParty(Integer caseId, PartResource input) {
        CasePartyType output = new CasePartyType();

        output.setCaseId(caseId);
        optionalValue(input.getPartNavn()).ifPresent(output::setName);
        optionalValue(input.getKontaktperson()).ifPresent(output::setAttention);

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

        return output;
    }
}
