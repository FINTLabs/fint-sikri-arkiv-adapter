package no.fint.sikri.data.noark.klasse;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.ClassType;
import no.fint.model.resource.arkiv.noark.KlasseResource;
import no.fint.sikri.service.EphorteIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Slf4j
@Service
public class KlasseService {

    @Autowired
    private KlasseFactory klasseFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private EphorteIdentityService identityService;

    public Stream<KlasseResource> getKlasserByEmneId(String id) {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.CLASS, "ClassificationSystemId=" + id)
                .stream().map(ClassType.class::cast)
                .map(klasseFactory::toFintResource);
    }


}
