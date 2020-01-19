package no.fint.sikri.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.*;
import no.fint.sikri.AdapterProps;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.utilities.SikriObjectTypes;
import no.fint.sikri.utilities.SikriUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.AddressingFeature;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class SikriObjectModelService extends SikriAbstractService {
    private static final QName SERVICE_NAME = new QName("http://www.gecko.no/ephorte/services/objectmodel/v3/en", "ObjectModelService");

    @Value("${fint.sikri.wsdl-location:./src/main/resources/wsdl}/ObjectModelServiceV3En.wsdl")
    private String wsdlLocation;


    @Autowired
    private AdapterProps props;

    private ObjectModelService objectModelService;
    private ObjectFactory objectFactory;
    private EphorteIdentity ephorteIdentity;

    public SikriObjectModelService() {
        super("http://www.gecko.no/ephorte/services/objectmodel/v3/en", "ObjectModelService");
    }

    @PostConstruct
    public void init() throws MalformedURLException {

        URL wsdlLocationUrl = SikriUtils.getURL(wsdlLocation);
        ObjectModelService_Service ss = new ObjectModelService_Service(wsdlLocationUrl, SERVICE_NAME);
        objectModelService = ss.getWsHttpsBindingTextObjectModelService(new AddressingFeature());
        super.setup(objectModelService, "ObjectModelService");
        objectFactory = new ObjectFactory();
        setupEphorteIdentity();
    }


    public List<DataObject> getDataObjects(String dataObjectName, String filter, int count, List<String> relatedObjects) {
        FilteredQueryArguments filteredQueryArguments = objectFactory.createFilteredQueryArguments();

        if (count > 0) {
            filteredQueryArguments.setTakeCount(objectFactory.createFilteredQueryArgumentsTakeCount(count));
        }
        filteredQueryArguments.setDataObjectName(dataObjectName);

        if (StringUtils.isNotEmpty(filter)) {
            filteredQueryArguments.setFilterExpression(objectFactory.createFilteredQueryArgumentsFilterExpression(filter));
        }

        JAXBElement<ArrayOfstring> related = objectFactory.createFilteredQueryArgumentsRelatedObjects(objectFactory.createArrayOfstring());
        relatedObjects.forEach(o -> related.getValue().getString().add(o));
        filteredQueryArguments.setRelatedObjects(related);

        QueryResult queryResult = objectModelService.filteredQuery(ephorteIdentity, filteredQueryArguments);

        return queryResult.getDataObjects().getValue().getDataObject();

    }

    public List<DataObject> getDataObjects(String dataObjectName) {
        return getDataObjects(dataObjectName, null, 0, Collections.emptyList());
    }

    public CaseType getSakByCaseNumber(String caseNumber) throws CaseNotFound, IllegalCaseNumberFormat {
        String sequenceNumber = NOARKUtils.getCaseSequenceNumber(caseNumber);
        String caseYear = NOARKUtils.getCaseYear(caseNumber);
        List<DataObject> dataObjects = getDataObjects(
                SikriObjectTypes.CASE,
                "SequenceNumber=" + sequenceNumber + " AND CaseYear=" + caseYear,
                0,
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION));

        if (dataObjects.size() == 1) {
            return (CaseType) dataObjects.get(0);
        }
        throw new CaseNotFound("Found " + dataObjects.size() + " cases. Should be 1.");
    }

    public CaseType getSakBySystemId(String systemId) throws CaseNotFound, IllegalCaseNumberFormat {

        List<DataObject> dataObjects = getDataObjects(
                SikriObjectTypes.CASE,
                "Id=" + systemId,
                0,
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION));

        if (dataObjects.size() == 1) {
            return (CaseType) dataObjects.get(0);
        }
        throw new CaseNotFound("Found " + dataObjects.size() + " cases. Should be 1.");
    }

    public List<CaseType> getGetCasesQueryByTitle(Map<String, Object> params) throws CaseNotFound, IllegalCaseNumberFormat {
        String filter = String.format("Title=%s", params.get("title"));
        List<DataObject> dataObjects = getDataObjects(
                SikriObjectTypes.CASE,
                filter,
                Integer.parseInt((String) params.getOrDefault("maxResult", "10")),
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION));

        return dataObjects.stream().map(CaseType.class::cast).collect(Collectors.toList());
    }

    public List<RegistryEntryType> getRegistryEntries(String caseId) {

        List<DataObject> dataObjects = getDataObjects(SikriObjectTypes.REGISTRY_ENTRY, "CaseId=" + caseId, 0, Collections.emptyList());

        return dataObjects.stream().map(RegistryEntryType.class::cast).collect(Collectors.toList());
    }

    public List<RegistryEntryDocumentType> getRegistryEntryDocuments(String registryEntryId) {

        List<DataObject> dataObjects = getDataObjects(SikriObjectTypes.REGISTRY_ENTRY_DOCUMENT, "RegistryEntryId=" + registryEntryId, 0, Collections.emptyList());

        return dataObjects.stream().map(RegistryEntryDocumentType.class::cast).collect(Collectors.toList());
    }

    public List<RegistryEntryTypeType> getRegistryEntryTypes(String id) {

        List<DataObject> dataObjects = getDataObjects(SikriObjectTypes.REGISTRY_ENTRY_TYPES, "Id=" + id, 0, Arrays.asList(SikriObjectTypes.DOCUMENT_DESCRIPTION));

        return dataObjects.stream().map(RegistryEntryTypeType.class::cast).collect(Collectors.toList());
    }

    /*
    public DocumentDescriptionType getDocumentDescription(String id)  {

        List<DataObject> dataObjects = getDataObjects(SikriObjectTypes.DOCUMENT_DESCRIPTION, "Id=" + id, 0, Collections.emptyList());

        if (dataObjects.size() == 1) {
            return (DocumentDescriptionType) dataObjects.get(0);
        }

        throw new GetDocumentDescriptionNotFoundException((dataObjects.size() == 0) ? "No objects found" : "More than one object found");
    }
     */

    public List<DocumentObjectType> getDocumentObject(String documentDescriptionId)  {

        List<DataObject> dataObjects = getDataObjects(SikriObjectTypes.DOCUMENT_OBJECT, "DocumentDescriptionId=" + documentDescriptionId, 0, Arrays.asList("FileFormat"));

        return dataObjects.stream().map(DocumentObjectType.class::cast).collect(Collectors.toList());
    }

    public List<SenderRecipientType> getSenderRecipents(String registryEntryId) {
        List<DataObject> dataObjects = getDataObjects(SikriObjectTypes.SENDER_RECIPIENT, "RegistryEntryId=" + registryEntryId, 0, Collections.emptyList());

        return dataObjects.stream().map(SenderRecipientType.class::cast).collect(Collectors.toList());
    }


    public boolean isHealty() {
        List<DataObject> accessCode = getDataObjects(SikriObjectTypes.ACCESS_CODE, null, 1, Collections.emptyList());
        return accessCode.size() == 1;
    }

    private void setupEphorteIdentity() {
        ephorteIdentity = objectFactory.createEphorteIdentity();
        ephorteIdentity.setDatabase(objectFactory.createEphorteIdentityDatabase(props.getDatabase()));
        ephorteIdentity.setExternalSystemName(objectFactory.createEphorteIdentityExternalSystemName(props.getExternalSystemName()));
        ephorteIdentity.setUserName(objectFactory.createEphorteIdentityUserName(props.getUser()));
        ephorteIdentity.setPassword(objectFactory.createEphorteIdentityPassword(props.getPassword()));
    }
}
