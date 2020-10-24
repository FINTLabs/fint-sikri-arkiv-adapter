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
import no.fint.sikri.service.SikriCaseDefaultsService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class DrosjeloyveFactory {

    private final NoarkFactory noarkFactory;
    private final SikriCaseDefaultsService caseDefaultsService;
    private final SikriObjectModelService sikriObjectModelService;
    private final CaseDefaults caseDefaults;

    private CaseProperties properties;

    public DrosjeloyveFactory(NoarkFactory noarkFactory, SikriCaseDefaultsService caseDefaultsService, SikriObjectModelService sikriObjectModelService, CaseDefaults caseDefaults) {
        this.noarkFactory = noarkFactory;
        this.caseDefaultsService = caseDefaultsService;
        this.sikriObjectModelService = sikriObjectModelService;
        this.caseDefaults = caseDefaults;
    }


    @PostConstruct
    public void init() {
        properties = caseDefaults.getDrosjeloyve();
    }

    public CaseType toCaseType(DrosjeloyveResource drosjeloyveResource) throws AdministrativeUnitNotFound {

        return noarkFactory.toCaseType(drosjeloyveResource);


    }

    public ClassificationType createPrimaryClassification(DrosjeloyveResource drosjeloyveResource, Integer caseId) {
        //String organisationName = drosjeloyveResource.getTittel().split("-")[1].trim();

        ClassificationType classificationType = new ClassificationType();
        classificationType.setClassId(drosjeloyveResource.getOrganisasjonsnummer());
        classificationType.setClassificationSystemId("LØYVE");
        classificationType.setDescription(drosjeloyveResource.getTittel());
        classificationType.setCaseId(caseId);
        classificationType.setIsRestricted(false);
        classificationType.setSortOrder("1");

        return classificationType;
    }

    public ClassificationType createFagklasse(Integer caseId) throws ClassNotFoundException {
        ClassType classType = getClassType("N12");

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
        ClassType classType = getClassType("&18");

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
