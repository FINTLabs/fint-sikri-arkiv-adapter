package no.fint.sikri.data.noark.codes.saksmappetype;

import no.fint.arkiv.sikri.oms.FileTypeType;
import no.fint.model.resource.arkiv.kodeverk.SaksmappetypeResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Stream;

@Service
public class SaksmappetypeService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<SaksmappetypeResource> getFileTypeTable() {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.FILE_TYPE, null, 0, Collections.emptyList())
                .stream()
                .map(FileTypeType.class::cast)
                .map(BegrepMapper::mapSaksmappetype);
    }
}
