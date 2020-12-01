package no.fint.sikri.data.noark.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseProperties;
import no.fint.arkiv.TitleService;
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
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
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
    private TitleService titleService;

    @Autowired
    private DokumentobjektService dokumentobjektService;

    @Autowired
    private JournalpostFactory journalpostFactory;

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

    public CaseType createCase(CaseType input, SaksmappeResource resource) {
        CaseType result = sikriObjectModelService.createDataObject(input);
        createRelatedObjectsForNewCase(result, resource);
        return result;
    }

    private void createRelatedObjectsForNewCase(CaseType caseType, SaksmappeResource resource) {
        if (resource.getPart() != null) {
            sikriObjectModelService.createDataObjects(
                    resource
                            .getPart()
                            .stream()
                            .map(part -> partFactory.createCaseParty(caseType.getId(), part))
                            .toArray(DataObject[]::new)
            );
        }

        if (resource.getKlasse() != null) {
            sikriObjectModelService.createDataObjects(
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


    public void createExternalSystemLink(Integer caseId, Identifikator identifikator) {
        if (identifikator != null && StringUtils.isNotBlank(identifikator.getIdentifikatorverdi())) {
            sikriObjectModelService.createDataObject(noarkFactory.externalSystemLink(caseId, identifikator.getIdentifikatorverdi()));
        }
    }

    public void updateCase(CaseProperties caseProperties, String query, SaksmappeResource saksmappeResource) throws CaseNotFound {
        if (!(caseQueryService.isValidQuery(query))) {
            throw new IllegalArgumentException("Invalid query: " + query);
        }
        final List<CaseType> cases = caseQueryService.query(query).collect(Collectors.toList());
        if (cases.size() != 1) {
            throw new CaseNotFound("Case not found for query " + query);
        }
        final CaseType caseType = cases.get(0);
        noarkFactory.applyFieldsForSaksmappe(caseType, saksmappeResource);
        noarkFactory.addLinksToSaksmappe(caseType, saksmappeResource);
        noarkFactory.parseTitleAndFields(caseProperties, caseType, saksmappeResource);
        String recordPrefix = titleService.getRecordTitlePrefix(caseProperties.getTitle(), saksmappeResource);
        String documentPrefix = titleService.getDocumentTitlePrefix(caseProperties.getTitle(), saksmappeResource);

        for (JournalpostResource journalpost : saksmappeResource.getJournalpost()) {
            log.debug("Create journalpost {}", journalpost.getTittel());
            final RegistryEntryDocuments registryEntryDocuments = journalpostFactory.toRegistryEntryDocuments(caseType.getId(), journalpost, recordPrefix, documentPrefix);
            final RegistryEntryType registryEntry = sikriObjectModelService.createDataObject(registryEntryDocuments.getRegistryEntry());

            // Elements creates one RegistryEntryDocument and DocumentDescription when creating a RegistryEntry.
            final List<DataObject> dataObjects = sikriObjectModelService.getDataObjects(
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
                sikriObjectModelService.createDataObject(senderRecipient);
            }

            for (int i = 0; i < registryEntryDocuments.getDocuments().size(); i++) {
                Pair<String, RegistryEntryDocuments.Document> document = registryEntryDocuments.getDocuments().get(i);
                Integer documentDescriptionId = null;

                for (int j = 0; j < document.getRight().getCheckinDocuments().size(); j++) {
                    CheckinDocument checkinDocument = document.getRight().getCheckinDocuments().get(j);

                    if (i == 0 && j == 0 && dataObjects != null && dataObjects.size() == 1) {
                        log.debug("ELEMENTS WORKAROUND HACK IN PROGRESS! 💣");

                        RegistryEntryDocumentType registryEntryDocument = (RegistryEntryDocumentType) dataObjects.get(0);
                        final DocumentDescriptionType documentDescription = registryEntryDocument.getDocumentDescription();

                        BeanUtils.copyProperties(document.getRight().getDocumentDescription(), documentDescription, "id", "dataObjectId", "documentCategoryId");
                        registryEntryDocument.setDocumentLinkTypeId(document.getLeft());

                        log.debug("Update 💼 {}", documentDescription);
                        sikriObjectModelService.updateDataObject(documentDescription);
                        log.debug("Update 📂 {}", registryEntryDocument);
                        sikriObjectModelService.updateDataObject(registryEntryDocument);

                        documentDescriptionId = documentDescription.getId();
                        log.debug("Create 🧾 {}", checkinDocument.getGuid());
                        checkinDocument.setDocumentId(documentDescriptionId);
                        sikriObjectModelService.createDataObject(dokumentobjektService.createDocumentObject(checkinDocument));

                        log.debug("Checkin 🧾 {}", checkinDocument);
                        dokumentobjektService.checkinDocument(checkinDocument);

                        log.debug("🤬🤬🤬");

                    } else {
                        if (j == 0)
                        {
                            log.debug("Create DocumentDescription {}", document.getRight().getDocumentDescription().getDocumentTitle());
                            final DocumentDescriptionType documentDescription = sikriObjectModelService.createDataObject(document.getRight().getDocumentDescription());
                            documentDescriptionId = documentDescription.getId();
                        }
                        log.debug("Create DocumentObject {}", checkinDocument.getGuid());
                        checkinDocument.setDocumentId(documentDescriptionId);
                        sikriObjectModelService.createDataObject(dokumentobjektService.createDocumentObject(checkinDocument));
                        dokumentobjektService.checkinDocument(checkinDocument);
                        log.debug("Create RegistryEntryDocument {}", document.getLeft());
                        sikriObjectModelService.createDataObject(dokumentbeskrivelseFactory.toRegistryEntryDocument(registryEntry.getId(), document.getLeft(), documentDescriptionId));
                    }
                }
            }
        }
    }
}
