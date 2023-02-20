package no.fint.sikri.data.noark.journalpost;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.RegistryEntryType;
import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.arkiv.kodeverk.JournalStatus;
import no.fint.model.arkiv.kodeverk.JournalpostType;
import no.fint.model.arkiv.noark.Arkivressurs;
import no.fint.model.arkiv.noark.Avskrivning;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.JournalpostResource;
import no.fint.model.resource.arkiv.noark.KorrespondansepartResource;
import no.fint.sikri.data.noark.arkivressurs.ArkivressursService;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseService;
import no.fint.sikri.data.noark.korrespondansepart.KorrespondansepartFactory;
import no.fint.sikri.data.noark.korrespondansepart.KorrespondansepartService;
import no.fint.sikri.data.noark.merknad.MerknadService;
import no.fint.sikri.data.noark.nokkelord.NokkelordService;
import no.fint.sikri.data.noark.skjerming.SkjermingService;
import no.fint.sikri.data.utilities.XmlUtils;
import no.fint.sikri.model.SikriIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fint.sikri.data.noark.skjerming.SkjermingService.hasTilgangsrestriksjon;
import static no.fint.sikri.data.utilities.SikriUtils.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
public class JournalpostFactory {

    @Deprecated
    @Value("${fint.sikri.registry-entry.access-code.downgrade-code:}")
    private String downgradeCode;

    @Autowired
    private XmlUtils xmlUtils;

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

    @Autowired
    private DokumentbeskrivelseService dokumentbeskrivelseService;

    @Autowired
    private KorrespondansepartService korrespondansepartService;

    @Autowired
    private KorrespondansepartFactory korrespondansepartFactory;

    @Autowired
    private MerknadService merknadService;

    @Autowired
    private SkjermingService skjermingService;

    @Autowired
    private NokkelordService nokkelordService;

    @Autowired
    private ArkivressursService arkivressursService;

    public JournalpostResource toFintResource(SikriIdentity identity, RegistryEntryType result) {
        JournalpostResource journalpost = new JournalpostResource();

        journalpost.setTittel(result.getTitle());
        journalpost.setOffentligTittel(result.getTitleRestricted());
        journalpost.setOpprettetDato(result.getCreatedDate().toGregorianCalendar().getTime());
        journalpost.setJournalDato(result.getRegistryDate().toGregorianCalendar().getTime());
        journalpost.setJournalAr(String.valueOf(result.getRegisterYear()));
        journalpost.setJournalPostnummer(Long.valueOf(result.getDocumentNumber()));
        // TODO journalpost.setJournalSekvensnummer(Long.valueOf(result.getSequenceNumber()));


        // FIXME: 2019-05-08 check for empty
        journalpost.setReferanseArkivDel(Collections.emptyList());

        // TODO: 2019-05-08 Check noark if this is correct
        journalpost.setForfatter(Stream.<String>builder()
                .add(result.getCreatedByUserNameId().toString())
                .add(result.getOfficerName().getName())
                .build()
                .distinct()
                .collect(Collectors.toList()));

        optionalValue(skjermingService.getSkjermingResource(result::getAccessCodeId, result::getPursuant)).ifPresent(
                journalpost::setSkjerming);

        if (isNotBlank(result.getDowngradingCodeId())) {
            Avskrivning avskrivning = new Avskrivning();
            avskrivning.setAvskrivningsmate(result.getDowngradingCodeId());
            optionalValue(result.getDowngradedDate()).map(XmlUtils::javaDate).ifPresent(avskrivning::setAvskrivningsdato);
            journalpost.setAvskrivning(avskrivning);
        }

        journalpost.setKorrespondansepart(korrespondansepartService.queryForRegistrering(identity,
                result.getId().toString()).map(it -> {
            KorrespondansepartResource korrespondansepartResource = korrespondansepartFactory.toFintResource(it);
            if (result.isIsRestricted() && it.isIsRestricted()) {
                korrespondansepartResource.setSkjerming(journalpost.getSkjerming());
            }
            return korrespondansepartResource;
        }).collect(Collectors.toList()));
        journalpost.setMerknad(merknadService.getRemarkForRegistryEntry(identity, result.getId().toString()));

        journalpost.setDokumentbeskrivelse(dokumentbeskrivelseService.queryForJournalpost(identity,
                result.getId().toString()));

        journalpost.addSaksbehandler(Link.with(Arkivressurs.class, "systemid", result.getOfficerNameId().toString()));
        journalpost.addOpprettetAv(Link.with(Arkivressurs.class,
                "systemid",
                result.getCreatedByUserNameId().toString()));
        journalpost.addJournalposttype(Link.with(JournalpostType.class, "systemid", result.getRegistryEntryTypeId()));
        journalpost.addJournalstatus(Link.with(JournalStatus.class, "systemid", result.getRecordStatusId()));

        return journalpost;
    }

