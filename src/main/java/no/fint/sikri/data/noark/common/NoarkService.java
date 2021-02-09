package no.fint.sikri.data.noark.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.*;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.arkiv.noark.JournalpostResource;
import no.fint.model.resource.arkiv.noark.SaksmappeResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.noark.dokument.CheckinDocument;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory;
import no.fint.sikri.data.noark.dokument.DokumentobjektService;
import no.fint.sikri.data.noark.journalpost.JournalpostFactory;
import no.fint.sikri.data.noark.journalpost.RegistryEntryDocuments;
import no.fint.sikri.data.noark.klasse.KlasseFactory;
import no.fint.sikri.data.noark.part.PartFactory;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.data.utilities.FintPropertyUtils;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NoarkService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private PartFactory partFactory;

    @Autowired
    private KlasseFactory klasseFactory;

    @Autowired
    private NoarkFactory noarkFactory;

    @Autowired
    private CaseQueryService caseQueryService;

    @Autowired
    private DokumentobjektService dokumentobjektService;

    @Autowired
    private JournalpostFactory journalpostFactory;

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

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
            sikriObjectModelService.createDataObjects(
                    identity,
                    resource.getKlasse()
                            .stream()
                            .map(klasseFactory::toClassificationType)
                            .sorted(Comparator.comparing(ClassificationType::getSortOrder))
                            .peek(cls -> cls.setCaseId(caseType.getId()))
                            .toArray(DataObject[]::new)
            );
        }

        // TODO if (resource.getArkivnotat() != null) {}
        // TODO if (resource.getNoekkelord() != null) {}

    }


    public void createExternalSystemLink(SikriIdentity identity, Integer caseId, Identifikator identifikator) {
        if (identifikator != null && StringUtils.isNotBlank(identifikator.getIdentifikatorverdi())) {
            sikriObjectModelService.createDataObject(identity, noarkFactory.externalSystemLink(caseId, identifikator.getIdentifikatorverdi()));
        }
    }

    public void updateCase(SikriIdentity identity, String query, SaksmappeResource saksmappeResource) throws CaseNotFound {
        if (!(caseQueryService.isValidQuery(query))) {
            throw new IllegalArgumentException("Invalid query: " + query);
        }
        final List<CaseType> cases = caseQueryService.query(identity, query).collect(Collectors.toList());
        if (cases.size() != 1) {
            throw new CaseNotFound("Case not found for query " + query);
        }
        final CaseType caseType = cases.get(0);

        for (JournalpostResource journalpost : saksmappeResource.getJournalpost()) {
            log.debug("Create journalpost {}", journalpost.getTittel());
            final RegistryEntryDocuments registryEntryDocuments = journalpostFactory.toRegistryEntryDocuments(caseType.getId(), journalpost);
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
                Integer documentDescriptionId = null;

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
                        documentDescriptionId = documentDescription.getId();
                        checkinDocument.setDocumentId(documentDescriptionId);
                        sikriObjectModelService.createDataObject(identity, dokumentobjektService.createDocumentObject(checkinDocument));

                        log.debug("Checkin 🧾 {}", checkinDocument);
                        dokumentobjektService.checkinDocument(identity, checkinDocument);

                        log.debug("🍷🍷🍷");

                    } else {
                        if (j == 0) {
                            log.debug("Create DocumentDescription {}", document.getRight().getDocumentDescription().getDocumentTitle());
                            final DocumentDescriptionType documentDescription = sikriObjectModelService.createDataObject(identity, document.getRight().getDocumentDescription());
                            documentDescriptionId = documentDescription.getId();
                        }
                        log.debug("Create DocumentObject {}", checkinDocument.getGuid());
                        checkinDocument.setDocumentId(documentDescriptionId);
                        sikriObjectModelService.createDataObject(identity, dokumentobjektService.createDocumentObject(checkinDocument));
                        dokumentobjektService.checkinDocument(identity, checkinDocument);
                        log.debug("Create RegistryEntryDocument {}", document.getLeft());
                        sikriObjectModelService.createDataObject(identity, dokumentbeskrivelseFactory.toRegistryEntryDocument(registryEntry.getId(), document.getLeft(), documentDescriptionId));
                    }
                }
            }
        }
    }
}
