package no.fint.sikri.data.noark.sak;


import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.arkiv.noark.SakResource;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.service.EphorteIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SakFactory {

    @Autowired
    private NoarkFactory noarkFactory;

    @Autowired
    private EphorteIdentityService identityService;

    public SakResource toFintResource(CaseType result) {
        return noarkFactory.applyValuesForSaksmappe(identityService.getDefaultIdentity(), result, new SakResource());
    }

}
