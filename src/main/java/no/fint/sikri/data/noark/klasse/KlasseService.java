package no.fint.sikri.data.noark.klasse;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.ClassType;
import no.fint.model.resource.administrasjon.arkiv.KlasseResource;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.FintUtils.optionalValue;

@Slf4j
@Service
public class KlasseService {

    @Autowired
    private KlasseFactory klasseFactory;

    @Getter
    private List<ClassType> classTypes;

    //@Scheduled(initialDelay = 10000, fixedDelayString = "${fint.kodeverk.refresh-interval:1500000}")
    public void refresh() {
        classTypes = sikriObjectModelService.getDataObjects(SikriObjectTypes.CLASS)
                .stream()
                .map(ClassType.class::cast)
                .collect(Collectors.toList());
    }

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<KlasseResource> getKlasserByEmneId(String id) {
        return classTypes.stream()
                .filter(c -> optionalValue(c.getClassificationSystemId()).map(id::equals).orElse(false))
                .map(klasseFactory::toFintResource);
    }

    public Stream<KlasseResource> getUnderKlasser(String id) {
        return classTypes.stream()
                .filter(c -> optionalValue(c.getParentId()).map(id::equals).orElse(false))
                .map(klasseFactory::toFintResource);
    }

    public Stream<KlasseResource> getKlasser() {
        return classTypes.stream()
                .map(klasseFactory::toFintResource);
    }


}
