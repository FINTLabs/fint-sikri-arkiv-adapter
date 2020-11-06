package no.fint.sikri.data.drosjeloyve;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.model.resource.arkiv.samferdsel.DrosjeloyveResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.exception.DrosjeloyveNotFoundException;
import no.fint.sikri.data.noark.common.NoarkService;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.CaseService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DrosjeloyveService {

    private final DrosjeloyveFactory drosjeloyveFactory;
    private final SikriObjectModelService sikriObjectModelService;
    private final CaseService caseService;
    private final NoarkService noarkService;
    private final CaseQueryService caseQueryService;
    private final CaseDefaults caseDefaults;

    @Value("${fint.case.defaults.drosjeloyve.kKodeFagklasse}")
    String kKodeFagklasse;

    @Value("${fint.case.defaults.drosjeloyve.kKodeTilleggskode}")
    String kKodeTilleggskode;

    @Value("${fint.case.defaults.drosjeloyve.primarklassifikasjon}")
    String primarklassifikasjon;

    public DrosjeloyveService(DrosjeloyveFactory drosjeloyveFactory, SikriObjectModelService sikriObjectModelService, CaseService caseService, NoarkService noarkService, CaseQueryService caseQueryService, CaseDefaults caseDefaults) {
        this.drosjeloyveFactory = drosjeloyveFactory;
        this.sikriObjectModelService = sikriObjectModelService;
        this.caseService = caseService;
        this.noarkService = noarkService;
        this.caseQueryService = caseQueryService;
        this.caseDefaults = caseDefaults;
    }

    public DrosjeloyveResource getDrosjeloyveBySystemId(String id) throws DrosjeloyveNotFoundException {

        checkIfKKodeTilleggskodeIsPresent(id);

        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CASE,
                "Id=" + id
                        + " AND Series.Id=" + caseDefaults.getDrosjeloyve().getArkivdel()
                        + " AND SecondaryClassification.ClassId=\"" + kKodeFagklasse
                        + "\" AND PrimaryClassification.CaseId=" + id
                        + " AND PrimaryClassification.ClassificationSystemId=" + primarklassifikasjon,
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION,
                        SikriObjectTypes.ADMINISTRATIVE_UNIT,
                        SikriObjectTypes.OFFICER_NAME))
                .stream()
                .map(CaseType.class::cast)
                .map(drosjeloyveFactory::toFintResource)
                .findFirst()
                .orElseThrow(DrosjeloyveNotFoundException::new);
    }

    private void checkIfKKodeTilleggskodeIsPresent(String id) throws DrosjeloyveNotFoundException {
        sikriObjectModelService.getDataObjects(SikriObjectTypes.CLASSIFICATION,
                "ClassId=" + kKodeTilleggskode
                        + " AND caseid=" + id)
                .stream()
                .map(ClassificationType.class::cast)
                .findFirst()
                .orElseThrow(DrosjeloyveNotFoundException::new);
    }

    public DrosjeloyveResource getDrosjeloyveByMappeId(String year, String sequenceNumber) throws DrosjeloyveNotFoundException {

        DrosjeloyveResource drosjeloyveResource = sikriObjectModelService.getDataObjects(SikriObjectTypes.CASE,
                "CaseYear=" + year
                        + " AND SequenceNumber=" + sequenceNumber
                        + " AND Series.Id=" + caseDefaults.getDrosjeloyve().getArkivdel()
                        + " AND SecondaryClassification.ClassId=\"" + kKodeFagklasse
                        + "\" AND PrimaryClassification.ClassificationSystemId=" + primarklassifikasjon,
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION,
                        SikriObjectTypes.ADMINISTRATIVE_UNIT,
                        SikriObjectTypes.OFFICER_NAME))
                .stream()
                .map(CaseType.class::cast)
                .map(drosjeloyveFactory::toFintResource)
                .findFirst()
                .orElseThrow(DrosjeloyveNotFoundException::new);

        checkIfKKodeTilleggskodeIsPresent(drosjeloyveResource.getSystemId().getIdentifikatorverdi());

        return drosjeloyveResource;
    }

    public List<DrosjeloyveResource> getAllDrosjeloyve() {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CASE,
                "Series.Id=" + caseDefaults.getDrosjeloyve().getArkivdel()
                        + " AND SecondaryClassification.ClassId=\"" + kKodeFagklasse
                        + "\" AND PrimaryClassification.ClassificationSystem" +
                        "" +
                        "Id=" + primarklassifikasjon,
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION,
                        SikriObjectTypes.ADMINISTRATIVE_UNIT,
                        SikriObjectTypes.OFFICER_NAME))
                .stream()
                .map(CaseType.class::cast)
                .map(drosjeloyveFactory::toFintResource)
                .map(drosjeloyve -> {
                    try {
                        checkIfKKodeTilleggskodeIsPresent(drosjeloyve.getSystemId().getIdentifikatorverdi());
                        return drosjeloyve;
                    } catch (DrosjeloyveNotFoundException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

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
