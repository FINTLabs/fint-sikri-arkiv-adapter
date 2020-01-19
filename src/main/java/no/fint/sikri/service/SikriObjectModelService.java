package no.fint.sikri.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.*;
import no.fint.sikri.AdapterProps;
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

    public List<DataObject> getDataObjects(String dataObjectName, String filter) {
        return getDataObjects(dataObjectName, filter, 0, Collections.emptyList());
    }

    public List<DataObject> getDataObjects(String dataObjectName, String filter, List<String> relatedObjects) {
        return getDataObjects(dataObjectName, filter, 0, relatedObjects);
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
