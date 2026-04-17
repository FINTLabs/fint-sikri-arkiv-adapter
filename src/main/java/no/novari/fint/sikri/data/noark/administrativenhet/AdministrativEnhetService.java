package no.novari.fint.sikri.data.noark.administrativenhet;

import no.novari.fint.arkiv.sikri.oms.AdministrativeUnitType;
import no.novari.fint.model.resource.arkiv.noark.AdministrativEnhetResource;
import no.novari.fint.sikri.data.utilities.BegrepMapper;
import no.novari.fint.sikri.service.SikriIdentityService;
import no.novari.fint.sikri.service.SikriObjectModelService;
import no.novari.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Stream;

@Service
public class AdministrativEnhetService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<AdministrativEnhetResource> getAdministrativeEnheter() {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.ADMINISTRATIVE_UNIT)
                .stream()
                .map(AdministrativeUnitType.class::cast)
                .filter(administrativeUnitType -> !StringUtils.isBlank(administrativeUnitType.getShortCodeThisLevel()))
                .filter(administrativeUnitType -> Objects.isNull(administrativeUnitType.getClosedDate()))
                .map(BegrepMapper::mapAdministrativEnhet);
    }
}
