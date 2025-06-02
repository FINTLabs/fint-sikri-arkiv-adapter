package no.fint.adapter.event;

import lombok.extern.slf4j.Slf4j;
import no.fint.adapter.FintAdapterEndpoints;
import no.fint.adapter.FintAdapterProps;
import no.fint.event.model.DefaultActions;
import no.fint.event.model.Event;
import no.fint.event.model.HeaderConstants;
import no.fint.event.model.Status;
import no.fint.sikri.SupportedActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Handles statuses back to the provider status endpoint.
 */
@Slf4j
@Service
public class EventStatusService {

    @Autowired
    private FintAdapterEndpoints endpoints;

    @Autowired
    @Qualifier("oauth2RestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private SupportedActions supportedActions;

    @Autowired
    private FintAdapterProps props;

    /**
     * Verifies if we can handle the event and set the status accordingly.
     *
     * @param component
     * @param event
     * @return The inbound event.
     */
    public boolean verifyEvent(String component, Event event) {
        if (supportedActions.supports(event.getAction()) || DefaultActions.getDefaultActions().contains(event.getAction())) {
            event.setStatus(Status.ADAPTER_ACCEPTED);
        } else if (props.isRejectUnknownEvents()) {
            log.info("Rejecting {}", event.getAction());
            event.setStatus(Status.ADAPTER_REJECTED);
        } else {
            return false;
        }

        log.info("{}: Posting status for {} {} ...", component, event.getAction(), event.getCorrId());
        return postStatus(component, event) && event.getStatus() == Status.ADAPTER_ACCEPTED;
    }

    /**
     * Method for posting back the status to the provider.
     *
     * @param component Name of component
     * @param event     Event to send
     */
    public boolean postStatus(String component, Event event) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HeaderConstants.ORG_ID, event.getOrgId());
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            headers.add(HeaderConstants.CLIENT, "sikri-adapter@" + event.getOrgId());
            String url = endpoints.getProviders().get(component) + endpoints.getStatus();
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(event, headers), Void.class);
            log.info("{}: Provider POST status response: {}", component, response.getStatusCode());
            return true;
        } catch (RestClientException e) {
            log.warn("{}: Provider POST status error: {}", component, e.getMessage());
        }
        return false;
    }
}
