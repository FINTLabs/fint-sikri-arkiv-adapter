package no.fint.sikri.data.noark.klasse;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.ClassType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.model.resource.arkiv.noark.KlasseResource;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class KlasseService {

    @Autowired
    private KlasseFactory klasseFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<KlasseResource> getKlasserByEmneId(String id) {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CLASS, "ClassificationSystemId=" + id)
                .stream().map(ClassType.class::cast)
                .map(klasseFactory::toFintResource);
    }

    public Stream<KlasseResource> getKlasserByCaseId(Integer id) {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CLASSIFICATION, "CaseId="+ id)
                .stream().map(ClassificationType.class::cast)
                .map(klasseFactory::toFintResource);
    }

    public void createClassification(Integer caseId, KlasseResource resource) {
        final List<ClassType> result = sikriObjectModelService.getDataObjects(SikriObjectTypes.CLASS, "ClassId=" + resource.getKlasseId())
                .stream().map(ClassType.class::cast).collect(Collectors.toList());
        if (result.isEmpty()) {
            sikriObjectModelService.createDataObject(klasseFactory.toNewClassification(caseId, resource));
        } else {
            sikriObjectModelService.createDataObject(klasseFactory.toExistingClassification(caseId, resource, result));
        }
    }
}
