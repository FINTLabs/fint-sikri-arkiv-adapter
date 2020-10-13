package no.fint.sikri.data.noark.administrativenhet;

import no.fint.arkiv.sikri.oms.AdministrativeUnitType;
import no.fint.model.arkiv.noark.AdministrativEnhet;
import no.fint.model.resource.arkiv.noark.AdministrativEnhetResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
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
                .filter(administrativeUnitType -> !administrativeUnitType.getShortCodeThisLevel().isNil())
                .filter(administrativeUnitType -> administrativeUnitType.getClosedDate().isNil())
                .map(BegrepMapper::mapAdministrativEnhet);
    }
}
