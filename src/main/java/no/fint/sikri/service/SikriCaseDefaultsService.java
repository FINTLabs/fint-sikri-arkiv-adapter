package no.fint.sikri.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.CaseDefaultsService;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.arkiv.noark.SaksmappeResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SikriCaseDefaultsService extends CaseDefaultsService {

    private final CaseDefaults caseDefaults;

    public SikriCaseDefaultsService(CaseDefaults caseDefaults) {
        this.caseDefaults = caseDefaults;
    }

    public void applyDefaultsToCaseType(SaksmappeResource resource, CaseType caseType) {
        caseType.setIsPhysical(false);
        //TODO -> Configurable?
        if (!StringUtils.isEmpty(caseDefaults.getDrosjeloyve().getSaksmappeType())) {
            caseType.setFileTypeId(caseDefaults.getDrosjeloyve().getSaksmappeType());
        }
    }

}
