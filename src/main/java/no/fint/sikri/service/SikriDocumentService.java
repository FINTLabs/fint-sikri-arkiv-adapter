package no.fint.sikri.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.ds.DocumentService;
import no.fint.arkiv.sikri.ds.DocumentService_Service;
import no.fint.arkiv.sikri.ds.EphorteIdentity;
import no.fint.arkiv.sikri.ds.ObjectFactory;
import no.fint.sikri.AdapterProps;
import no.fint.sikri.utilities.SikriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
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
        super.setup(documentService, "DocumentService");
        objectFactory = new ObjectFactory();
        setupEphorteIdentity();
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

}