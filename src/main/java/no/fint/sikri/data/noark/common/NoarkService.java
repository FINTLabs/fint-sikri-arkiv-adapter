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
import no.fint.sikri.data.noark.dokument.DokumentobjektFactory;
import no.fint.sikri.data.noark.journalpost.JournalpostFactory;
import no.fint.sikri.data.noark.journalpost.RegistryEntryDocuments;
import no.fint.sikri.data.noark.klasse.KlasseService;
import no.fint.sikri.data.noark.part.PartFactory;
import no.fint.sikri.data.utilities.FintPropertyUtils;
import no.fint.sikri.data.utilities.XmlUtils;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.ExternalSystemLinkService;
import no.fint.sikri.service.SikriDocumentService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NoarkService {


    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriDocumentService sikriDocumentService;

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
    private DokumentobjektFactory dokumentobjektFactory;

    @Autowired
    private JournalpostFactory journalpostFactory;

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

    @Autowired
    private ExternalSystemLinkService externalSystemLinkService;

    @Autowired
    private XmlUtils xmlUtils;

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

            final boolean updateRegistryEntryStatus = noark_3_2 &&
                    "J".equals(registryEntryDocuments.getRegistryEntry().getRecordStatusId());

            if (updateRegistryEntryStatus) {
                registryEntryDocuments.getRegistryEntry().setRecordStatusId(noark32Status(registryEntryDocuments.getRegistryEntry().getRegistryEntryTypeId()));
                log.info("NOARK section 3.2: Setting journalstatus to {}", registryEntryDocuments.getRegistryEntry().getRecordStatusId());
            }

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

            registryEntryDocuments.getSenderRecipients().stream()
                    .filter(it -> noValue(it.getAdministrativeUnitId()) && noValue(it.getOfficerNameId()))
                    .map(SenderRecipientType::getName)
                    .findFirst()
                    .ifPresent(registryEntry::setSenderRecipient);

            for (SenderRecipientType senderRecipient : registryEntryDocuments.getSenderRecipients()) {
                log.debug("Create SenderRecipient {}", senderRecipient.getName());
                senderRecipient.setRegistryEntryId(registryEntry.getId());
                sikriObjectModelService.createDataObject(identity, senderRecipient);
            }

            if (registryEntry.getNumberOfSubDocuments() == null) {
                registryEntry.setNumberOfSubDocuments(0);
            }

            for (int i = 0; i < registryEntryDocuments.getDocuments().size(); i++) {
                Pair<String, RegistryEntryDocuments.Document> document = registryEntryDocuments.getDocuments().get(i);

                for (int j = 0; j < document.getRight().getCheckinDocuments().size(); j++) {
                    CheckinDocument checkinDocument = document.getRight().getCheckinDocuments().get(j);

                    if (StringUtils.equalsIgnoreCase(document.getLeft(), "H")) {
                        registryEntry.setFileExtensionMainDocument(checkinDocument.getContentType());
                    } else {
                        registryEntry.setNumberOfSubDocuments(registryEntry.getNumberOfSubDocuments() + 1);
                    }

                    final String filePath = sikriDocumentService.uploadFile(identity, checkinDocument.getContent(), checkinDocument.getFilename());
                    log.debug("Uploaded filePath: {}", filePath);

                    log.trace("DocumentTitle from input source: {}", document.getRight().getDocumentDescription().getDocumentTitle());

                    // Beware that the first document is handled differently
                    if (i == 0 && j == 0 && dataObjects != null && dataObjects.size() == 1) {
                        RegistryEntryDocumentType registryEntryDocument = (RegistryEntryDocumentType) dataObjects.get(0);
                        final DocumentDescriptionType documentDescription = registryEntryDocument.getDocumentDescription();

                        log.trace("DocumentTitle from registryEntryDocument: {}", documentDescription.getDocumentTitle());

                        // Keep title from input if present
                        Optional.ofNullable(document.getRight().getDocumentDescription().getDocumentTitle())
                                .ifPresent(documentDescription::setDocumentTitle);

                        // When destination properties don't have values, keep properties from input (but not ids)
                        FintPropertyUtils.copyProperties(document.getRight().getDocumentDescription(), documentDescription,
                                p -> !StringUtils.equalsAny(p.getName(), "id", "dataObjectId", "documentCategoryId"),
                                (src, dst) -> dst == null ? src : dst);

                        log.trace("DocumentTitle after copy properties: {}", documentDescription.getDocumentTitle());

                        log.debug("Update 💼 {}", documentDescription);
                        sikriObjectModelService.updateDataObject(identity, documentDescription);

                        registryEntryDocument.setDocumentLinkTypeId(document.getLeft());
                        log.debug("Update 📂 {}", registryEntryDocument);
                        sikriObjectModelService.updateDataObject(identity, registryEntryDocument);

                        log.debug("Create 🧾 {}", checkinDocument.getFilename());
                        checkinDocument.setDocumentId(documentDescription.getId());
                        sikriObjectModelService.createDataObject(identity, dokumentobjektFactory.toDocumentObject(checkinDocument, filePath));
                    } else {
                        log.debug("Create 💼 {}", document.getRight().getDocumentDescription());
                        final DocumentDescriptionType documentDescription = sikriObjectModelService.createDataObject(identity, document.getRight().getDocumentDescription());

                        log.debug("Create 🧾 {}", checkinDocument.getFilename());
                        checkinDocument.setDocumentId(documentDescription.getId());
                        sikriObjectModelService.createDataObject(identity, dokumentobjektFactory.toDocumentObject(checkinDocument, filePath));

                        log.debug("Create 📂 {}", document.getLeft());
                        sikriObjectModelService.createDataObject(identity, dokumentbeskrivelseFactory.toRegistryEntryDocument(registryEntry.getId(), document.getLeft(), documentDescription.getId()));
                    }
                }
            }

            if (updateRegistryEntryStatus) {
                log.info("NOARK section 3.2: Updating journalstatus to J");
                registryEntry.setRecordStatusId("J");
            }

            if (StringUtils.isNotBlank(caseProperties.getAvskrivningsmaate())
                    && StringUtils.equalsAnyIgnoreCase(registryEntry.getRegistryEntryTypeId(), "I", "N")) {
                log.debug("Updating SenderRecipients 📧 with follow-up {}", caseProperties.getAvskrivningsmaate());
                sikriObjectModelService.getDataObjects(identity,
                        SikriObjectTypes.SENDER_RECIPIENT,
                        "RegistryEntryId=" + registryEntry.getId())
                        .stream()
                        .map(SenderRecipientType.class::cast)
                        .filter(SenderRecipientType::isIsResponsible)
                        .forEach(senderRecipient -> {
                            senderRecipient.setFollowUpMethodId(caseProperties.getAvskrivningsmaate());
                            senderRecipient.setFollowedUpByRegistryEntryId(registryEntry.getId());
                            senderRecipient.setFollowedUpDate(xmlUtils.xmlDate(new Date()));
                            log.trace("Setting follow up method {} on {}", caseProperties.getAvskrivningsmaate(), senderRecipient);
                            sikriObjectModelService.updateDataObject(identity, senderRecipient);
                        });
                registryEntry.setMustFollowUp(BacklogTypeType.NONE);
            }

            sikriObjectModelService.updateDataObject(identity, registryEntry);
        }
    }

    private boolean noValue(Integer id) {
        return id == null || id == 0;
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
