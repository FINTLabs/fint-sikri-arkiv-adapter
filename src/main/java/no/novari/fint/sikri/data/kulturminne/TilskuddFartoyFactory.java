package no.novari.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.CaseDefaults;
import no.novari.fint.arkiv.sikri.oms.CaseType;
import no.novari.fint.model.resource.arkiv.kulturminnevern.TilskuddFartoyResource;
import no.novari.fint.sikri.data.noark.common.NoarkFactory;
import no.novari.fint.sikri.data.noark.common.NoarkService;
import no.novari.fint.sikri.model.SikriIdentity;
import no.novari.fint.sikri.service.SikriIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TilskuddFartoyFactory {

    @Autowired
    private NoarkFactory noarkFactory;

    @Autowired
    private NoarkService noarkService;

    @Autowired
    private SikriIdentityService identityService;

    @Autowired
    private CaseDefaults caseDefaults;

    public CaseType toCaseType(TilskuddFartoyResource tilskuddFartoy) {
        return noarkFactory.toCaseType(caseDefaults.getTilskuddfartoy(), tilskuddFartoy);
    }

    public TilskuddFartoyResource toFintResource(CaseType input) {

        final TilskuddFartoyResource resource = new TilskuddFartoyResource();
        final SikriIdentity identity = identityService.getIdentityForCaseType(resource);

        resource.setSoknadsnummer(noarkService.getIdentifierFromExternalSystemLink(identity, input.getId()));

        return noarkFactory.applyValuesForSaksmappe(identity, caseDefaults.getTilskuddfartoy(), input, resource);
    }

}
