package no.novari.fint.sikri.data.noark.codes.merknadstype;

import no.fint.arkiv.sikri.oms.InformationTypeType;
import no.novari.fint.model.resource.arkiv.kodeverk.MerknadstypeResource;
import no.novari.fint.sikri.data.utilities.BegrepMapper;
import no.novari.fint.sikri.service.SikriIdentityService;
import no.novari.fint.sikri.service.SikriObjectModelService;
import no.novari.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class MerknadstypeService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<MerknadstypeResource> getMerknadstype() {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.INFORMATION_TYPE)
                .stream()
                .map(InformationTypeType.class::cast)
                .map(BegrepMapper::mapMerkandstype);
    }
}
