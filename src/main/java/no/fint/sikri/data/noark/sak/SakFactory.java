package no.fint.sikri.data.noark.sak;


import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.service.SikriIdentityService;
import no.novari.fint.arkiv.CaseProperties;
import no.novari.fint.model.resource.arkiv.noark.SakResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SakFactory {

    @Autowired
    private NoarkFactory noarkFactory;

    @Autowired
    private SikriIdentityService identityService;

    public SakResource toFintResource(CaseType result) {
        return noarkFactory.applyValuesForSaksmappe(
                identityService.getDefaultIdentity(),
                new CaseProperties(), //TODO CaseProperties
                result, new SakResource());
    }

    public CaseType toCaseType(SakResource sakResource) {
        return noarkFactory.toCaseType(new CaseProperties(), sakResource); //TODO CaseProperties
    }
}
