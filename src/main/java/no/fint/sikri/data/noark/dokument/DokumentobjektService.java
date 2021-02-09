package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.DocumentObjectType;
import no.fint.model.resource.arkiv.noark.DokumentobjektResource;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.SikriDocumentService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DokumentobjektService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriDocumentService sikriDocumentService;

    @Autowired
    private DokumentobjektFactory dokumentobjektFactory;

    public List<DokumentobjektResource> queryDokumentobjekt(SikriIdentity identity, String id) {
        return sikriObjectModelService.getDataObjects(
                identity,
                SikriObjectTypes.DOCUMENT_OBJECT,
                "DocumentDescriptionId=" + id,
                0,
                Arrays.asList(SikriObjectTypes.FILE_FORMAT, SikriObjectTypes.VARIANT_FORMAT)
        ).stream()
                .map(DocumentObjectType.class::cast)
                .map(dokumentobjektFactory::toFintResource)
                .collect(Collectors.toList());
    }

    public void checkinDocument(SikriIdentity identity, CheckinDocument checkinDocument) {
        sikriDocumentService.checkin(identity, checkinDocument.getDocumentId(), checkinDocument.getVariant(), checkinDocument.getVersion(), checkinDocument.getGuid());
    }

    public DocumentObjectType createDocumentObject(CheckinDocument checkinDocument) {
        DocumentObjectType documentObject = new DocumentObjectType();
        documentObject.setDocumentDescriptionId(checkinDocument.getDocumentId());
        documentObject.setVersionNumber(checkinDocument.getVersion());
        documentObject.setVariantFormatId(checkinDocument.getVariant());
        documentObject.setFileformatId(checkinDocument.getContentType());
        return documentObject;
    }
}
