package no.fint.sikri.data.noark.korrespondansepart;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.arkiv.kodeverk.KorrespondansepartType;
import no.fint.model.felles.kodeverk.iso.Landkode;
import no.fint.model.felles.kompleksedatatyper.Kontaktinformasjon;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.KorrespondansepartResource;
import no.fint.model.resource.felles.kompleksedatatyper.AdresseResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.SikriUtils.optionalValue;

@Service
@Slf4j
public class KorrespondansepartFactory {

    @Value("${fint.sikri.skip-internal-contacts:false}")
    private Boolean skipInternalContacts;

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
        optionalValue(input.getTwoLetterCountryCode()).map(Link.apply(Landkode.class, "systemid"))
                .ifPresent(adresse::addLand);
        optionalValue(input.getPostalAddress()).map(Collections::singletonList).ifPresent(adresse::setAdresselinje);
        optionalValue(input.getPostalCode()).ifPresent(adresse::setPostnummer);
        optionalValue(input.getCity()).ifPresent(adresse::setPoststed);
        if (!adresse.equals(new AdresseResource())) {
            output.setAdresse(adresse);
        }

        String recipientType = "";

        if (hasValue(input.getAdministrativeUnitId()) || hasValue(input.getOfficerNameId())) {
            recipientType += "I";
        } else {
            recipientType += "E";
        }

        if (input.isCopyRecipient()) {
            recipientType += "K";
        } else if (input.isIsRecipient()) {
            recipientType += "M";
        } else {
            recipientType += "A";
        }

        output.addKorrespondanseparttype(Link.with(KorrespondansepartType.class, "systemid", recipientType));

        log.debug("AdministrativeUnitId: {}, OfficerNameId: {}, IsCopyRecipient: {}, IsRecipient: {}",
                input.getAdministrativeUnitId(), input.getOfficerNameId(), input.isCopyRecipient(), input.isIsRecipient());
        log.debug("Based on the input, the recipientType is set to {}, and the KorrespondansepartType to: {}. Size: {}",
                recipientType, output.getKorrespondanseparttype(), output.getKorrespondanseparttype().size());

        return output;
    }

    private boolean hasValue(Integer value) {
        return value != null && value != 0;
    }

    public SenderRecipientType createSenderRecipient(KorrespondansepartResource input,
                                                     Integer officerNameId,
                                                     Integer administrativeUnitId,
                                                     String registryManagementUnitId) {
        SenderRecipientType output = new SenderRecipientType();

        optionalValue(input.getKorrespondansepartNavn()).ifPresent(output::setName);
        optionalValue(input.getKontaktperson()).ifPresent(output::setAttention);

        if (StringUtils.isNotBlank(input.getFodselsnummer())) {
            output.setIdTypeId("FNR");
            output.setExternalId(input.getFodselsnummer());
            output.setUser(Boolean.TRUE);
        } else if (StringUtils.isNotBlank(input.getOrganisasjonsnummer())) {
            output.setIdTypeId("ORG");
            output.setExternalId(input.getOrganisasjonsnummer());
        }

        optionalValue(input.getKontaktinformasjon()).map(Kontaktinformasjon::getEpostadresse)
                .ifPresent(output::setEmail);
        optionalValue(input.getKontaktinformasjon()).map(Kontaktinformasjon::getTelefonnummer)
                .ifPresent(output::setTelephone);

        optionalValue(input.getAdresse()).map(AdresseResource::getPostnummer).ifPresent(output::setPostalCode);
        optionalValue(input.getAdresse()).map(AdresseResource::getPoststed).ifPresent(output::setCity);
        optionalValue(input.getAdresse()).map(AdresseResource::getAdresselinje)
                .map(i -> String.join(", ", i))
                .ifPresent(output::setPostalAddress);
        optionalValue(input.getAdresse()).map(AdresseResource::getLand)
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(Link::getHref)
                .map(i -> StringUtils.substringAfterLast(i, "/"))
                .findFirst()
                .ifPresent(output::setTwoLetterCountryCode);

        String[] expectedContactTypes = skipInternalContacts ? new String[]{"EA", "EM", "EK"} : new String[]{"EA", "EM", "EK", "IA", "IM", "IK"};
        log.debug("The boolean skipInternalContacts is set to {} resulting in these expected contact types: {}",
                skipInternalContacts, expectedContactTypes);

        optionalValue(input.getKorrespondanseparttype()).map(List::stream)
                .orElseGet(Stream::empty)
                .map(Link::getHref)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .map(StringUtils::upperCase)
                .filter(s -> StringUtils.equalsAny(s, expectedContactTypes))
                .findFirst()
                .ifPresent(type -> {
                    log.trace("Currently giving the type '{}' some TLC.", type);
                    output.setCopyRecipient(false);
                    output.setIsRecipient(false);
                    output.setIsResponsible(false);
                    if (StringUtils.startsWith(type, "E")) {
                        output.setOfficerNameId(0);
                        output.setAdministrativeUnitId(0);
                    } else if (StringUtils.startsWith(type, "I")) {
                        output.setIsResponsible(true);
                        output.setOfficerNameId(officerNameId);
                        output.setAdministrativeUnitId(administrativeUnitId);
                        output.setRegistryManagementUnitId(registryManagementUnitId);
                    }
                    if (StringUtils.endsWith(type, "K")) {
                        output.setCopyRecipient(true);
                        output.setIsRecipient(true);
                        output.setIsResponsible(false);
                    } else if (StringUtils.endsWith(type, "M")) {
                        output.setIsRecipient(true);
                    }
                });

        log.debug("About to return the SenderRecipientType. IsRecipient: {}, OfficerNameId: {}, AdministrativeUnitId: {}.",
                output.isIsRecipient(), output.getOfficerNameId(), output.getAdministrativeUnitId());
        log.debug("The SenderRecipientType was made based on this input (KorrespondansepartResource): {}",
                input.getKorrespondanseparttype());

        return output;
    }
}
