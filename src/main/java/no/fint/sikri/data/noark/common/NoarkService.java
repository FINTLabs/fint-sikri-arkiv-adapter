package no.fint.sikri.data.noark.common;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.model.resource.arkiv.noark.SaksmappeResource;
import no.fint.sikri.data.noark.part.PartFactory;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NoarkService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private PartFactory partFactory;

    public void createRelatedObjectsForNewCase(CaseType caseType, SaksmappeResource saksmappeResource) {
        if (saksmappeResource.getPart() != null) {
            sikriObjectModelService.createDataObjects(
                    saksmappeResource
                            .getPart()
                            .stream()
                            .map(part -> partFactory.createCaseParty(caseType.getId(), part))
                            .toArray(DataObject[]::new)
            );
        }

        // TODO if (saksmappeResource.getArkivnotat() != null) {}
        // TODO if (saksmappeResource.getNoekkelord() != null) {}

    }


}
