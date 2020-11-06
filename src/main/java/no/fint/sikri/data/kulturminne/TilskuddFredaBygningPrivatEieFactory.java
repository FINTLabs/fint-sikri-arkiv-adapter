package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.arkiv.kulturminnevern.TilskuddFredaBygningPrivatEieResource;
import no.fint.model.resource.felles.kompleksedatatyper.MatrikkelnummerResource;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.service.SikriIdentityService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TilskuddFredaBygningPrivatEieFactory {
    private final NoarkFactory noarkFactory;
    private final SikriIdentityService identityService;

    public TilskuddFredaBygningPrivatEieFactory(NoarkFactory noarkFactory, SikriIdentityService identityService) {
        this.noarkFactory = noarkFactory;
        this.identityService = identityService;
    }

    public TilskuddFredaBygningPrivatEieResource toFintResource(CaseType caseType) {
        final TilskuddFredaBygningPrivatEieResource resource = new TilskuddFredaBygningPrivatEieResource();
        resource.setSoknadsnummer(new Identifikator());
        resource.setMatrikkelnummer(new MatrikkelnummerResource());

        return noarkFactory.applyValuesForSaksmappe(identityService.getIdentityForCaseType(resource), caseType, resource);
    }

    public CaseType toCaseType(TilskuddFredaBygningPrivatEieResource tilskuddFredaBygningPrivatEieResource) {
        return noarkFactory.toCaseType(tilskuddFredaBygningPrivatEieResource);
    }
}
