package no.fint.sikri.data.noark.administrativenhet;

import no.fint.arkiv.sikri.oms.AdministrativeUnitType;
import no.fint.model.resource.arkiv.noark.AdministrativEnhetResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.EphorteIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
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
    private EphorteIdentityService identityService;

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
