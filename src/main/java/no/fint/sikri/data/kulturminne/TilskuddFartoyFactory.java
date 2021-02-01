package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.arkiv.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.data.noark.common.NoarkService;
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
    private CaseDefaults caseDefaults;

    public CaseType toCaseType(TilskuddFartoyResource tilskuddFartoy) {
        return noarkFactory.toCaseType(caseDefaults.getTilskuddfartoy(), tilskuddFartoy);
    }

    public TilskuddFartoyResource toFintResource(CaseType input) {

        final TilskuddFartoyResource resource = new TilskuddFartoyResource();
        resource.setSoknadsnummer(noarkService.getIdentifierFromExternalSystemLink(input.getId()));

        return noarkFactory.applyValuesForSaksmappe(caseDefaults.getTilskuddfartoy(), input, resource);
    }

}
