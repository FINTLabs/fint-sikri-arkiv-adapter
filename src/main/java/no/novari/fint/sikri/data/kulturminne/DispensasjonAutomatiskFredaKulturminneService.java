package no.novari.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.CaseDefaults;
import no.novari.fint.arkiv.sikri.oms.CaseType;
import no.novari.fint.model.resource.arkiv.kulturminnevern.DispensasjonAutomatiskFredaKulturminneResource;
import no.novari.fint.sikri.data.exception.CaseNotFound;
import no.novari.fint.sikri.data.noark.common.NoarkService;
import no.novari.fint.sikri.model.SikriIdentity;
import no.novari.fint.sikri.service.CaseQueryService;
import no.novari.fint.sikri.service.SikriIdentityService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DispensasjonAutomatiskFredaKulturminneService {
    private final NoarkService noarkService;
    private final DispensasjonAutomatiskFredaKulturminneFactory dispensasjonAutomatiskFredaKulturminneFactory;
    private final CaseQueryService caseQueryService;
    private final CaseDefaults caseDefaults;
    private final SikriIdentityService identityService;

    public DispensasjonAutomatiskFredaKulturminneService(
            NoarkService noarkService,
            DispensasjonAutomatiskFredaKulturminneFactory dispensasjonAutomatiskFredaKulturminneFactory,
            CaseQueryService caseQueryService, CaseDefaults caseDefaults, SikriIdentityService identityService) {

        this.noarkService = noarkService;
        this.dispensasjonAutomatiskFredaKulturminneFactory = dispensasjonAutomatiskFredaKulturminneFactory;
        this.caseQueryService = caseQueryService;
        this.caseDefaults = caseDefaults;
        this.identityService = identityService;
    }

    public DispensasjonAutomatiskFredaKulturminneResource updateDispensasjonAutomatiskFredaKulturminneCase(
            String query, DispensasjonAutomatiskFredaKulturminneResource dispensasjonAutomatiskFredaKulturminneResource)
            throws CaseNotFound {

        log.debug("About to update a disp §8.1 case.");
        final SikriIdentity identity = identityService.getIdentityForClass(DispensasjonAutomatiskFredaKulturminneResource.class);
        noarkService.updateCase(identity, caseDefaults.getDispensasjonautomatiskfredakulturminne(), query,
                dispensasjonAutomatiskFredaKulturminneResource);

        return caseQueryService
                .query(identity, query)
                .map(dispensasjonAutomatiskFredaKulturminneFactory::toFintResource)
                .findFirst()
                .orElseThrow(() -> new CaseNotFound("Unable to find updated case for query " + query));
    }

    public DispensasjonAutomatiskFredaKulturminneResource createDispensasjonAutomatiskFredaKulturminneCase(
            DispensasjonAutomatiskFredaKulturminneResource dispensasjonAutomatiskFredaKulturminne) {

        log.debug("About to create a disp §8.1 case.");
        final SikriIdentity identity = identityService.getIdentityForClass(DispensasjonAutomatiskFredaKulturminneResource.class);
        final CaseType caseType = noarkService.createCase(identity,
                dispensasjonAutomatiskFredaKulturminneFactory.toCaseType(dispensasjonAutomatiskFredaKulturminne),
                dispensasjonAutomatiskFredaKulturminne);
        noarkService.createExternalSystemLink(identity, caseType.getId(), dispensasjonAutomatiskFredaKulturminne.getSoknadsnummer());

        return dispensasjonAutomatiskFredaKulturminneFactory.toFintResource(caseType);
    }
}
