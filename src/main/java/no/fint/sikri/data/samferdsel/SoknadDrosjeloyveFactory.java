package no.fint.sikri.data.samferdsel;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.CaseProperties;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.arkiv.samferdsel.SoknadDrosjeloyveResource;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.SikriIdentityService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SoknadDrosjeloyveFactory {

    private final NoarkFactory noarkFactory;
    private final SikriIdentity identity;
    private final CaseProperties properties;


    public SoknadDrosjeloyveFactory(NoarkFactory noarkFactory, CaseDefaults caseDefaults, SikriIdentityService identityService) {
        this.noarkFactory = noarkFactory;
        properties = caseDefaults.getSoknaddrosjeloyve();
        identity = identityService.getIdentityForClass(SoknadDrosjeloyveResource.class);
    }

    public CaseType toCaseType(SoknadDrosjeloyveResource resource) {
        return noarkFactory.toCaseType(properties, resource);
    }

    public SoknadDrosjeloyveResource toFintResource(CaseType input) {
        return noarkFactory.applyValuesForSaksmappe(identity, properties, input, new SoknadDrosjeloyveResource());
    }
}
