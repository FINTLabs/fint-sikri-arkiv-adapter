package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.arkiv.sikri.oms.DocumentDescriptionType;
import no.fint.arkiv.sikri.oms.RegistryEntryType;
import no.fint.model.resource.kultur.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.data.exception.GetTilskuddFartoyNotFoundException;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory;
import no.fint.sikri.data.noark.dokument.DokumentobjektService;
import no.fint.sikri.data.noark.journalpost.JournalpostFactory;
import no.fint.sikri.data.noark.sak.SakFactory;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TilskuddFartoyService {

    @Autowired
    private CaseQueryService caseQueryService;

    @Autowired
    private TilskuddFartoyFactory tilskuddFartoyFactory;

    @Autowired
    private SakFactory sakFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private DokumentobjektService dokumentobjektService;

    @Autowired
    private JournalpostFactory journalpostFactory;

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

    /*
    public TilskuddFartoyResource getTilskuddFartoyCaseByMappeId(String mappeId) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromMappeId(mappeId);
        return tilskuddFartoyFactory.toFintResourceList(sikriObjectModelService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(mappeId));
    }

    public TilskuddFartoyResource getTilskuddFartoyCaseBySoknadsnummer(String soknadsnummer) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.createQueryInput("refEksternId.eksternID", soknadsnummer);
        return tilskuddFartoyFactory.toFintResourceList(sikriObjectModelService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(soknadsnummer));
    }

    public TilskuddFartoyResource getTilskuddFartoyCaseBySystemId(String systemId) throws NotTilskuddfartoyException, GetTilskuddFartoyNotFoundException, GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromSystemId(systemId);
        return tilskuddFartoyFactory.toFintResourceList(sikriObjectModelService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new GetTilskuddFartoyNotFoundException(systemId));
    }

    public List<TilskuddFartoyResource> searchTilskuddFartoyCaseByQueryParams(Map<String, Object> query) throws GetTilskuddFartoyException, GetDocumentException, IllegalCaseNumberFormat {
        QueryInput queryInput = sakFactory.getQueryInputFromQueryParams(query);
        return tilskuddFartoyFactory.toFintResourceList(sikriObjectModelService.query(queryInput));
    }

    public TilskuddFartoyResource updateTilskuddFartoyCase(String caseNumber, TilskuddFartoyResource tilskuddFartoyResource) {
        throw new NotImplementedException("updateTilskuddFartoyCase");
    }
     */

    public TilskuddFartoyResource createTilskuddFartoyCase(TilskuddFartoyResource tilskuddFartoyResource) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        log.info("Create tilskudd fartøy");
        final CaseType caseType = sikriObjectModelService.createDataObject(tilskuddFartoyFactory.toCaseType(tilskuddFartoyResource));
        sikriObjectModelService.createDataObject(tilskuddFartoyFactory.externalSystemLink(caseType.getId(), tilskuddFartoyResource.getSoknadsnummer().getIdentifikatorverdi()));
        return tilskuddFartoyFactory.toFintResource(caseType);

    }

    public TilskuddFartoyResource updateTilskuddFartoyCase(String query, TilskuddFartoyResource tilskuddFartoyResource) throws GetTilskuddFartoyNotFoundException {
        if (!(caseQueryService.isValidQuery(query))) {
            throw new IllegalArgumentException("Invalid query: " + query);
        }
        final List<CaseType> cases = caseQueryService.query(query).collect(Collectors.toList());
        if (cases.size() != 1) {
            throw new GetTilskuddFartoyNotFoundException("Case not found for query " + query);
        }
        final CaseType caseType = cases.get(0);
        sikriObjectModelService.createDataObjects(
                tilskuddFartoyResource
                        .getJournalpost()
                        .stream()
                        .map(r -> journalpostFactory.toRegistryEntryDocuments(caseType.getId(), r))
                        .flatMap(d -> {
                            final RegistryEntryType registryEntry = sikriObjectModelService.createDataObject(d.getRegistryEntry());
                            return d.getDocuments().stream().map(it -> {
                                final DocumentDescriptionType documentDescription = sikriObjectModelService.createDataObject(it.getRight().getDocumentDescription());
                                it.getRight()
                                        .getCheckinDocuments()
                                        .stream()
                                        .peek(checkinDocument -> checkinDocument.setDocumentId(documentDescription.getId()))
                                        .peek(checkinDocument -> sikriObjectModelService.createDataObject(
                                                dokumentobjektService.createDocumentObject(checkinDocument)
                                        ))
                                        .forEach(dokumentobjektService::checkinDocument);
                                return dokumentbeskrivelseFactory.toRegistryEntryDocument(registryEntry.getId(), it.getLeft(), documentDescription.getId());
                            });
                        })
                        .toArray(DataObject[]::new));
        return caseQueryService
                .query(query)
                .map(tilskuddFartoyFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new GetTilskuddFartoyNotFoundException("Unable to find updated case for query " + query));
    }
}
