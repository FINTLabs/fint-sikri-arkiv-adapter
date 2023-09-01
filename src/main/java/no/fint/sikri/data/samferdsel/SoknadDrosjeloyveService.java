package no.fint.sikri.data.samferdsel;

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
    private final NoarkService noarkService;
    private final CaseQueryService caseQueryService;
    private final CaseService caseService;
    private final CaseProperties caseProperties;
    private final SikriIdentityService identityService;

    @Value("${fint.case.defaults.soknaddrosjeloyve.kKodeFagklasse:X}")
    @Deprecated
    String kKodeFagklasse;

    @Value("${fint.case.defaults.soknaddrosjeloyve.kKodeTilleggskode:X}")
    @Deprecated
    String kKodeTilleggskode;

    @Value("${fint.case.defaults.soknaddrosjeloyve.primarklassifikasjon:X}")
    @Deprecated
    String primarklassifikasjon;

    public SoknadDrosjeloyveService(
            SoknadDrosjeloyveFactory soknadDrosjeloyveFactory,
            SikriObjectModelService sikriObjectModelService,
            NoarkService noarkService,
            CaseQueryService caseQueryService,
            CaseService caseService, CaseDefaults caseDefaults,
            SikriIdentityService identityService) {
        this.soknadDrosjeloyveFactory = soknadDrosjeloyveFactory;
        this.sikriObjectModelService = sikriObjectModelService;
        this.noarkService = noarkService;
        this.caseQueryService = caseQueryService;
        this.caseService = caseService;
        caseProperties = caseDefaults.getSoknaddrosjeloyve();
        this.identityService = identityService;
    }

    private SikriIdentity getSoknadDrosjeloyveIdentity() {
        return identityService.getIdentityForClass(SoknadDrosjeloyveResource.class);
    }

    public SoknadDrosjeloyveResource getDrosjeloyveBySystemId(String id) throws DrosjeloyveNotFoundException {

        checkIfKKodeTilleggskodeIsPresent(id);

        return sikriObjectModelService.getDataObjects(getSoknadDrosjeloyveIdentity(),
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
        sikriObjectModelService.getDataObjects(getSoknadDrosjeloyveIdentity(),
                SikriObjectTypes.CLASSIFICATION,
                "ClassId=" + kKodeTilleggskode
                        + " AND caseid=" + id)
                .stream()
                .map(ClassificationType.class::cast)
                .findFirst()
                .orElseThrow(DrosjeloyveNotFoundException::new);
    }

    public SoknadDrosjeloyveResource getDrosjeloyveByMappeId(String year, String sequenceNumber) throws DrosjeloyveNotFoundException {

        SoknadDrosjeloyveResource SoknadDrosjeloyveResource = sikriObjectModelService.getDataObjects(getSoknadDrosjeloyveIdentity(),
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
        return sikriObjectModelService.getDataObjects(getSoknadDrosjeloyveIdentity(),
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


    public SoknadDrosjeloyveResource createDrosjeloyve(SoknadDrosjeloyveResource resource) throws CaseNotFound, ClassNotFoundException {
        log.info("Create Drosjeløyve søknad");

        CaseType caseResponse = noarkService.createCase(
                getSoknadDrosjeloyveIdentity(),
                soknadDrosjeloyveFactory.toCaseType(resource),
                resource);
        Integer caseId = caseResponse.getId();

        return caseService.getCaseBySystemId(getSoknadDrosjeloyveIdentity(), caseId.toString())
                .map(soknadDrosjeloyveFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable get case from Sikri after update"));
    }

    public SoknadDrosjeloyveResource updateDrosjeloyve(String query, SoknadDrosjeloyveResource SoknadDrosjeloyveResource) throws CaseNotFound {
        noarkService.updateCase(getSoknadDrosjeloyveIdentity(), caseProperties, query, SoknadDrosjeloyveResource);
        return caseQueryService
                .query(getSoknadDrosjeloyveIdentity(), query)
                .map(soknadDrosjeloyveFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable to find updated case for query " + query));
    }
}