    public RegistryEntryDocuments toRegistryEntryDocuments(Integer caseId,
                                                           JournalpostResource journalpostResource,
                                                           String recordPrefix,
                                                           String documentPrefix) {

        RegistryEntryType registryEntry = new RegistryEntryType();

        registryEntry.setCaseId(caseId);
        registryEntry.setTitle(recordPrefix + journalpostResource.getTittel());

        // Offentlig tittel, straight through just as is, if it is
        optionalValue(journalpostResource.getOffentligTittel())
                .map(String::valueOf)
                .ifPresent(registryEntry::setTitleRestricted);

        applyParameter(journalpostResource.getOpprettetDato(), registryEntry::setCreatedDate, xmlUtils::xmlDate);

        applyParameter(journalpostResource.getJournalDato(), registryEntry::setRegistryDate, xmlUtils::xmlDate);

        applyParameterFromLink(journalpostResource.getJournalposttype(), registryEntry::setRegistryEntryTypeId);

        applyParameterFromLink(journalpostResource.getJournalstatus(), registryEntry::setRecordStatusId);


        applyParameterFromLink(journalpostResource.getSaksbehandler(),
                arkivressursService::lookupUserId,
                registryEntry::setOfficerNameId);

        applyParameterFromLink(journalpostResource.getAdministrativEnhet(),
                Integer::parseUnsignedInt,
                registryEntry::setAdministrativeUnitId);

        applyParameterFromLink(journalpostResource.getJournalenhet(), registryEntry::setRegistryManagementUnitId);

        applyParameterFromLink(journalpostResource.getOpprettetAv(),
                Integer::parseUnsignedInt,
                registryEntry::setCreatedByUserNameId);

        if (skjermingService.applyAccessCodeAndPursuant(journalpostResource.getSkjerming(),
                registryEntry::setAccessCodeId,
                registryEntry::setPursuant)) {
            // 🦍 If access code and pursuant was applied ..
            if (isNotBlank(downgradeCode)) {
                // 🦧 .. and a downgrading code has been set ..
                registryEntry.setDowngradingCodeId(downgradeCode);
                // 🐖 .. set it and hope for the best.
            }
        }

        optionalValue(journalpostResource.getAvskrivning()).map(Avskrivning::getAvskrivningsmate)
                .ifPresent(registryEntry::setDowngradingCodeId);
        optionalValue(journalpostResource.getAvskrivning()).map(Avskrivning::getAvskrivningsdato)
                .map(xmlUtils::xmlDate)
                .ifPresent(registryEntry::setDowngradedDate);

        RegistryEntryDocuments result = new RegistryEntryDocuments(registryEntry);

        journalpostResource.getDokumentbeskrivelse()
                .stream()
                .map(dokumentbeskrivelseResource -> dokumentbeskrivelseFactory.toDocumentDescription(
                        dokumentbeskrivelseResource,
                        documentPrefix))
                .forEach(result::addDocument);

        /*
        The rules for IsRestricted are as follows:
        1. IsRestricted on RegistryEntry can only be set if AccessCode and Pursuant is set.
        2. IsRestricted on SenderRecipient can only be set if IsRestricted on RegistryEntry is set.
         */
        if (journalpostResource.getKorrespondansepart() != null) {
            journalpostResource.getKorrespondansepart().stream().map(input -> {
                SenderRecipientType senderRecipient = korrespondansepartFactory.createSenderRecipient(input,
                        registryEntry.getOfficerNameId(),
                        registryEntry.getAdministrativeUnitId(),
                        registryEntry.getRegistryManagementUnitId());
                if (hasTilgangsrestriksjon(journalpostResource.getSkjerming()) && hasTilgangsrestriksjon(input.getSkjerming())) {
                    registryEntry.setIsRestricted(true);
                    senderRecipient.setIsRestricted(true);
                }
                return senderRecipient;
            }).forEach(result::addSenderRecipient);
        }

        return result;
    }

}
