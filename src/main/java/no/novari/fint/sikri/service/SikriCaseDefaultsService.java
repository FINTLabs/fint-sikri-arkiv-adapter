package no.novari.fint.sikri.service;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.novari.fint.arkiv.CaseDefaultsService;
import no.novari.fint.arkiv.CaseProperties;
import no.novari.fint.model.resource.arkiv.noark.SaksmappeResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SikriCaseDefaultsService extends CaseDefaultsService {

    public void applyDefaultsToCaseType(CaseProperties caseProperties, SaksmappeResource resource, CaseType caseType) {
        caseType.setIsPhysical(false);
        caseType.setFileTypeId(caseProperties.getSaksmappeType());
    }

}
