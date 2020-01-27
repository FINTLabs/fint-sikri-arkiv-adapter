package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.arkiv.DokumentfilResource;
import no.fint.sikri.data.utilities.ContentDisposition;
import no.fint.sikri.data.utilities.FintUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@Slf4j
public class DokumentfilService {

    /*
    @Autowired
    private Noark5WebService noark5WebService;

    public DokumentfilResource getDokumentfil(String systemId) {
        ResponseEntity<byte[]> responseEntity = noark5WebService.download(systemId);
        DokumentfilResource resource = new DokumentfilResource();
        resource.setSystemId(FintUtils.createIdentifikator(systemId));
        resource.setFormat(responseEntity.getHeaders().getContentType().toString());
        resource.setFilnavn(ContentDisposition.parse(responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).getFilename());
        resource.setData(Base64.getEncoder().encodeToString(responseEntity.getBody()));
        return resource;
    }

    public DokumentfilResource createDokumentfil(DokumentfilResource resource) {
        String docid = noark5WebService.upload(resource.getFilnavn(),
                MediaType.valueOf(resource.getFormat()),
                Base64.getDecoder().decode(resource.getData()));
        resource.setSystemId(FintUtils.createIdentifikator(docid));
        return resource;
    }
     */

}
