package no.fint.sikri.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaultsService;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class SikriCaseDefaultsService extends CaseDefaultsService {
    private ObjectFactory objectFactory;

    @PostConstruct
    public void init() {
        objectFactory = new ObjectFactory();
    }

    public void applyDefaultsToCaseType(SaksmappeResource saksmappeResource, CaseType caseType) {
        caseType.setIsPhysical(objectFactory.createBoolean(false));
    }

}
