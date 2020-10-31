package no.fint.sikri.data.noark.common;

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

    public void updateCase(String query, SaksmappeResource saksmappeResource) throws CaseNotFound {
        if (!(caseQueryService.isValidQuery(query))) {
            throw new IllegalArgumentException("Invalid query: " + query);
        }
        final List<CaseType> cases = caseQueryService.query(query).collect(Collectors.toList());
        if (cases.size() != 1) {
            throw new CaseNotFound("Case not found for query " + query);
        }
        final CaseType caseType = cases.get(0);

        for (JournalpostResource journalpost : saksmappeResource.getJournalpost()) {
            log.debug("Create journalpost {}", journalpost.getTittel());
            final RegistryEntryDocuments registryEntryDocuments = journalpostFactory.toRegistryEntryDocuments(caseType.getId(), journalpost);
            final RegistryEntryType registryEntry = sikriObjectModelService.createDataObject(registryEntryDocuments.getRegistryEntry());

            final List<DataObject> dataObjects = sikriObjectModelService.getDataObjects(
                    SikriObjectTypes.REGISTRY_ENTRY_DOCUMENT,
                    "RegistryEntryId=" + registryEntry.getId(),
                    1,
                    SikriObjectTypes.DOCUMENT_DESCRIPTION,
                    "DocumentDescription.CurrentVersion");
            log.debug("Bernie made this: {}", dataObjects);

            for (SenderRecipientType senderRecipient : registryEntryDocuments.getSenderRecipients()) {
                log.debug("Create SenderRecipient {}", senderRecipient.getName());
                senderRecipient.setRegistryEntryId(registryEntry.getId());
                sikriObjectModelService.createDataObject(senderRecipient);
            }

            for (int i = 0; i < registryEntryDocuments.getDocuments().size(); i++) {
                Pair<String, RegistryEntryDocuments.Document> document = registryEntryDocuments.getDocuments().get(i);

                for (int j = 0; j < document.getRight().getCheckinDocuments().size(); j++) {
                    CheckinDocument checkinDocument = document.getRight().getCheckinDocuments().get(j);

//                    if (false && i == 0 && j == 0 && dataObjects != null && dataObjects.size() == 1) {
//                        log.debug("BERNIE WORKAROUND HACK IN PROGRESS! 💣");
//
//                        RegistryEntryDocumentType bernieObject = (RegistryEntryDocumentType) dataObjects.get(0);
//
//
//                        checkinDocument.setDocumentId(bernieObject.getDocumentDescriptionId());
//                        dokumentobjektService.checkinDocument(checkinDocument);
//
//                        BeanUtils.copyProperties(document.getRight().getDocumentDescription(), bernieObject.getDocumentDescription());
//                        log.debug("Updating 🧾 {}", bernieObject.getDocumentDescription());
//                        sikriObjectModelService.updateDataObject(bernieObject.getDocumentDescription());
//
//                        bernieObject.setDocumentLinkTypeId(document.getLeft());
//                        log.debug("Updating 🌲 {}", bernieObject);
//                        sikriObjectModelService.updateDataObject(bernieObject);
//
//                        log.debug("🤬🤬🤬");
//
//                    } else {
                        // TODO 🚽 This creates too many DocumentDescription instances if there are multiple document objects on the same document description. 👿
                        log.debug("Create DocumentDescription {}", document.getRight().getDocumentDescription().getDocumentTitle());
                        final DocumentDescriptionType documentDescription = sikriObjectModelService.createDataObject(document.getRight().getDocumentDescription());
                        log.debug("Create DocumentObject {}", checkinDocument.getGuid());
                        checkinDocument.setDocumentId(documentDescription.getId());
                        sikriObjectModelService.createDataObject(dokumentobjektService.createDocumentObject(checkinDocument));
                        dokumentobjektService.checkinDocument(checkinDocument);
                        log.debug("Create RegistryEntryDocument {}", document.getLeft());
                        sikriObjectModelService.createDataObject(dokumentbeskrivelseFactory.toRegistryEntryDocument(registryEntry.getId(), document.getLeft(), documentDescription.getId()));
//                    }
                }
            }
        }
    }
}
