package no.fint.sikri.service;

import no.fint.sikri.utilities.RequestUtilities;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

public abstract class SikriAbstractService {

    @Autowired
    protected RequestUtilities requestUtilities;

    final QName serviceName;

    public SikriAbstractService(String namespaceURI, String localPart) {
        serviceName = new QName(namespaceURI, localPart);
    }

    void setup(Object port, String service) {
        BindingProvider bp = (BindingProvider) port;
        //requestUtilities.addAuthentication(bp.getRequestContext());
        requestUtilities.setEndpointAddress(bp.getRequestContext(), service);
    }
}