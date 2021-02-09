package no.fint.sikri.data.drosjeloyve;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.CaseProperties;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.model.resource.arkiv.samferdsel.SoknadDrosjeloyveResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.exception.DrosjeloyveNotFoundException;
import no.fint.sikri.data.noark.common.NoarkService;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.CaseQueryService;
import no.fint.sikri.service.CaseService;
import no.fint.sikri.service.SikriIdentityService;
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
public class SoknadDrosjeloyveService {

    private final SoknadDrosjeloyveFactory soknadDrosjeloyveFactory;
    private final SikriObjectModelService sikriObjectModelService;
    private final CaseService caseService;
    private final NoarkService noarkService;
    private final CaseQueryService caseQueryService;
    private final CaseProperties caseProperties;
    private final SikriIdentity identity;

    @Value("${fint.case.defaults.drosjeloyve.kKodeFagklasse}")
    String kKodeFagklasse;

    @Value("${fint.case.defaults.drosjeloyve.kKodeTilleggskode}")
    String kKodeTilleggskode;

    @Value("${fint.case.defaults.drosjeloyve.primarklassifikasjon}")
    String primarklassifikasjon;

    public SoknadDrosjeloyveService(
            SoknadDrosjeloyveFactory soknadDrosjeloyveFactory,
            SikriObjectModelService sikriObjectModelService,
            CaseService caseService,
            NoarkService noarkService,
            CaseQueryService caseQueryService,
            CaseDefaults caseDefaults,
            SikriIdentityService identityService) {
        this.soknadDrosjeloyveFactory = soknadDrosjeloyveFactory;
        this.sikriObjectModelService = sikriObjectModelService;
        this.caseService = caseService;
        this.noarkService = noarkService;
        this.caseQueryService = caseQueryService;
        caseProperties = caseDefaults.getSoknaddrosjeloyve();
        identity = identityService.getIdentityForClass(SoknadDrosjeloyveResource.class);
    }

    public SoknadDrosjeloyveResource getDrosjeloyveBySystemId(String id) throws DrosjeloyveNotFoundException {

        checkIfKKodeTilleggskodeIsPresent(id);

        return sikriObjectModelService.getDataObjects(identity,
                SikriObjectTypes.CASE,
                "Id=" + id
                        + " AND Series.Id=" + caseProperties.getArkivdel()
                        + " AND SecondaryClassification.ClassId=\"" + kKodeFagklasse
                        + "\" AND PrimaryClassification.CaseId=" + id
                        + " AND PrimaryClassification.ClassificationSystemId=" + primarklassifikasjon,
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION,
                        SikriObjectTypes.ADMINISTRATIVE_UNIT,
                        SikriObjectTypes.OFFICER_NAME))
                .stream()
                .map(CaseType.class::cast)
                .map(soknadDrosjeloyveFactory::toFintResource)
                .findFirst()
                .orElseThrow(DrosjeloyveNotFoundException::new);
    }

    private void checkIfKKodeTilleggskodeIsPresent(String id) throws DrosjeloyveNotFoundException {
        sikriObjectModelService.getDataObjects(identity,
                SikriObjectTypes.CLASSIFICATION,
                "ClassId=" + kKodeTilleggskode
                        + " AND caseid=" + id)
                .stream()
                .map(ClassificationType.class::cast)
                .findFirst()
                .orElseThrow(DrosjeloyveNotFoundException::new);
    }

    public SoknadDrosjeloyveResource getDrosjeloyveByMappeId(String year, String sequenceNumber) throws DrosjeloyveNotFoundException {

        SoknadDrosjeloyveResource SoknadDrosjeloyveResource = sikriObjectModelService.getDataObjects(identity,
                SikriObjectTypes.CASE,
                "CaseYear=" + year
                        + " AND SequenceNumber=" + sequenceNumber
                        + " AND Series.Id=" + caseProperties.getArkivdel()
                        + " AND SecondaryClassification.ClassId=\"" + kKodeFagklasse
                        + "\" AND PrimaryClassification.ClassificationSystemId=" + primarklassifikasjon,
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION,
                        SikriObjectTypes.ADMINISTRATIVE_UNIT,
                        SikriObjectTypes.OFFICER_NAME))
                .stream()
                .map(CaseType.class::cast)
                .map(soknadDrosjeloyveFactory::toFintResource)
                .findFirst()
                .orElseThrow(DrosjeloyveNotFoundException::new);

        checkIfKKodeTilleggskodeIsPresent(SoknadDrosjeloyveResource.getSystemId().getIdentifikatorverdi());

        return SoknadDrosjeloyveResource;
    }

    public List<SoknadDrosjeloyveResource> getAllDrosjeloyve() {
        return sikriObjectModelService.getDataObjects(identity,
                SikriObjectTypes.CASE,
                "Series.Id=" + caseProperties.getArkivdel()
                        + " AND SecondaryClassification.ClassId=\"" + kKodeFagklasse
                        + "\" AND PrimaryClassification.ClassificationSystem" +
                        "" +
                        "Id=" + primarklassifikasjon,
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION,
                        SikriObjectTypes.ADMINISTRATIVE_UNIT,
                        SikriObjectTypes.OFFICER_NAME))
                .stream()
                .map(CaseType.class::cast)
                .map(soknadDrosjeloyveFactory::toFintResource)
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


    public SoknadDrosjeloyveResource createDrosjeloyve(SoknadDrosjeloyveResource SoknadDrosjeloyveResource) throws CaseNotFound, ClassNotFoundException {
        log.info("Create Drosjeløyve søknad");

        CaseType caseResponse = sikriObjectModelService.createDataObject(identity, soknadDrosjeloyveFactory.toCaseType(SoknadDrosjeloyveResource));
        Integer caseId = caseResponse.getId();

        sikriObjectModelService.createDataObject(identity, soknadDrosjeloyveFactory.createPrimaryClassification(SoknadDrosjeloyveResource, caseId));
        sikriObjectModelService.createDataObject(identity, soknadDrosjeloyveFactory.createFagklasse(caseId));
        sikriObjectModelService.createDataObject(identity, soknadDrosjeloyveFactory.createTilleggsKode(caseId));

        return caseService.getCaseBySystemId(identity, caseId.toString())
                .map(soknadDrosjeloyveFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable get case from Sikri after update"));

    }

    public SoknadDrosjeloyveResource updateDrosjeloyve(String query, SoknadDrosjeloyveResource SoknadDrosjeloyveResource) throws CaseNotFound {
        noarkService.updateCase(identity, query, SoknadDrosjeloyveResource);
        return caseQueryService
                .query(identity, query)
                .map(soknadDrosjeloyveFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable to find updated case for query " + query));
    }
}
