package no.fint.sikri.data.noark.codes.dokumenttype;

import no.fint.arkiv.sikri.oms.DocumentCategoryType;
import no.fint.model.resource.arkiv.noark.DokumentTypeResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Stream;

@Service
public class DokumenttypeService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<DokumentTypeResource> getDocumenttypeTable() {

        return sikriObjectModelService.getDataObjects(SikriObjectTypes.DOCUMENT_CATEGORY, null, 0, Collections.emptyList())
                .stream()
                .map(DocumentCategoryType.class::cast)
                .map(BegrepMapper::mapDokumentType);
    }
}
