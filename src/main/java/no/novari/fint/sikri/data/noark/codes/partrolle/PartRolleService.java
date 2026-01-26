package no.novari.fint.sikri.data.noark.codes.partrolle;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CasePartyRoleType;
import no.novari.fint.model.resource.arkiv.kodeverk.PartRolleResource;
import no.novari.fint.sikri.data.utilities.BegrepMapper;
import no.novari.fint.sikri.service.SikriIdentityService;
import no.novari.fint.sikri.service.SikriObjectModelService;
import no.novari.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Slf4j
@Service
public class PartRolleService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<PartRolleResource> getPartRolle() {
        return sikriObjectModelService
                .getDataObjects(
                        identityService.getDefaultIdentity(),
                        SikriObjectTypes.CASE_PARTY_ROLE)
                .stream()
                .map(CasePartyRoleType.class::cast)
                .map(BegrepMapper::mapPartRolle);
    }
}
