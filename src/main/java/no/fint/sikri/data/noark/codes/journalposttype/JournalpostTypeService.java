package no.fint.sikri.data.noark.codes.journalposttype;

import no.fint.arkiv.sikri.oms.RegistryEntryTypeType;
import no.fint.model.resource.arkiv.kodeverk.JournalpostTypeResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Stream;

@Service
public class JournalpostTypeService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<JournalpostTypeResource> getDocumentCategoryTable() {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.REGISTRY_ENTRY_TYPE, null, 0, Collections.emptyList())
                .stream()
                .map(RegistryEntryTypeType.class::cast)
                .map(BegrepMapper::mapJournalpostType);
    }
}
