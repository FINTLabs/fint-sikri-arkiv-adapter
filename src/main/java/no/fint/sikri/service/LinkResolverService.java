package no.fint.sikri.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.LinkResolver;
import no.fint.model.resource.Link;
import no.fint.model.resource.felles.kodeverk.FylkeResource;
import no.fint.model.resource.felles.kodeverk.KommuneResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class LinkResolverService implements LinkResolver {
    private final RestTemplate restTemplate;

    public LinkResolverService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Object resolve(Link link) {
        final String href = link.getHref();
        if (!StringUtils.startsWith(href, "https://")) {
            return null;
        }
        if (StringUtils.contains(href, "/felles/kodeverk/kommune/")) {
            final KommuneResource kommune = restTemplate.getForObject(href, KommuneResource.class);
            log.info("Found {}", kommune);
            return kommune;
        }
        if (StringUtils.contains(href, "/felles/kodeverk/fylke/")) {
            final FylkeResource fylke = restTemplate.getForObject(href, FylkeResource.class);
            log.info("Found {}", fylke);
            return fylke;
        }
        log.info("No known resource for link {}", href);
        return null;
    }
}
