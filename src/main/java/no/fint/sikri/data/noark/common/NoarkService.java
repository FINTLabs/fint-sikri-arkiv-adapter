package no.fint.sikri.data.noark.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseProperties;
import no.fint.arkiv.TitleService;
import no.fint.arkiv.sikri.oms.*;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.arkiv.noark.JournalpostResource;
import no.fint.model.resource.arkiv.noark.KlasseResource;
import no.fint.model.resource.arkiv.noark.SaksmappeResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.noark.dokument.CheckinDocument;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory;
import no.fint.sikri.data.noark.dokument.DokumentobjektService;
import no.fint.sikri.data.noark.journalpost.JournalpostFactory;
import no.fint.sikri.data.noark.journalpost.RegistryEntryDocuments;
import no.fint.sikri.data.noark.klasse.KlasseService;
import no.fint.sikri.data.noark.part.PartFactory;
import no.fint.sikri.data.utilities.FintPropertyUtils;
import no.fint.sikri.data.utilities.XmlUtils;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.ExternalSystemLinkService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NoarkService {


    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private PartFactory partFactory;

    @Autowired
    private KlasseService klasseService;

    @Autowired
    private NoarkFactory noarkFactory;

    @Autowired
    private CaseQueryService caseQueryService;

    @Autowired
    private TitleService titleService;

    @Autowired
    private DokumentobjektService dokumentobjektService;

    @Autowired
    private JournalpostFactory journalpostFactory;

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

    @Autowired
    private ExternalSystemLinkService externalSystemLinkService;

    @Autowired
    private XmlUtils xmlUtils;

    @Value("${fint.sikri.followup:}")
    private String followUpMethodId; // = "TE";

    @Value("${fint.sikri.noark.3.2:true}")
    private boolean noark_3_2;

    public CaseType createCase(SikriIdentity identity, CaseType input, SaksmappeResource resource) {
        CaseType result = sikriObjectModelService.createDataObject(identity, input);
        createRelatedObjectsForNewCase(identity, result, resource);
        return result;
    }

    private void createRelatedObjectsForNewCase(SikriIdentity identity, CaseType caseType, SaksmappeResource resource) {
        if (resource.getPart() != null) {
            sikriObjectModelService.createDataObjects(
                    identity,
                    resource
                            .getPart()
                            .stream()
                            .map(part -> partFactory.createCaseParty(caseType.getId(), part))
                            .toArray(DataObject[]::new)
            );
        }

        if (resource.getKlasse() != null) {
            resource.getKlasse()
                    .stream()
                    .sorted(Comparator.comparingInt(KlasseResource::getRekkefolge))
                    .forEach(klasse -> klasseService.createClassification(identity, caseType.getId(), klasse));
        }

        // TODO if (resource.getArkivnotat() != null) {}
        // TODO if (resource.getNoekkelord() != null) {}

    }


    public void createExternalSystemLink(SikriIdentity identity, Integer caseId, Identifikator identifikator) {
        if (identifikator != null && StringUtils.isNotBlank(identifikator.getIdentifikatorverdi())) {
            sikriObjectModelService.createDataObject(identity, externalSystemLink(caseId, identifikator.getIdentifikatorverdi()));
        }
    }

    public ExternalSystemLinkCaseType externalSystemLink(Integer caseId, String externalKey) {
        ExternalSystemLinkCaseType externalSystemLinkCaseType = new ExternalSystemLinkCaseType();
        externalSystemLinkCaseType.setCaseId(caseId);
        externalSystemLinkCaseType.setExternalKey(externalKey);
        externalSystemLinkCaseType.setExternalSystemCode(externalSystemLinkService.getExternalSystemLinkId());

        return externalSystemLinkCaseType;
    }

    public Identifikator getIdentifierFromExternalSystemLink(SikriIdentity identity, Integer caseId) {
        Identifikator identifikator = new Identifikator();
        final String filter = "ExternalSystemCode=" + externalSystemLinkService.getExternalSystemLinkId()
                + " and CaseId=" + caseId;
        sikriObjectModelService.getDataObjects(identity, SikriObjectTypes.EXTERNAL_SYSTEM_LINK_CASE, filter)
                .stream()
                .filter(ExternalSystemLinkCaseType.class::isInstance)
                .map(ExternalSystemLinkCaseType.class::cast)
                .map(ExternalSystemLinkType::getExternalKey)
                .forEach(identifikator::setIdentifikatorverdi);
        return identifikator;
    }


    public void updateCase(SikriIdentity identity, CaseProperties caseProperties, String query, SaksmappeResource saksmappeResource) throws CaseNotFound {
        if (!(caseQueryService.isValidQuery(query))) {
            throw new IllegalArgumentException("Invalid query: " + query);
        }
        final List<CaseType> cases = caseQueryService.query(identity, query).collect(Collectors.toList());
        if (cases.size() != 1) {
            throw new CaseNotFound("Case not found for query " + query);
        }
        final CaseType caseType = cases.get(0);
        noarkFactory.applyFieldsForSaksmappe(identity, caseType, saksmappeResource);
        noarkFactory.addLinksToSaksmappe(caseType, saksmappeResource);
        noarkFactory.parseTitleAndFields(caseProperties, caseType, saksmappeResource);
        String recordPrefix = titleService.getRecordTitlePrefix(caseProperties.getTitle(), saksmappeResource);
        String documentPrefix = titleService.getDocumentTitlePrefix(caseProperties.getTitle(), saksmappeResource);

        for (JournalpostResource journalpost : saksmappeResource.getJournalpost()) {
            log.debug("Create journalpost {}", journalpost.getTittel());
            final RegistryEntryDocuments registryEntryDocuments = journalpostFactory.toRegistryEntryDocuments(caseType.getId(), journalpost, recordPrefix, documentPrefix);

            boolean updateRegistryEntry = noark_3_2 &&
                    "J".equals(registryEntryDocuments.getRegistryEntry().getRecordStatusId());

            if (updateRegistryEntry) {
                registryEntryDocuments.getRegistryEntry().setRecordStatusId(noark32Status(registryEntryDocuments.getRegistryEntry().getRegistryEntryTypeId()));
                log.info("NOARK avsnitt 3.2: Setter journalstatus til {}", registryEntryDocuments.getRegistryEntry().getRecordStatusId());
            }

            // XXX 💩
            log.info("💩 _can_ very soon (when calling createDataObject) happen 😓");
            final RegistryEntryType registryEntry = sikriObjectModelService.createDataObject(identity, registryEntryDocuments.getRegistryEntry());

            // Elements creates one RegistryEntryDocument and DocumentDescription when creating a RegistryEntry.
            final List<DataObject> dataObjects = sikriObjectModelService.getDataObjects(
                    identity,
                    SikriObjectTypes.REGISTRY_ENTRY_DOCUMENT,
                    "RegistryEntryId=" + registryEntry.getId(),
                    1,
                    SikriObjectTypes.DOCUMENT_DESCRIPTION);

            if (log.isDebugEnabled()) {
                try {
                    log.debug("Elements made this: {}", new ObjectMapper().writeValueAsString(dataObjects));
                } catch (JsonProcessingException ignore) {
                }
            }

            for (SenderRecipientType senderRecipient : registryEntryDocuments.getSenderRecipients()) {
                log.debug("Create SenderRecipient {}", senderRecipient.getName());
                senderRecipient.setRegistryEntryId(registryEntry.getId());
                sikriObjectModelService.createDataObject(identity, senderRecipient);
            }

            for (int i = 0; i < registryEntryDocuments.getDocuments().size(); i++) {
                Pair<String, RegistryEntryDocuments.Document> document = registryEntryDocuments.getDocuments().get(i);

                for (int j = 0; j < document.getRight().getCheckinDocuments().size(); j++) {
                    CheckinDocument checkinDocument = document.getRight().getCheckinDocuments().get(j);

                    if (i == 0 && j == 0 && dataObjects != null && dataObjects.size() == 1) {
                        log.debug("SIKRI WORKAROUND HACK IN PROGRESS! 🦴");

                        RegistryEntryDocumentType registryEntryDocument = (RegistryEntryDocumentType) dataObjects.get(0);
                        final DocumentDescriptionType documentDescription = registryEntryDocument.getDocumentDescription();

                        FintPropertyUtils.copyProperties(document.getRight().getDocumentDescription(), documentDescription,
                                p -> !StringUtils.equalsAny(p.getName(), "id", "dataObjectId", "documentCategoryId"),
                                (src, dst) -> dst == null ? src : dst);

                        log.debug("Update 💼 {}", documentDescription);
                        sikriObjectModelService.updateDataObject(identity, documentDescription);

                        registryEntryDocument.setDocumentLinkTypeId(document.getLeft());
                        log.debug("Update 📂 {}", registryEntryDocument);
                        sikriObjectModelService.updateDataObject(identity, registryEntryDocument);

                        log.debug("Create 🧾 {}", checkinDocument.getGuid());
                        checkinDocument.setDocumentId(documentDescription.getId());
                        sikriObjectModelService.createDataObject(identity, dokumentobjektService.createDocumentObject(checkinDocument));

                        log.debug("Checkin 🧾 {}", checkinDocument);
                        dokumentobjektService.checkinDocument(identity, checkinDocument);

                        log.debug("🍷🍷🍷");

                    } else {
                        log.debug("Create 💼 {}", document.getRight().getDocumentDescription());
                        final DocumentDescriptionType documentDescription = sikriObjectModelService.createDataObject(identity, document.getRight().getDocumentDescription());
                        log.debug("Create 🧾 {}", checkinDocument.getGuid());
                        checkinDocument.setDocumentId(documentDescription.getId());
                        sikriObjectModelService.createDataObject(identity, dokumentobjektService.createDocumentObject(checkinDocument));
                        log.debug("Checkin 🧾 {}", checkinDocument);
                        dokumentobjektService.checkinDocument(identity, checkinDocument);
                        log.debug("Create 📂 {}", document.getLeft());
                        sikriObjectModelService.createDataObject(identity, dokumentbeskrivelseFactory.toRegistryEntryDocument(registryEntry.getId(), document.getLeft(), documentDescription.getId()));
                    }
                }
            }

            if (updateRegistryEntry) {
                log.info("NOARK avsnitt 3.2: Oppdaterer journalstatus til J");
                registryEntry.setRecordStatusId("J");
            }

            if (StringUtils.isNotBlank(followUpMethodId)
                    && StringUtils.equalsAnyIgnoreCase(registryEntry.getRegistryEntryTypeId(), "I", "N")) {
                log.debug("Updating SenderRecipients 📧 with follow-up {}", followUpMethodId);
                sikriObjectModelService.getDataObjects(identity,
                        SikriObjectTypes.SENDER_RECIPIENT,
                        "RegistryEntryId=" + registryEntry.getId())
                        .stream()
                        .map(SenderRecipientType.class::cast)
                        .filter(SenderRecipientType::isIsResponsible)
                        .forEach(senderRecipient -> {
                            senderRecipient.setFollowUpMethodId(followUpMethodId);
                            senderRecipient.setFollowedUpByRegistryEntryId(registryEntry.getId());
                            senderRecipient.setFollowedUpDate(xmlUtils.xmlDate(new Date()));
                            log.trace("Setting follow up method {} on {}", followUpMethodId, senderRecipient);
                            sikriObjectModelService.updateDataObject(identity, senderRecipient);
                        });
                registryEntry.setMustFollowUp(BacklogTypeType.NONE);
                updateRegistryEntry = true;
            }

            if (updateRegistryEntry) {
                sikriObjectModelService.updateDataObject(identity, registryEntry);
            }
        }
    }

    private String noark32Status(String registryEntryTypeId) {
        switch (registryEntryTypeId.toUpperCase(Locale.ROOT)) {
            case "I":
            case "T":
                return "M";
            case "U":
            case "N":
            default:
                return "F";
        }
    }
}
