package no.fint.sikri.data.noark.common;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.*;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.arkiv.noark.SaksmappeResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory;
import no.fint.sikri.data.noark.dokument.DokumentobjektService;
import no.fint.sikri.data.noark.journalpost.JournalpostFactory;
import no.fint.sikri.data.noark.klasse.KlasseFactory;
import no.fint.sikri.data.noark.part.PartFactory;
import no.fint.sikri.model.ElementsIdentity;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.SikriObjectModelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public CaseType createCase(ElementsIdentity identity, CaseType input, SaksmappeResource resource) {
        CaseType result = sikriObjectModelService.createDataObject(identity, input);
        createRelatedObjectsForNewCase(identity, result, resource);
        return result;
    }

    private void createRelatedObjectsForNewCase(ElementsIdentity identity, CaseType caseType, SaksmappeResource resource) {
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


    public void createExternalSystemLink(ElementsIdentity identity, Integer caseId, Identifikator identifikator) {
        if (identifikator != null && StringUtils.isNotBlank(identifikator.getIdentifikatorverdi())) {
            sikriObjectModelService.createDataObject(identity, noarkFactory.externalSystemLink(caseId, identifikator.getIdentifikatorverdi()));
        }
    }

    public void updateCase(ElementsIdentity identity, String query, SaksmappeResource saksmappeResource) throws CaseNotFound {
        if (!(caseQueryService.isValidQuery(query))) {
            throw new IllegalArgumentException("Invalid query: " + query);
        }
        final List<CaseType> cases = caseQueryService.query(identity, query).collect(Collectors.toList());
        if (cases.size() != 1) {
            throw new CaseNotFound("Case not found for query " + query);
        }
        final CaseType caseType = cases.get(0);
        sikriObjectModelService.createDataObjects(
                identity,
                saksmappeResource
                        .getJournalpost()
                        .stream()
                        .map(r -> journalpostFactory.toRegistryEntryDocuments(caseType.getId(), r))
                        .flatMap(d -> {
                            final RegistryEntryType registryEntry = sikriObjectModelService.createDataObject(identity, d.getRegistryEntry());
                            return Stream.concat(
                                    d.getSenderRecipients().stream().peek(it -> it.setRegistryEntryId(registryEntry.getId()))
                                    ,
                                    d.getDocuments().stream().map(it -> {
                                        final DocumentDescriptionType documentDescription = sikriObjectModelService.createDataObject(identity, it.getRight().getDocumentDescription());
                                        it.getRight()
                                                .getCheckinDocuments()
                                                .stream()
                                                .peek(checkinDocument -> checkinDocument.setDocumentId(documentDescription.getId()))
                                                .peek(checkinDocument -> sikriObjectModelService.createDataObject(
                                                        identity,
                                                        dokumentobjektService.createDocumentObject(checkinDocument)
                                                ))
                                                .forEach(document -> dokumentobjektService.checkinDocument(identity, document));
                                        return dokumentbeskrivelseFactory.toRegistryEntryDocument(registryEntry.getId(), it.getLeft(), documentDescription.getId());
                                    }));
                        })
                        .toArray(DataObject[]::new));
    }
}
