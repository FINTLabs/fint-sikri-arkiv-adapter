package no.fint.sikri.data.noark.journalpost;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.arkiv.sikri.oms.RegistryEntryType;
import no.fint.model.arkiv.noark.Arkivressurs;
import no.fint.model.arkiv.noark.JournalStatus;
import no.fint.model.arkiv.noark.JournalpostType;
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

    public RegistryEntryDocuments toRegistryEntryDocuments(Integer caseId, JournalpostResource journalpostResource) {
        RegistryEntryType registryEntry = objectFactory.createRegistryEntryType();

        registryEntry.setCaseId(objectFactory.createRemarkTypeCaseId(caseId));
        registryEntry.setTitle(objectFactory.createRegistryEntryTypeTitle(journalpostResource.getTittel()));
        registryEntry.setTitleRestricted(objectFactory.createRegistryEntryTypeTitleRestricted(journalpostResource.getOffentligTittel()));

        applyParameter(
                journalpostResource.getOpprettetDato(),
                objectFactory::createRegistryEntryTypeCreatedDate,
                registryEntry::setCreatedDate,
                xmlUtils::xmlDate
        );

        applyParameter(
                journalpostResource.getJournalDato(),
                objectFactory::createRegistryEntryTypeRegistryDate,
                registryEntry::setRegistryDate,
                xmlUtils::xmlDate
        );

        applyParameterFromLink(
                journalpostResource.getJournalposttype(),
                objectFactory::createRegistryEntryTypeRegistryEntryTypeId,
                registryEntry::setRegistryEntryTypeId
        );

        applyParameterFromLink(
                journalpostResource.getJournalstatus(),
                objectFactory::createRegistryEntryTypeRecordStatusId,
                registryEntry::setRecordStatusId
        );


        applyParameterFromLink(
                journalpostResource.getSaksbehandler(),
                v -> objectFactory.createRegistryEntryTypeOfficerNameId(Integer.parseInt(v)),
                registryEntry::setOfficerNameId
        );

        applyParameterFromLink(
                journalpostResource.getOpprettetAv(),
                v -> objectFactory.createRegistryEntryTypeCreatedByUserNameId(Integer.parseInt(v)),
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
