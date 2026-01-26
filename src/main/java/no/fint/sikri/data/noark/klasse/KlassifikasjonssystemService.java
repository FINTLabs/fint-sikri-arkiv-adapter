package no.fint.sikri.data.noark.klasse;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.ClassificationSystemType;
import no.novari.fint.model.resource.arkiv.noark.KlassifikasjonssystemResource;
import no.fint.sikri.service.SikriIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Slf4j
@Service
public class KlassifikasjonssystemService {

    @Autowired
    private KlassifikasjonssystemFactory klassifikasjonssystemFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<KlassifikasjonssystemResource> getKlassifikasjonssystem() {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.CLASSIFICATION_SYSTEM)
                .stream()
                .map(ClassificationSystemType.class::cast)
                .map(klassifikasjonssystemFactory::toFintResource);
    }

}
