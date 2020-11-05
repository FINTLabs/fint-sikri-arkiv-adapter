package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.RegistryEntryDocumentType;
import no.fint.model.resource.arkiv.noark.DokumentbeskrivelseResource;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DokumentbeskrivelseService {

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<DokumentbeskrivelseResource> queryForJournalpost(String id) {
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.REGISTRY_ENTRY_DOCUMENT,
                "RegistryEntryId=" + id,
                0,
                SikriObjectTypes.DOCUMENT_DESCRIPTION,
                SikriObjectTypes.DOCUMENT_DESCRIPTION + ".CurrentVersion",
                SikriObjectTypes.DOCUMENT_DESCRIPTION + ".CurrentVersion." + SikriObjectTypes.FILE_FORMAT,
                SikriObjectTypes.DOCUMENT_DESCRIPTION + ".CurrentVersion." + SikriObjectTypes.VARIANT_FORMAT,
                SikriObjectTypes.DOCUMENT_LINK_TYPE,
                SikriObjectTypes.DOCUMENT_DESCRIPTION_DOCUMENT_CATEGORY
        ).stream()
                .map(RegistryEntryDocumentType.class::cast)
                .map(registryEntryDocumentType -> dokumentbeskrivelseFactory.toFintResource(registryEntryDocumentType))
                .collect(Collectors.toList());
    }
}
