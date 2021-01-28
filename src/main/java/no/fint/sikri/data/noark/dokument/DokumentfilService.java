package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.arkiv.noark.DokumentfilResource;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.service.SikriDocumentService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Slf4j
public class DokumentfilService {

    @Autowired
    private SikriDocumentService sikriDocumentService;

    private String getContentType(String filename) {
        return new Tika().detect(filename);
    }

    private String getContentType(byte[] content) {
        return new Tika().detect(content);
    }

    public DokumentfilResource getDokumentfil(String systemId) {
        SikriDocumentService.SikriDocument sikriDocument;
        if (systemId.contains("_")) {
            sikriDocument = getSikriDocument(systemId);
        } else {
            sikriDocument = sikriDocumentService.getTempDocumentContentByTempId(systemId);
        }
        DokumentfilResource resource = new DokumentfilResource();
        resource.setSystemId(FintUtils.createIdentifikator(systemId));
        if (StringUtils.isNotBlank(sikriDocument.getContentType())) {
            resource.setFormat(sikriDocument.getContentType());
        } else if (StringUtils.isNotBlank(sikriDocument.getFilename())) {
            resource.setFormat(getContentType(sikriDocument.getFilename()));
        } else {
            resource.setFormat(getContentType(sikriDocument.getContent()));
        }
        resource.setFilnavn(sikriDocument.getFilename());
        resource.setData(Base64.getEncoder().encodeToString(sikriDocument.getContent()));
        return resource;
    }

    private SikriDocumentService.SikriDocument getSikriDocument(String systemId) {
        final String[] strings = StringUtils.split(systemId, '_');
        int docId = Integer.parseInt(strings[0]);
        int version = Integer.parseInt(strings[1]);
        String variant = strings[2];
        return sikriDocumentService.getDocumentContentByDocumentId(docId, variant, version);
    }


    public DokumentfilResource createDokumentfil(DokumentfilResource resource) {
        final String docid = sikriDocumentService.uploadFile(Base64.getDecoder().decode(resource.getData()), resource.getFormat(), resource.getFilnavn());
        resource.setSystemId(FintUtils.createIdentifikator(docid));
        return resource;
    }


}
