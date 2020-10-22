package no.fint.sikri.data.noark.codes.dokumentstatus;

import no.fint.arkiv.sikri.oms.DocumentStatusType;
import no.fint.model.resource.arkiv.kodeverk.DokumentStatusResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Stream;

@Service
public class DokumentstatusService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<DokumentStatusResource> getDocumentStatusTable() {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.DOCUMENT_STATUS, null, 0, Collections.emptyList())
                .stream()
                .map(DocumentStatusType.class::cast)
                .map(BegrepMapper::mapDokumentStatus);
    }
}
