package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.arkiv.DokumentfilResource;
import no.fint.sikri.data.utilities.ContentDisposition;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.service.SikriDocumentService;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Slf4j
public class DokumentfilService {

    @Autowired
    private SikriDocumentService sikriDocumentService;

    private String getContentType(String filename) {
        Tika tika = new Tika();
        return tika.detect(filename);
    }

    public DokumentfilResource getDokumentfil(String systemId) {
        final String[] strings = StringUtils.split(systemId, '_');
        int docId = Integer.parseInt(strings[0]);
        int version = Integer.parseInt(strings[1]);
        String variant = strings[2];
        final SikriDocumentService.SikriDocument sikriDocument = sikriDocumentService.getDocumentContentByDocumentId(docId, variant, version);
        DokumentfilResource resource = new DokumentfilResource();
        resource.setSystemId(FintUtils.createIdentifikator(systemId));
        if (StringUtils.isNotBlank(sikriDocument.getContentType())) {
            resource.setFormat(sikriDocument.getContentType());
        } else {
            resource.setFormat(getContentType(sikriDocument.getFilename()));
        }
        resource.setFilnavn(sikriDocument.getFilename());
        resource.setData(Base64.getEncoder().encodeToString(sikriDocument.getContent()));
        return resource;
    }

    /*
    public DokumentfilResource createDokumentfil(DokumentfilResource resource) {
        String docid = noark5WebService.upload(resource.getFilnavn(),
                MediaType.valueOf(resource.getFormat()),
                Base64.getDecoder().decode(resource.getData()));
        resource.setSystemId(FintUtils.createIdentifikator(docid));
        return resource;
    }

     */

}
