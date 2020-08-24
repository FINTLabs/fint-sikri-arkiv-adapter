package no.fint.sikri.data.noark.arkivdel;

import no.fint.arkiv.sikri.oms.AdministrativeUnitType;
import no.fint.arkiv.sikri.oms.SeriesType;
import no.fint.model.resource.administrasjon.arkiv.AdministrativEnhetResource;
import no.fint.model.resource.administrasjon.arkiv.ArkivdelResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class ArkivdelService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<ArkivdelResource> getArkivdeler() {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.SERIES)
                .stream()
                .map(SeriesType.class::cast)
                .map(BegrepMapper::mapArkivdel);
    }
}
