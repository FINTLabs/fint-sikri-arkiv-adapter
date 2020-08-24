package no.fint.sikri.data.noark.administrativenhet;

import no.fint.arkiv.sikri.oms.AdministrativeUnitType;
import no.fint.model.administrasjon.arkiv.AdministrativEnhet;
import no.fint.model.resource.administrasjon.arkiv.AdministrativEnhetResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class AdministrativEnhetService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<AdministrativEnhetResource> getAdministrativeEnheter() {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.ADMINISTRATIVE_UNIT)
                .stream()
                .map(AdministrativeUnitType.class::cast)
                .filter(administrativeUnitType -> StringUtils.isNotBlank(administrativeUnitType.getShortCodeThisLevel()))
                .filter(administrativeUnitType -> administrativeUnitType.getClosedDate() != null)
                .map(BegrepMapper::mapAdministrativEnhet);
    }
}
