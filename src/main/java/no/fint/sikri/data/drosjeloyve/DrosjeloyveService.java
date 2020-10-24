package no.fint.sikri.data.drosjeloyve;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.arkiv.samferdsel.DrosjeloyveResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.noark.common.NoarkService;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.CaseService;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DrosjeloyveService {

    private final DrosjeloyveFactory drosjeloyveFactory;
    private final SikriObjectModelService sikriObjectModelService;
    private final CaseService caseService;
    private final NoarkService noarkService;
    private final CaseQueryService caseQueryService;


    public DrosjeloyveService(DrosjeloyveFactory drosjeloyveFactory, SikriObjectModelService sikriObjectModelService, CaseService caseService, NoarkService noarkService, CaseQueryService caseQueryService) {
        this.drosjeloyveFactory = drosjeloyveFactory;
        this.sikriObjectModelService = sikriObjectModelService;
        this.caseService = caseService;
        this.noarkService = noarkService;
        this.caseQueryService = caseQueryService;
    }

    public DrosjeloyveResource createDrosjeloyve(DrosjeloyveResource drosjeloyveResource) throws CaseNotFound, ClassNotFoundException {
        log.info("Create Drosjeløyve søknad");

        CaseType caseResponse = sikriObjectModelService.createDataObject(drosjeloyveFactory.toCaseType(drosjeloyveResource));
        Integer caseId = caseResponse.getId();

        sikriObjectModelService.createDataObject(drosjeloyveFactory.createPrimaryClassification(drosjeloyveResource, caseId));
        sikriObjectModelService.createDataObject(drosjeloyveFactory.createFagklasse(caseId));
        sikriObjectModelService.createDataObject(drosjeloyveFactory.createTilleggsKode(caseId));

        return caseService.getCaseBySystemId(caseId.toString())
                .map(drosjeloyveFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable get case from Sikri after update"));

    }

    public DrosjeloyveResource updateDrosjeloyve(String query, DrosjeloyveResource drosjeloyveResource) throws CaseNotFound {
        noarkService.updateCase(query, drosjeloyveResource);
        return caseQueryService
                .query(query)
                .map(drosjeloyveFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable to find updated case for query " + query));
    }
}
