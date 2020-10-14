package no.fint.sikri.data.noark.journalpost;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.arkiv.sikri.oms.RegistryEntryType;
import no.fint.model.arkiv.kodeverk.JournalStatus;
import no.fint.model.arkiv.kodeverk.JournalpostType;
import no.fint.model.arkiv.noark.Arkivressurs;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.JournalpostResource;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseService;
import no.fint.sikri.data.noark.korrespondansepart.KorrespondanseService;
import no.fint.sikri.data.noark.merknad.MerknadService;
import no.fint.sikri.data.noark.nokkelord.NokkelordService;
import no.fint.sikri.data.utilities.XmlUtils;
import no.fint.sikri.repository.KodeverkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.SikriUtils.applyParameter;
import static no.fint.sikri.data.utilities.SikriUtils.applyParameterFromLink;

@Slf4j
@Service
public class JournalpostFactory {

    @Autowired
    private XmlUtils xmlUtils;

    @Autowired
    private KodeverkRepository kodeverkRepository;

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

    @Autowired
    private DokumentbeskrivelseService dokumentbeskrivelseService;

    @Autowired
    private KorrespondanseService korrespondanseService;

    @Autowired
    private MerknadService merknadService;

    @Autowired
    private NokkelordService nokkelordService;

    private ObjectFactory objectFactory = new ObjectFactory();

    public JournalpostResource toFintResource(RegistryEntryType result) {
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

        journalpost.setKorrespondansepart(korrespondanseService.queryForRegistrering(result.getId().toString()));
        journalpost.setMerknad(merknadService.getRemarkForRegistryEntry(result.getId().toString()));

        journalpost.setDokumentbeskrivelse(dokumentbeskrivelseService.queryForJournalpost(result.getId().toString()));


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

        RegistryEntryDocuments result = new RegistryEntryDocuments(registryEntry);

        journalpostResource.getDokumentbeskrivelse()
                .stream()
                .map(dokumentbeskrivelseFactory::toDocumentDescription)
                .forEach(result::addDocument);

        return result;
    }

}
