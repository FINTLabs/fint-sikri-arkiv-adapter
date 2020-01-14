package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DokumentfilService {

//    @Autowired
//    private Noark5WebService noark5WebService;

//    public DokumentfilResource getDokumentfil(String systemId) {
//        ResponseEntity<byte[]> responseEntity = noark5WebService.download(systemId);
//        DokumentfilResource resource = new DokumentfilResource();
//        resource.setSystemId(FintUtils.createIdentifikator(systemId));
//        resource.setFormat(responseEntity.getHeaders().getContentType().toString());
//        resource.setFilnavn(ContentDisposition.parse(responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).getFilename());
//        resource.setData(Base64.getEncoder().encodeToString(responseEntity.getBody()));
//        return resource;
//    }
//
//    public DokumentfilResource createDokumentfil(DokumentfilResource resource) {
//        String docid = noark5WebService.upload(resource.getFilnavn(),
//                MediaType.valueOf(resource.getFormat()),
//                Base64.getDecoder().decode(resource.getData()));
//        resource.setSystemId(FintUtils.createIdentifikator(docid));
//        return resource;
//    }

}
