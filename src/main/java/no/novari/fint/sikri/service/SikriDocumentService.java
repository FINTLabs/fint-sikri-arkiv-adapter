package no.novari.fint.sikri.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.sikri.ds.*;
import no.novari.fint.sikri.AdapterProps;
import no.novari.fint.sikri.data.utilities.SikriUtils;
import no.novari.fint.sikri.model.SikriIdentity;
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

    public String uploadFile(SikriIdentity identity, byte[] content, String fileName) {
        Holder<String> identifier = new Holder<>();
        Holder<String> fileNameHolder = new Holder<>();
        fileNameHolder.value = fileName;
        UploadMessage parameters = new UploadMessage();
        parameters.setContent(content);
        documentService.uploadFile(parameters, null, mapIdentity(identity), fileNameHolder, "ObjectModelService", identifier);
        log.debug("uploadFile result: filename = {}, identifier = {}", fileNameHolder.value, identifier.value);
        return fileNameHolder.value;
    }

    public SikriDocument getTempDocumentContentByTempId(SikriIdentity identity, String identifier) {
        GetTempDocumentContentMessage parameters = new GetTempDocumentContentMessage();
        Holder<String> contentType = new Holder<>();
        Holder<String> fileName = new Holder<>();
        final EphorteIdentity ephorteIdentity = mapIdentity(identity);
        final DocumentReturnMessage documentReturnMessage = documentService.getTempDocumentContentByTempId(parameters, identifier, ephorteIdentity, contentType, fileName);
        return new SikriDocument(documentReturnMessage.getContent(), fileName.value, contentType.value);
    }

    public void checkin(SikriIdentity identity, Integer docId, String variant, Integer version, byte[] content, String guid, String path) {
        Holder<String> contentType = new Holder<>();
        final EphorteIdentity ephorteIdentity = mapIdentity(identity);
        CheckinMessage checkinMessage = new CheckinMessage();
        checkinMessage.setContent(content);
        DocumentCriteria documentCriteria = new DocumentCriteria();
        documentCriteria.setDocumentId(docId);
        documentCriteria.setEphorteIdentity(ephorteIdentity);
        documentCriteria.setVariant(variant);
        documentCriteria.setVersion(version);
        log.debug("Checkin {} ...", documentCriteria);
        documentService.checkin(checkinMessage, contentType.value, documentCriteria, guid, path);
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
