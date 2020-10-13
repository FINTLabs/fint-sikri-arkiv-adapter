package no.fint.sikri.data.noark.codes.partrolle;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CasePartyRoleType;
import no.fint.model.resource.arkiv.noark.PartRolleResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Stream;

@Slf4j
@Service
public class PartRolleService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<PartRolleResource> getPartRolle() {
        return sikriObjectModelService
                .getDataObjects(SikriObjectTypes.CASE_PARTY_ROLE)
                .stream()
                .map(CasePartyRoleType.class::cast)
                .map(BegrepMapper::mapPartRolle);
    }
}
