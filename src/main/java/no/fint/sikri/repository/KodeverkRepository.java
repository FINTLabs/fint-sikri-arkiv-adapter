package no.fint.sikri.repository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.arkiv.kodeverk.*;
import no.fint.model.resource.arkiv.noark.AdministrativEnhetResource;
import no.fint.model.resource.arkiv.noark.KlasseResource;
import no.fint.model.resource.arkiv.noark.KlassifikasjonssystemResource;
import no.fint.sikri.data.noark.administrativenhet.AdministrativEnhetService;
import no.fint.sikri.data.noark.codes.dokumentstatus.DokumentstatusService;
import no.fint.sikri.data.noark.codes.dokumenttype.DokumenttypeService;
import no.fint.sikri.data.noark.codes.format.FormatService;
import no.fint.sikri.data.noark.codes.journalposttype.JournalpostTypeService;
import no.fint.sikri.data.noark.codes.journalstatus.JournalStatusService;
import no.fint.sikri.data.noark.codes.korrespondanseparttype.KorrespondansepartTypeService;
import no.fint.sikri.data.noark.codes.merknadstype.MerknadstypeService;
import no.fint.sikri.data.noark.codes.partrolle.PartRolleService;
import no.fint.sikri.data.noark.codes.saksmappetype.SaksmappetypeService;
import no.fint.sikri.data.noark.codes.saksstatus.SaksStatusService;
import no.fint.sikri.data.noark.codes.skjermingshjemmel.SkjermingshjemmelService;
import no.fint.sikri.data.noark.codes.tilgangsrestriksjon.TilgangsrestriksjonService;
import no.fint.sikri.data.noark.codes.tilknyttetregistreringsom.TilknyttetRegistreringSomService;
import no.fint.sikri.data.noark.codes.variantformat.VariantformatService;
import no.fint.sikri.data.noark.klasse.KlassifikasjonssystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KodeverkRepository {

    @Autowired
    private SaksStatusService saksStatusService;

    @Autowired
    private DokumentstatusService dokumentstatusService;

    @Autowired
    private DokumenttypeService dokumenttypeService;

    @Autowired
    private JournalpostTypeService journalpostTypeService;

    @Autowired
    private KorrespondansepartTypeService korrespondansepartTypeService;

    @Autowired
    private PartRolleService partRolleService;

    @Autowired
    private JournalStatusService journalStatusService;

    @Autowired
    private TilknyttetRegistreringSomService tilknyttetRegistreringSomService;

    @Autowired
    private TilgangsrestriksjonService tilgangsrestriksjonService;

    @Autowired
    private SkjermingshjemmelService skjermingshjemmelService;

    @Autowired
    private MerknadstypeService merknadstypeService;

    @Autowired
    private VariantformatService variantformatService;

    @Autowired
    private FormatService formatService;

    @Autowired
    private KlassifikasjonssystemService klassifikasjonssystemService;

    @Autowired
    private AdministrativEnhetService administrativEnhetService;

    @Autowired
    private SaksmappetypeService saksmappetypeService;

    @Getter
    private List<SaksstatusResource> saksstatus;

    @Getter
    private List<DokumentStatusResource> dokumentStatus;

    @Getter
    private List<DokumentTypeResource> dokumentType;

    @Getter
    private List<JournalpostTypeResource> journalpostType;

    @Getter
    private List<KorrespondansepartTypeResource> korrespondansepartType;

    @Getter
    private List<PartRolleResource> partRolle;

    @Getter
    private List<TilknyttetRegistreringSomResource> tilknyttetRegistreringSom;

    @Getter
    private List<JournalStatusResource> journalStatus;

    @Getter
    private List<TilgangsrestriksjonResource> tilgangsrestriksjon;

    @Getter
    private List<SkjermingshjemmelResource> skjermingshjemmel;

    @Getter
    private List<MerknadstypeResource> merknadstype;

    @Getter
    private List<VariantformatResource> variantformat;

    @Getter
    private List<FormatResource> format;

    @Getter
    private List<KlasseResource> klasse;

    @Getter
    private List<KlassifikasjonssystemResource> klassifikasjonssystem;

    @Getter
    private List<AdministrativEnhetResource> administrativEnhet;

    @Getter
    private List<SaksmappetypeResource> saksmappetype;
    private transient boolean healthy = false;

    @Scheduled(initialDelay = 10000, fixedDelayString = "${fint.kodeverk.refresh-interval:1500000}")
    public void refresh() {
        saksstatus = saksStatusService.getCaseStatusTable().collect(Collectors.toList());
        dokumentStatus = dokumentstatusService.getDocumentStatusTable().collect(Collectors.toList());
        dokumentType = dokumenttypeService.getDocumenttypeTable().collect(Collectors.toList());
        journalpostType = journalpostTypeService.getDocumentCategoryTable().collect(Collectors.toList());
        korrespondansepartType = korrespondansepartTypeService.getKorrespondansepartType().collect(Collectors.toList());
        journalStatus = journalStatusService.getJournalStatusTable().collect(Collectors.toList());
        tilknyttetRegistreringSom = tilknyttetRegistreringSomService.getDocumentRelationTable().collect(Collectors.toList());
        partRolle = partRolleService.getPartRolle().collect(Collectors.toList());
        merknadstype = merknadstypeService.getMerknadstype().collect(Collectors.toList());
        tilgangsrestriksjon = tilgangsrestriksjonService.getAccessCodeTable().collect(Collectors.toList());
        skjermingshjemmel = skjermingshjemmelService.getStatutoryAuthority().collect(Collectors.toList());
        variantformat = variantformatService.getVariantFormatTable().collect(Collectors.toList());
        format = formatService.getFileFormatTable().collect(Collectors.toList());
        // TODO klasse = klasseService.getKlasser().collect(Collectors.toList());
        klassifikasjonssystem = klassifikasjonssystemService.getKlassifikasjonssystem().collect(Collectors.toList());
        administrativEnhet = administrativEnhetService.getAdministrativeEnheter().collect(Collectors.toList());
        saksmappetype = saksmappetypeService.getFileTypeTable().collect(Collectors.toList());
        log.info("Refreshed code lists");
        healthy = true;
    }

    public boolean health() {
        return healthy;
    }
}
