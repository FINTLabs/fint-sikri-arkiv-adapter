package no.fint.sikri.data.noark.codes.saksstatus;

import no.fint.arkiv.sikri.oms.CaseStatusType;
import no.fint.model.resource.administrasjon.arkiv.SaksstatusResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Stream;

@Service
public class SaksStatusService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<SaksstatusResource> getCaseStatusTable() {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CASE_STATUS, null, 0, Collections.emptyList())
                .stream()
                .map(CaseStatusType.class::cast)
                .map(BegrepMapper::mapSaksstatus);
    }
}
