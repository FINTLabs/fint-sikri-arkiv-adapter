package no.fint.sikri.data.noark.journalpost;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.RegistryEntryType;
import no.fint.model.arkiv.kodeverk.JournalStatus;
import no.fint.model.arkiv.kodeverk.JournalpostType;
import no.fint.model.arkiv.noark.Arkivressurs;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.JournalpostResource;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseService;
import no.fint.sikri.data.noark.korrespondansepart.KorrespondansepartFactory;
import no.fint.sikri.data.noark.korrespondansepart.KorrespondansepartService;
import no.fint.sikri.data.noark.merknad.MerknadService;
import no.fint.sikri.data.noark.nokkelord.NokkelordService;
import no.fint.sikri.data.noark.skjerming.SkjermingService;
import no.fint.sikri.data.utilities.XmlUtils;
import no.fint.sikri.model.ElementsIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.SikriUtils.*;

@Slf4j
@Service
public class JournalpostFactory {

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

    public JournalpostResource toFintResource(ElementsIdentity identity, RegistryEntryType result) {
        JournalpostResource journalpost = new JournalpostResource();

        journalpost.setTittel(result.getTitleRestricted());
        journalpost.setOffentligTittel(result.getTitle());
        journalpost.setOpprettetDato(result.getCreatedDate().toGregorianCalendar().getTime());
        journalpost.setJournalDato(result.getRegistryDate().toGregorianCalendar().getTime());
        journalpost.setJournalAr(String.valueOf(result.getRegisterYear()));
        journalpost.setJournalPostnummer(Long.valueOf(result.getDocumentNumber()));
        journalpost.setJournalSekvensnummer(Long.valueOf(result.getSequenceNumber()));


        // FIXME: 2019-05-08 check for empty
        journalpost.setReferanseArkivDel(Collections.emptyList());

        // TODO: 2019-05-08 Check noark if this is correct
        journalpost.setForfatter(
                Stream.<String>builder()
                        .add(result.getCreatedByUserNameId().toString())
                        .add(result.getOfficerName().getName())
                        .build()
                        .distinct()
                        .collect(Collectors.toList()));

        journalpost.setKorrespondansepart(korrespondansepartService.queryForRegistrering(identity, result.getId().toString()));
        journalpost.setMerknad(merknadService.getRemarkForRegistryEntry(identity, result.getId().toString()));

        journalpost.setDokumentbeskrivelse(dokumentbeskrivelseService.queryForJournalpost(identity, result.getId().toString()));

        optionalValue(skjermingService.getSkjermingResource(result::getAccessCodeId, result::getPursuant))
                .ifPresent(journalpost::setSkjerming);

        journalpost.addSaksbehandler(Link.with(Arkivressurs.class, "systemid", result.getOfficerNameId().toString()));
        journalpost.addOpprettetAv(Link.with(Arkivressurs.class, "systemid", result.getCreatedByUserNameId().toString()));
        journalpost.addJournalposttype(Link.with(JournalpostType.class, "systemid", result.getRegistryEntryTypeId()));
        journalpost.addJournalstatus(Link.with(JournalStatus.class, "systemid", result.getRecordStatusId()));

        return journalpost;
    }

    public RegistryEntryDocuments toRegistryEntryDocuments(Integer caseId, JournalpostResource journalpostResource) {

        RegistryEntryType registryEntry = new RegistryEntryType();

        registryEntry.setCaseId(caseId);
        registryEntry.setTitle(journalpostResource.getTittel());
        registryEntry.setTitleRestricted(journalpostResource.getOffentligTittel());

        applyParameter(
                journalpostResource.getOpprettetDato(),
                registryEntry::setCreatedDate,
                xmlUtils::xmlDate
        );

        applyParameter(
                journalpostResource.getJournalDato(),
                registryEntry::setRegistryDate,
                xmlUtils::xmlDate
        );

        applyParameterFromLink(
                journalpostResource.getJournalposttype(),
                registryEntry::setRegistryEntryTypeId
        );

        applyParameterFromLink(
                journalpostResource.getJournalstatus(),
                registryEntry::setRecordStatusId
        );


        applyParameterFromLink(
                journalpostResource.getSaksbehandler(),
                Integer::parseInt,
                registryEntry::setOfficerNameId
        );

        applyParameterFromLink(
                journalpostResource.getOpprettetAv(),
                Integer::parseInt,
                registryEntry::setCreatedByUserNameId
        );

        skjermingService.applyAccessCodeAndPursuant(journalpostResource.getSkjerming(), registryEntry::setAccessCodeId, registryEntry::setPursuant);

        RegistryEntryDocuments result = new RegistryEntryDocuments(registryEntry);

        journalpostResource.getDokumentbeskrivelse()
                .stream()
                .map(dokumentbeskrivelseFactory::toDocumentDescription)
                .forEach(result::addDocument);

        if (journalpostResource.getKorrespondansepart() != null) {
            journalpostResource.getKorrespondansepart()
                    .stream()
                    .map(korrespondansepartFactory::createSenderRecipient)
                    .forEach(result::addSenderRecipient);
        }

        return result;
    }

}
