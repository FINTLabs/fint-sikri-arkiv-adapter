package no.novari.fint.sikri.data.noark.arkivdel;

import no.fint.arkiv.sikri.oms.SeriesType;
import no.novari.fint.model.resource.arkiv.noark.ArkivdelResource;
import no.novari.fint.sikri.data.utilities.BegrepMapper;
import no.novari.fint.sikri.service.SikriIdentityService;
import no.novari.fint.sikri.service.SikriObjectModelService;
import no.novari.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class ArkivdelService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<ArkivdelResource> getArkivdeler() {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.SERIES)
                .stream()
                .map(SeriesType.class::cast)
                .map(BegrepMapper::mapArkivdel);
    }
}
