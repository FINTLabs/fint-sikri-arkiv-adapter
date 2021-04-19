package no.fint.sikri.data.noark.klasse;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.ClassType;
import no.fint.arkiv.sikri.oms.ClassificationSystemType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.model.resource.arkiv.noark.KlasseResource;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.SikriIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.SikriUtils.getLinkTargets;

@Slf4j
@Service
public class KlasseService {

    @Autowired
    private KlasseFactory klasseFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<KlasseResource> getKlasserByEmneId(String id) {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.CLASS, "ClassificationSystemId=" + id)
                .stream().map(ClassType.class::cast)
                .map(klasseFactory::toFintResource);
    }

    public Stream<KlasseResource> getKlasserByCaseId(SikriIdentity identity, Integer id) {
        return sikriObjectModelService.getDataObjects(identity, SikriObjectTypes.CLASSIFICATION, "CaseId=" + id)
                .stream().map(ClassificationType.class::cast)
                .map(klasseFactory::toFintResource);
    }

    public void createClassification(SikriIdentity identity, Integer caseId, KlasseResource resource) {
        final String classificationSystemId = getLinkTargets(resource.getKlassifikasjonssystem())
                .filter(StringUtils::isNotBlank)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        final ClassificationSystemType classificationSystem =
                sikriObjectModelService.getDataObjects(identity,
                        SikriObjectTypes.CLASSIFICATION_SYSTEM, "Id=" + classificationSystemId)
                        .stream()
                        .map(ClassificationSystemType.class::cast)
                        .findFirst()
                        .orElseThrow(IllegalStateException::new);
        final List<ClassType> result = sikriObjectModelService.getDataObjects(identity, SikriObjectTypes.CLASS,
                "ClassificationSystemId=" + classificationSystemId + " and Id=" + resource.getKlasseId())
                .stream().map(ClassType.class::cast).collect(Collectors.toList());
        if (result.isEmpty()) {
            ClassType classType = klasseFactory.toClassType(classificationSystemId, resource);
            classType.setIsSecondaryClassAllowed(classificationSystem.isIsSecondaryClassAllowed());
            classType = sikriObjectModelService.createDataObject(identity, classType);
            sikriObjectModelService.createDataObject(identity, klasseFactory.toNewClassification(caseId, classificationSystemId, resource, classType));
        } else {
            sikriObjectModelService.createDataObject(identity, klasseFactory.toExistingClassification(caseId, classificationSystemId, resource, result));
        }
    }
}
