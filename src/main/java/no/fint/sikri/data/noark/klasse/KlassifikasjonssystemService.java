package no.fint.sikri.data.noark.klasse;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.ClassificationSystemType;
import no.fint.model.resource.administrasjon.arkiv.KlassifikasjonssystemResource;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class KlassifikasjonssystemService {

    @Autowired
    private KlassifikasjonssystemFactory klassifikasjonssystemFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<KlassifikasjonssystemResource> getKlassifikasjonssystem() {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CLASSIFICATION_SYSTEM)
                .stream()
                .map(ClassificationSystemType.class::cast)
                .map(klassifikasjonssystemFactory::toFintResource);
    }

}
