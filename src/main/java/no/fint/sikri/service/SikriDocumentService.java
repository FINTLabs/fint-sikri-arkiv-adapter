package no.fint.sikri.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.ds.*;
import no.fint.sikri.AdapterProps;
import no.fint.sikri.data.utilities.SikriUtils;
import no.fint.sikri.model.SikriIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.AddressingFeature;
import java.net.MalformedURLException;
import java.net.URL;


@Slf4j
@Service
public class SikriDocumentService extends SikriAbstractService {
    private static final QName SERVICE_NAME = new QName("http://www.gecko.no/ephorte/services/documents/v3", "DocumentService");

    @Value("${fint.sikri.wsdl-location:./src/main/resources/wsdl}/DocumentServiceV3.wsdl")
    private String wsdlLocation;

    @Value("${fint.sikri.documents.storage-identifier:}")
    private String storageIdentifier;

    @Autowired
    private AdapterProps adapterProps;

    private DocumentService documentService;

    public SikriDocumentService() {
        super("http://www.gecko.no/ephorte/services/documents/v3", "DocumentService");
    }

    @PostConstruct
    public void init() throws MalformedURLException {

        URL wsdlLocationUrl = SikriUtils.getURL(wsdlLocation);
        DocumentService_Service ss = new DocumentService_Service(wsdlLocationUrl, SERVICE_NAME);
        documentService = ss.getBasicHttpsBindingMtomStreamedDocumentService(new AddressingFeature());
        super.setup(documentService, "/Services/Documents/V3/DocumentService.svc");
    }

    public SikriDocument getDocumentContentByDocumentId(SikriIdentity identity, int documentId, String variant, int version) {
        Holder<String> contentType = new Holder<>();
        Holder<String> fileName = new Holder<>();
        GetDocumentContentMessage parameters = new GetDocumentContentMessage();
        final DocumentReturnMessage documentReturnMessage = documentService.getDocumentContentBase(parameters, documentId, mapIdentity(identity), variant, version, contentType, fileName);
        return new SikriDocument(documentReturnMessage.getContent(), fileName.value, contentType.value);
    }

    public SikriDocument getDocumentContentByJournalPostId(SikriIdentity identity, int journalpostId) {
        Holder<String> contentType = new Holder<>();
        Holder<String> fileName = new Holder<>();
        GetJournalpostDocumentContentMessage parameters = new GetJournalpostDocumentContentMessage();
        final DocumentReturnMessage documentReturnMessage = documentService.getDocumentContentByJournalPostId(parameters, mapIdentity(identity), journalpostId, contentType, fileName);
        return new SikriDocument(documentReturnMessage.getContent(), fileName.value, contentType.value);
    }

    public String uploadFile(SikriIdentity identity, byte[] content, String contentType, String fileName) {
        Holder<String> identifier = new Holder<>();
        Holder<String> fileNameHolder = new Holder<>();
        fileNameHolder.value = fileName;
        UploadMessage parameters = new UploadMessage();
        parameters.setContent(content);
        final UploadReturnMessage uploadReturnMessage = documentService.uploadFile(parameters, contentType, mapIdentity(identity), fileNameHolder, storageIdentifier, identifier);
        log.debug("uploadFile result: filename = {}, identifier = {}, returnMessage = {}", fileNameHolder.value, identifier.value, uploadReturnMessage);
        return identifier.value;
    }

    public SikriDocument getTempDocumentContentByTempId(SikriIdentity identity, String identifier) {
        GetTempDocumentContentMessage parameters = new GetTempDocumentContentMessage();
        Holder<String> contentType = new Holder<>();
        Holder<String> fileName = new Holder<>();
        final EphorteIdentity ephorteIdentity = mapIdentity(identity);
        final DocumentReturnMessage documentReturnMessage = documentService.getTempDocumentContentByTempId(parameters, identifier, ephorteIdentity, contentType, fileName);
        return new SikriDocument(documentReturnMessage.getContent(), fileName.value, contentType.value);
    }

    public void checkin(SikriIdentity identity, Integer docId, String variant, Integer version, String identifier) {
        Holder<String> contentType = new Holder<>();
        Holder<String> fileName = new Holder<>();
        final EphorteIdentity ephorteIdentity = mapIdentity(identity);
        DocumentCriteria documentCriteria = new DocumentCriteria();
        documentCriteria.setDocumentId(docId);
        documentCriteria.setEphorteIdentity(ephorteIdentity);
        documentCriteria.setVariant(variant);
        documentCriteria.setVersion(version);
        log.debug("Checkin {} ...", documentCriteria);
        documentService.checkin(null, contentType.value, documentCriteria, identifier, fileName.value);
    }

    private EphorteIdentity mapIdentity(SikriIdentity identity) {
        log.debug("Using identity {} / {}", identity.getUsername(), identity.getExternalSystemName());
        EphorteIdentity ephorteIdentity = new EphorteIdentity();
        ephorteIdentity.setUserName(identity.getUsername());
        ephorteIdentity.setPassword(identity.getPassword());
        ephorteIdentity.setExternalSystemName(identity.getExternalSystemName());
        ephorteIdentity.setRole(identity.getRole());
        ephorteIdentity.setDatabase(adapterProps.getDatabase());
        return ephorteIdentity;
    }
    /*
    public ResponseEntity<byte[]> download(String docId) {
        GetDocumentContentMessage param = objectFactory.();
        //param.
        documentService.getDocumentContentBase(param)
        //return restTemplate.getForEntity("/rms/api/public/noark5/v1/download?id={docId}", byte[].class, docId);
    }

    public String upload(String filename, MediaType contentType, byte[] data) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.builder("attachment")
                        .filename(filename, StandardCharsets.UTF_8)
                        .build()
                        .toString());
        HttpEntity<byte[]> entity = new HttpEntity<>(data, headers);
        ResponseEntity<JsonNode> result = restTemplate.exchange("/rms/api/public/noark5/v1/upload", HttpMethod.POST, entity, JsonNode.class);
        return result.getBody().get("id").asText();
    }
 */


    public boolean isHealty() {
        return true;
    }

    @Data
    public static class SikriDocument {
        private final byte[] content;
        private final String filename;
        private final String contentType;
    }
}
