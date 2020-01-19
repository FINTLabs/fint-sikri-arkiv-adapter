package no.fint.sikri.data.noark.journalpost;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.RegistryEntryType;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseService;
import no.fint.sikri.data.noark.korrespondansepart.KorrespondanseService;
import no.fint.sikri.data.noark.merknad.MerknadService;
import no.fint.sikri.data.noark.nokkelord.NokkelordService;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.QueryUtils;
import no.fint.sikri.repository.KodeverkRepository;
import no.fint.model.administrasjon.arkiv.Arkivressurs;
import no.fint.model.administrasjon.arkiv.JournalStatus;
import no.fint.model.administrasjon.arkiv.JournalpostType;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.JournalpostResource;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.FintUtils.validIdentifikator;

@Slf4j
@Service
public class JournalpostFactory {

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

    public JournalpostResource toFintResource(RegistryEntryType result) {
        JournalpostResource journalpost = new JournalpostResource();

        journalpost.setTittel(result.getTitleRestricted().getValue());
        journalpost.setOffentligTittel(result.getTitle().getValue());
        journalpost.setOpprettetDato(result.getCreatedDate().getValue().toGregorianCalendar().getTime());
        journalpost.setJournalDato(result.getRegistryDate().getValue().toGregorianCalendar().getTime());
        journalpost.setJournalAr(String.valueOf(result.getRegisterYear().getValue()));
        journalpost.setJournalPostnummer(Long.valueOf(result.getDocumentNumber().getValue()));
        journalpost.setJournalSekvensnummer(Long.valueOf(result.getSequenceNumber().getValue()));



        // FIXME: 2019-05-08 check for empty
        journalpost.setReferanseArkivDel(Collections.emptyList());

        // TODO: 2019-05-08 Check noark if this is correct
        journalpost.setForfatter(
                Stream.<String>builder()
                        .add(result.getCreatedByUserNameId().getValue().toString())
                        .add(result.getOfficerName().getValue().getName().getValue())
                        .build()
                        .distinct()
                        .collect(Collectors.toList()));

        journalpost.setKorrespondansepart(korrespondanseService.queryForRegistrering(result.getId().toString()));
        journalpost.setMerknad(merknadService.getRemarkForRegistryEntry(result.getId().toString()));

        journalpost.setDokumentbeskrivelse(dokumentbeskrivelseService.queryForJournalpost(result.getId().toString()));



        journalpost.addSaksbehandler(Link.with(Arkivressurs.class, "systemid", result.getOfficerNameId().getValue().toString()));
        journalpost.addOpprettetAv(Link.with(Arkivressurs.class, "systemid", result.getCreatedByUserNameId().getValue().toString()));
        journalpost.addJournalposttype(Link.with(JournalpostType.class, "systemid", result.getRegistryEntryTypeId().getValue()));
        journalpost.addJournalstatus(Link.with(JournalStatus.class, "systemid", result.getRecordStatusId().getValue()));

        return journalpost;
    }

}
