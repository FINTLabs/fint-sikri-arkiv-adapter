package no.fint.sikri.data.noark.codes.tilknyttetregistreringsom;

import no.fint.arkiv.sikri.oms.DocumentLinkTypeType;
import no.fint.model.resource.arkiv.kodeverk.TilknyttetRegistreringSomResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.EphorteIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class TilknyttetRegistreringSomService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private EphorteIdentityService identityService;

    public Stream<TilknyttetRegistreringSomResource> getDocumentRelationTable() {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.DOCUMENT_LINK_TYPE)
                .stream()
                .map(DocumentLinkTypeType.class::cast)
                .map(BegrepMapper::mapTilknyttetTegistreringSom);
    }

}
