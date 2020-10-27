package no.fint.sikri.data.noark.arkivdel;

import no.fint.arkiv.sikri.oms.SeriesType;
import no.fint.model.resource.arkiv.noark.ArkivdelResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.EphorteIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class ArkivdelService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private EphorteIdentityService identityService;

    public Stream<ArkivdelResource> getArkivdeler() {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.SERIES)
                .stream()
                .map(SeriesType.class::cast)
                .map(BegrepMapper::mapArkivdel);
    }
}
