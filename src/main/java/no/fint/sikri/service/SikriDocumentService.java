package no.fint.sikri.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.ds.*;
import no.fint.sikri.AdapterProps;
import no.fint.sikri.utilities.SikriUtils;
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

    @Autowired
    private AdapterProps props;

    private DocumentService documentService;
    private ObjectFactory objectFactory;
    private EphorteIdentity ephorteIdentity;

    public SikriDocumentService() {
        super("http://www.gecko.no/ephorte/services/documents/v3", "DocumentService");
    }

    @PostConstruct
    public void init() throws MalformedURLException {

        URL wsdlLocationUrl = SikriUtils.getURL(wsdlLocation);
        DocumentService_Service ss = new DocumentService_Service(wsdlLocationUrl, SERVICE_NAME);
        documentService = ss.getBasicHttpsBindingMtomStreamedDocumentService(new AddressingFeature());
        super.setup(documentService, "/Services/Documents/V3/DocumentService.svc");
        objectFactory = new ObjectFactory();
        setupEphorteIdentity();
    }

    public SikriDocument getDocumentContentByDocumentId(int documentId, String variant, int version) {
        Holder<String> contentType = new Holder<>();
        Holder<String> fileName = new Holder<>();
        GetDocumentContentMessage parameters = objectFactory.createGetDocumentContentMessage();
        final DocumentReturnMessage documentReturnMessage = documentService.getDocumentContentBase(parameters, documentId, ephorteIdentity, variant, version, contentType, fileName);
        return new SikriDocument(documentReturnMessage.getContent(), fileName.value, contentType.value);
    }

    public SikriDocument getDocumentContentByJournalPostId(int journalpostId) {
        Holder<String> contentType = new Holder<>();
        Holder<String> fileName = new Holder<>();
        GetJournalpostDocumentContentMessage parameters = objectFactory.createGetJournalpostDocumentContentMessage();
        final DocumentReturnMessage documentReturnMessage = documentService.getDocumentContentByJournalPostId(parameters, ephorteIdentity, journalpostId, contentType, fileName);
        return new SikriDocument(documentReturnMessage.getContent(), fileName.value, contentType.value);
    }

    public String uploadFile(byte[] content, String contentType, String fileName) {
        Holder<String> identifier = new Holder<>();
        Holder<String> fileNameHolder = new Holder<>();
        fileNameHolder.value = fileName;
        UploadMessage parameters = objectFactory.createUploadMessage();
        parameters.setContent(content);
        documentService.uploadFile(parameters, contentType, ephorteIdentity, fileNameHolder, null, identifier);
        log.info("uploadFile result: filename = {}, identifier = {}", fileNameHolder.value, identifier.value);
        return identifier.value;
    }

    public void checkin(Integer docId, String variant, Integer version, String identifier) {
        Holder<String> contentType = new Holder<>();
        Holder<String> fileName = new Holder<>();
        log.info("Try fetch document {} ...", identifier);
        final DocumentReturnMessage documentReturnMessage = documentService.getTempDocumentContentByTempId(null, identifier, ephorteIdentity, contentType, fileName);
        CheckinMessage checkinMessage = objectFactory.createCheckinMessage();
        byte[] content = new byte[documentReturnMessage.getContent().length];
        System.arraycopy(documentReturnMessage.getContent(), 0, content, 0, content.length);
        checkinMessage.setContent(content);
        DocumentCriteria documentCriteria = objectFactory.createDocumentCriteria();
        documentCriteria.setDocumentId(docId);
        documentCriteria.setEphorteIdentity(objectFactory.createDocumentCriteriaEphorteIdentity(ephorteIdentity));
        documentCriteria.setVariant(objectFactory.createDocumentCriteriaVariant(variant));
        documentCriteria.setVersion(version);
        log.info("Checkin {} ...", documentCriteria);
        documentService.checkin(checkinMessage, contentType.value, documentCriteria, identifier, fileName.value);
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

    private void setupEphorteIdentity() {
        ephorteIdentity = objectFactory.createEphorteIdentity();
        ephorteIdentity.setDatabase(objectFactory.createEphorteIdentityDatabase(props.getDatabase()));
        ephorteIdentity.setExternalSystemName(objectFactory.createEphorteIdentityExternalSystemName(props.getExternalSystemName()));
        ephorteIdentity.setUserName(objectFactory.createEphorteIdentityUserName(props.getUser()));
        ephorteIdentity.setPassword(objectFactory.createEphorteIdentityPassword(props.getPassword()));
    }

    @Data
    public static class SikriDocument {
        private final byte[] content;
        private final String filename;
        private final String contentType;
    }
}
