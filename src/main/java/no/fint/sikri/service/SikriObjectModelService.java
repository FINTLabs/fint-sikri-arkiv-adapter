package no.fint.sikri.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.*;
import no.fint.sikri.AdapterProps;
import no.fint.sikri.data.utilities.SikriUtils;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import javax.xml.ws.soap.AddressingFeature;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Slf4j
@Service
public class SikriObjectModelService extends SikriAbstractService {
    private static final QName SERVICE_NAME = new QName("http://www.gecko.no/ephorte/services/objectmodel/v3/en", "ObjectModelService");

    @Value("${fint.sikri.wsdl-location:./src/main/resources/wsdl}/ObjectModelServiceV3En.wsdl")
    private String wsdlLocation;

    @Autowired
    private AdapterProps props;

    private ObjectModelService objectModelService;
    private EphorteIdentity ephorteIdentity;

    public SikriObjectModelService() {
        super("http://www.gecko.no/ephorte/services/objectmodel/v3/en", "ObjectModelService");
    }

    @PostConstruct
    public void init() throws MalformedURLException {

        URL wsdlLocationUrl = SikriUtils.getURL(wsdlLocation);
        ObjectModelService_Service ss = new ObjectModelService_Service(wsdlLocationUrl, SERVICE_NAME);
        objectModelService = ss.getWsHttpsBindingTextObjectModelService(new AddressingFeature());
        super.setup(objectModelService, "/Services/ObjectModel/V3/En/ObjectModelService.svc");
        setupEphorteIdentity();

        final List<DataObject> externalSystems = getDataObjects("ExternalSystem", "ExternalSystemName=FINT");
        if (externalSystems.isEmpty()) {
            log.info("Creating ExternalSystem FINT ...");
            ExternalSystemType externalSystem = new ExternalSystemType();
            externalSystem.setExternalSystemName("FINT");
            externalSystem.setIsActive(true);
            final ExternalSystemType result = createDataObject(externalSystem);
            log.info("Result: {}", result);
        }
    }

    public List<DataObject> getDataObjects(String dataObjectName, String filter, int count, String... relatedObjects) {
        return getDataObjects(dataObjectName, filter, count, Arrays.asList(relatedObjects));
    }

    public List<DataObject> getDataObjects(String dataObjectName, String filter, int count, Collection<String> relatedObjects) {
        FilteredQueryArguments filteredQueryArguments = new FilteredQueryArguments();

        if (count > 0) {
            filteredQueryArguments.setTakeCount(count);
        }
        filteredQueryArguments.setDataObjectName(dataObjectName);

        if (StringUtils.isNotEmpty(filter)) {
            filteredQueryArguments.setFilterExpression(filter);
        }

        ArrayOfstring related = new ArrayOfstring();
        relatedObjects.forEach(related.getString()::add);
        filteredQueryArguments.setRelatedObjects(related);

        QueryResult queryResult = objectModelService.filteredQuery(ephorteIdentity, filteredQueryArguments);

        return queryResult.getDataObjects().getDataObject();

    }

    public List<DataObject> getDataObjects(String dataObjectName) {
        return getDataObjects(dataObjectName, null, 0, Collections.emptyList());
    }

    public List<DataObject> getDataObjects(String dataObjectName, String filter) {
        return getDataObjects(dataObjectName, filter, 0, Collections.emptyList());
    }

    public List<DataObject> getDataObjects(String dataObjectName, String filter, List<String> relatedObjects) {
        return getDataObjects(dataObjectName, filter, 0, relatedObjects);
    }

    public <T extends DataObject> T createDataObject(T dataObject) {
        ArrayOfDataObject arrayOfDataObject = new ArrayOfDataObject();
        arrayOfDataObject.getDataObject().add(dataObject);
        ArrayOfDataObject insert = objectModelService.insert(ephorteIdentity, arrayOfDataObject);
        log.info("Created {} objects", insert.getDataObject().size());
        if (insert.getDataObject().size() == 1) {
            return (T) insert.getDataObject().get(0);
        }
        return null;
    }

    public List<DataObject> createDataObjects(DataObject... objects) {
        ArrayOfDataObject arrayOfDataObject = new ArrayOfDataObject();
        for (DataObject object : objects) {
            arrayOfDataObject.getDataObject().add(object);
        }
        ArrayOfDataObject insert = objectModelService.insert(ephorteIdentity, arrayOfDataObject);
        log.info("Created {} objects", insert.getDataObject().size());
        return insert.getDataObject();
    }

    public List<DataObject> updateDataObjects(DataObject... dataObjects) {
        ArrayOfDataObject arrayOfDataObject = new ArrayOfDataObject();
        for (DataObject dataObject : dataObjects) {
            arrayOfDataObject.getDataObject().add(dataObject);
        }
        ArrayOfDataObject update = objectModelService.update(ephorteIdentity, arrayOfDataObject);
        log.info("Updated {} objects", update.getDataObject().size());
        return update.getDataObject();
    }

    public DataObject updateDataObject(DataObject dataObject) {
        ArrayOfDataObject arrayOfDataObject = new ArrayOfDataObject();
        arrayOfDataObject.getDataObject().add(dataObject);
        ArrayOfDataObject update = objectModelService.update(ephorteIdentity, arrayOfDataObject);
        log.info("Updated {} objects", update.getDataObject().size());
        if (update.getDataObject().size() == 1) {
            return update.getDataObject().get(0);
        }
        return null;
    }

    public boolean isHealty() {
        List<DataObject> accessCode = getDataObjects(SikriObjectTypes.ACCESS_CODE, null, 1, Collections.emptyList());
        return accessCode.size() == 1;
    }

    private void setupEphorteIdentity() {
        ephorteIdentity = new EphorteIdentity();
        ephorteIdentity.setDatabase(props.getDatabase());
        ephorteIdentity.setExternalSystemName(props.getExternalSystemName());
        ephorteIdentity.setUserName(props.getUser());
        ephorteIdentity.setPassword(props.getPassword());
        if (StringUtils.isNotBlank(props.getRole()))
            ephorteIdentity.setRole(props.getRole());
    }

}
