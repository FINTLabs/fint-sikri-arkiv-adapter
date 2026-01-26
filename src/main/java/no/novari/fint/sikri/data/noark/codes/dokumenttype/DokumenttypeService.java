package no.novari.fint.sikri.data.noark.codes.dokumenttype;

import no.fint.arkiv.sikri.oms.DocumentCategoryType;
import no.novari.fint.model.resource.arkiv.kodeverk.DokumentTypeResource;
import no.novari.fint.sikri.data.utilities.BegrepMapper;
import no.novari.fint.sikri.service.SikriIdentityService;
import no.novari.fint.sikri.service.SikriObjectModelService;
import no.novari.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Stream;

@Service
public class DokumenttypeService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<DokumentTypeResource> getDocumenttypeTable() {

        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.DOCUMENT_CATEGORY, null, 0, Collections.emptyList())
                .stream()
                .map(DocumentCategoryType.class::cast)
                .map(BegrepMapper::mapDokumentType);
    }
}
