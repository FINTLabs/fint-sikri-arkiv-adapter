package no.fint.sikri.data.noark.common;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.model.resource.arkiv.noark.SaksmappeResource;
import no.fint.sikri.data.noark.klasse.KlasseFactory;
import no.fint.sikri.data.noark.part.PartFactory;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
@Slf4j
public class NoarkService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private PartFactory partFactory;

    @Autowired
    private KlasseFactory klasseFactory;

    public CaseType createCase(CaseType input, SaksmappeResource resource) {
        CaseType result = sikriObjectModelService.createDataObject(input);
        createRelatedObjectsForNewCase(result, resource);
        return result;
    }

    private void createRelatedObjectsForNewCase(CaseType caseType, SaksmappeResource resource) {
        if (resource.getPart() != null) {
            sikriObjectModelService.createDataObjects(
                    resource
                            .getPart()
                            .stream()
                            .map(part -> partFactory.createCaseParty(caseType.getId(), part))
                            .toArray(DataObject[]::new)
            );
        }

        if (resource.getKlasse() != null) {
            sikriObjectModelService.createDataObjects(
            resource.getKlasse()
                    .stream()
                    .map(klasseFactory::toClassificationType)
                    .sorted(Comparator.comparing(ClassificationType::getSortOrder))
                    .peek(cls -> cls.setCaseId(caseType.getId()))
                    .toArray(DataObject[]::new)
            );
        }

        // TODO if (resource.getArkivnotat() != null) {}
        // TODO if (resource.getNoekkelord() != null) {}

    }


}
