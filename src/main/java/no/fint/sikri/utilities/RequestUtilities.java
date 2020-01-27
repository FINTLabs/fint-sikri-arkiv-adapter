package no.fint.sikri.utilities;

import no.fint.sikri.AdapterProps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingProvider;
import java.util.Map;


@Component
public class RequestUtilities {

    @Autowired
    private AdapterProps appProps;

    public void addAuthentication(Map<String, Object> map) {
        map.put(BindingProvider.USERNAME_PROPERTY, appProps.getUser());
        map.put(BindingProvider.PASSWORD_PROPERTY, appProps.getPassword());
    }

    public void setEndpointAddress(Map<String, Object> map, String service) {
        map.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                String.format("%s/Services/ObjectModel/V3/En/%s.svc", appProps.getEndpointBaseUrl(), service));
        //map.put("http://www.w3.org/2005/08/addressing", "Action");
    }
}
