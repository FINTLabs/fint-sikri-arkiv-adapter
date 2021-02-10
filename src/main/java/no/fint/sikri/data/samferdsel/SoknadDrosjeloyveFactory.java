package no.fint.sikri.data.samferdsel;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.CaseProperties;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.arkiv.samferdsel.SoknadDrosjeloyveResource;
import no.fint.sikri.data.exception.AdministrativeUnitNotFound;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.SikriIdentityService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SoknadDrosjeloyveFactory {

    private final NoarkFactory noarkFactory;
    private final SikriIdentity identity;
    private final CaseProperties properties;

    /*
    @Value("${fint.case.defaults.soknaddrosjeloyve.primarklassifikasjon}")
    private String primarklassifikasjon;

    @Value("${fint.case.defaults.soknaddrosjeloyve.kKodeFagklasse}")
    private String kKodeFagklasse;

    @Value("${fint.case.defaults.soknaddrosjeloyve.kKodeTilleggskode}")
    private String kKodeTilleggskode;


     */


    public SoknadDrosjeloyveFactory(NoarkFactory noarkFactory, CaseDefaults caseDefaults, SikriIdentityService identityService) {
        this.noarkFactory = noarkFactory;
        properties = caseDefaults.getSoknaddrosjeloyve();
        identity = identityService.getIdentityForClass(SoknadDrosjeloyveResource.class);
    }


    public CaseType toCaseType(SoknadDrosjeloyveResource resource) throws AdministrativeUnitNotFound {
        CaseType caseType = noarkFactory.toCaseType(properties, resource);

        if (!StringUtils.isAllEmpty(properties.getJournalenhet())) {
            caseType.setRegistryManagementUnitId(properties.getJournalenhet());
        }
        return caseType;


    }

    /*
    public ClassificationType createPrimaryClassification(SoknadDrosjeloyveResource resource, Integer caseId) {
        //String organisationName = resource.getTittel().split("-")[1].trim();

        ClassificationType classificationType = new ClassificationType();
        classificationType.setClassId(resource.getOrganisasjonsnummer());
        classificationType.setClassificationSystemId(primarklassifikasjon);
        classificationType.setDescription(resource.getTittel());
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
        return sikriObjectModelService.getDataObjects(identity, SikriObjectTypes.CLASS, "id=" + classId).stream()
                .map(ClassType.class::cast)
                .findAny()
                .orElseThrow(() -> new ClassNotFoundException(classId));
    }


     */
    public SoknadDrosjeloyveResource toFintResource(CaseType input) {
        SoknadDrosjeloyveResource resource = new SoknadDrosjeloyveResource();
        resource.setOrganisasjonsnummer(input.getPrimaryClassification().getClassId());
        return noarkFactory.applyValuesForSaksmappe(identity, properties, input, resource);
    }
}
