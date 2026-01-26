package no.novari.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.CaseDefaults;
import no.fint.arkiv.sikri.oms.CaseType;
import no.novari.fint.model.resource.arkiv.kulturminnevern.TilskuddFredaBygningPrivatEieResource;
import no.novari.fint.model.resource.felles.kompleksedatatyper.MatrikkelnummerResource;
import no.novari.fint.sikri.data.noark.common.NoarkFactory;
import no.novari.fint.sikri.data.noark.common.NoarkService;
import no.novari.fint.sikri.model.SikriIdentity;
import no.novari.fint.sikri.service.SikriIdentityService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TilskuddFredaBygningPrivatEieFactory {
    private final NoarkFactory noarkFactory;
    private final NoarkService noarkService;
    private final SikriIdentityService identityService;
    private final CaseDefaults caseDefaults;

    public TilskuddFredaBygningPrivatEieFactory(NoarkFactory noarkFactory, NoarkService noarkService, SikriIdentityService identityService, CaseDefaults caseDefaults) {
        this.noarkFactory = noarkFactory;
        this.noarkService = noarkService;
        this.identityService = identityService;
        this.caseDefaults = caseDefaults;
    }

    public TilskuddFredaBygningPrivatEieResource toFintResource(CaseType caseType) {
        final TilskuddFredaBygningPrivatEieResource resource = new TilskuddFredaBygningPrivatEieResource();
        final SikriIdentity identity = identityService.getIdentityForCaseType(resource);

        resource.setSoknadsnummer(noarkService.getIdentifierFromExternalSystemLink(identity, caseType.getId()));
        resource.setMatrikkelnummer(new MatrikkelnummerResource());

        return noarkFactory.applyValuesForSaksmappe(
                identity,
                caseDefaults.getTilskuddfredabygningprivateie(),
                caseType, resource);
    }

    public CaseType toCaseType(TilskuddFredaBygningPrivatEieResource tilskuddFredaBygningPrivatEieResource) {
        return noarkFactory.toCaseType(caseDefaults.getTilskuddfredabygningprivateie(), tilskuddFredaBygningPrivatEieResource);
    }
}
