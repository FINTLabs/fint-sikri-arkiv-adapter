package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.CaseDefaults;
import no.fint.arkiv.sikri.oms.CaseType;
import no.novari.fint.model.resource.arkiv.kulturminnevern.DispensasjonAutomatiskFredaKulturminneResource;
import no.novari.fint.model.resource.felles.kompleksedatatyper.MatrikkelnummerResource;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.data.noark.common.NoarkService;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.SikriIdentityService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DispensasjonAutomatiskFredaKulturminneFactory {
    private final NoarkFactory noarkFactory;
    private final NoarkService noarkService;
    private final SikriIdentityService identityService;
    private final CaseDefaults caseDefaults;

    public DispensasjonAutomatiskFredaKulturminneFactory(
            NoarkFactory noarkFactory, NoarkService noarkService,
            SikriIdentityService identityService, CaseDefaults caseDefaults) {

        this.noarkFactory = noarkFactory;
        this.noarkService = noarkService;
        this.identityService = identityService;
        this.caseDefaults = caseDefaults;
    }

    public DispensasjonAutomatiskFredaKulturminneResource toFintResource(CaseType caseType) {
        final DispensasjonAutomatiskFredaKulturminneResource resource = new DispensasjonAutomatiskFredaKulturminneResource();
        final SikriIdentity identity = identityService.getIdentityForCaseType(resource);

        resource.setSoknadsnummer(noarkService.getIdentifierFromExternalSystemLink(identity, caseType.getId()));
        resource.setMatrikkelnummer(new MatrikkelnummerResource());

        return noarkFactory.applyValuesForSaksmappe(
                identity,
                caseDefaults.getDispensasjonautomatiskfredakulturminne(),
                caseType, resource);
    }

    public CaseType toCaseType(DispensasjonAutomatiskFredaKulturminneResource dispensasjonAutomatiskFredaKulturminneResource) {
        return noarkFactory.toCaseType(caseDefaults.getDispensasjonautomatiskfredakulturminne(), dispensasjonAutomatiskFredaKulturminneResource);
    }
}
