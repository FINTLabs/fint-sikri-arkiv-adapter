package no.fint.sikri.data.drosjeloyve;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.CaseProperties;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ClassType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.model.resource.arkiv.samferdsel.DrosjeloyveResource;
import no.fint.sikri.data.exception.AdministrativeUnitNotFound;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class DrosjeloyveFactory {

    private final NoarkFactory noarkFactory;
    private final SikriObjectModelService sikriObjectModelService;
    private final CaseDefaults caseDefaults;

    @Value("${fint.case.defaults.drosjeloyve.primarklassifikasjon}")
    String primarklassifikasjon;

    @Value("${fint.case.defaults.drosjeloyve.kKodeFagklasse}")
    String kKodeFagklasse;

    @Value("${fint.case.defaults.drosjeloyve.kKodeTilleggskode}")
    String kKodeTilleggskode;

    private CaseProperties properties;


    public DrosjeloyveFactory(NoarkFactory noarkFactory, SikriObjectModelService sikriObjectModelService, CaseDefaults caseDefaults) {
        this.noarkFactory = noarkFactory;
        this.sikriObjectModelService = sikriObjectModelService;
        this.caseDefaults = caseDefaults;
    }


    @PostConstruct
    public void init() {
        properties = caseDefaults.getDrosjeloyve();
    }

    public CaseType toCaseType(DrosjeloyveResource drosjeloyveResource) throws AdministrativeUnitNotFound {


        CaseType caseType = noarkFactory.toCaseType(drosjeloyveResource);

        if (!StringUtils.isAllEmpty(properties.getJournalenhet())) {
            caseType.setRegistryManagementUnitId(properties.getJournalenhet());
        }
        return caseType;


    }

    public ClassificationType createPrimaryClassification(DrosjeloyveResource drosjeloyveResource, Integer caseId) {
        //String organisationName = drosjeloyveResource.getTittel().split("-")[1].trim();

        ClassificationType classificationType = new ClassificationType();
        classificationType.setClassId(drosjeloyveResource.getOrganisasjonsnummer());
        classificationType.setClassificationSystemId(primarklassifikasjon);
        classificationType.setDescription(drosjeloyveResource.getTittel());
        classificationType.setCaseId(caseId);
        classificationType.setIsRestricted(false);
        classificationType.setSortOrder("1");

        return classificationType;
    }

    public ClassificationType createFagklasse(Integer caseId) throws ClassNotFoundException {
        ClassType classType = getClassType(kKodeFagklasse);

        ClassificationType classificationType = new ClassificationType();
        classificationType.setClassId(classType.getId());
        classificationType.setClassificationSystemId(classType.getClassificationSystemId());
        classificationType.setDescription(classType.getDescription());
        classificationType.setCaseId(caseId);
        classificationType.setIsRestricted(false);
        classificationType.setSortOrder("2");

        return classificationType;
    }

    public ClassificationType createTilleggsKode(Integer caseId) throws ClassNotFoundException {
        ClassType classType = getClassType(kKodeTilleggskode);

        ClassificationType classificationType = new ClassificationType();
        classificationType.setClassId(classType.getId());
        classificationType.setClassificationSystemId(classType.getClassificationSystemId());
        classificationType.setDescription(classType.getDescription());
        classificationType.setCaseId(caseId);
        classificationType.setIsRestricted(false);
        classificationType.setSortOrder("3");

        return classificationType;
    }

    private ClassType getClassType(String classId) throws ClassNotFoundException {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.CLASS, "id=" + classId).stream()
                .map(ClassType.class::cast)
                .findAny()
                .orElseThrow(() -> new ClassNotFoundException(classId));
    }

    public DrosjeloyveResource toFintResource(CaseType input) {
        DrosjeloyveResource drosjeloyveResource = new DrosjeloyveResource();
        drosjeloyveResource.setOrganisasjonsnummer(input.getPrimaryClassification().getClassId());
        return noarkFactory.applyValuesForSaksmappe(input, drosjeloyveResource);
    }
}
