package no.fint.sikri.data.personal;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.model.arkiv.noark.Saksstatus;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.personal.PersonalmappeResource;
import no.fint.sikri.CaseDefaults;
import no.fint.sikri.data.CaseProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class PersonalmappeDefaults {
    @Autowired
    private CaseDefaults caseDefaults;

    private CaseProperties properties;
    private ObjectFactory objectFactory;


    @PostConstruct
    public void init() {
        log.info("Case Defaults: {}", caseDefaults);
        properties = caseDefaults.getCasetype().get("personalmappe");
        log.info("Defaults for Personalmappe: {}", properties);
        objectFactory = new ObjectFactory();
    }

    public void applyDefaultsForCreation(PersonalmappeResource personalmappeResource) {

        if (StringUtils.isNotEmpty(properties.getSaksstatus()) && personalmappeResource.getSaksstatus().isEmpty()) {
            personalmappeResource.addSaksstatus(Link.with(
                    Saksstatus.class,
                    "systemid",
                    properties.getSaksstatus()
            ));
        }
    }

    public void applyDefaultsToCaseType(PersonalmappeResource personalmappeResource, CaseType caseType) {

        caseType.setIsPhysical(objectFactory.createCaseTypeIsPhysical(false));

    }
}