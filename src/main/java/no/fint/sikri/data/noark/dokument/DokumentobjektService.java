package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.DocumentObjectType;
import no.fint.model.resource.administrasjon.arkiv.DokumentobjektResource;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DokumentobjektService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private DokumentobjektFactory dokumentobjektFactory;

    public List<DokumentobjektResource> queryDokumentobjekt(String id) {
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.DOCUMENT_OBJECT,
                "DocumentDescriptionId=" + id,
                0,
                Arrays.asList(SikriObjectTypes.FILE_FORMAT, SikriObjectTypes.VARIANT_FORMAT)
        ).stream()
                .map(DocumentObjectType.class::cast)
                .map(dokumentobjektFactory::toFintResource)
                .collect(Collectors.toList());
    }
}
