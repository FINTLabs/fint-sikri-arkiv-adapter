package no.fint.sikri.data.noark.part;

import no.fint.model.resource.administrasjon.arkiv.PartResource;
import no.fint.model.resource.administrasjon.arkiv.PartsinformasjonResource;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import no.fint.sikri.data.exception.PartNotFound;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartService {

    @Autowired
    private PartFactory partFactory;

    @Autowired
    private PartsinformasjonFactory partsinformasjonFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public PartResource getPartByPartId(String id) throws PartNotFound {
        //QueryInput queryInput = partFactory.createQueryInput("id", id);
        return null; //partFactory.toFintResourceList(noark5WebService.query(queryInput))
        // .stream().findAny().orElseThrow(() -> new PartNotFound(id));
    }

    public List<PartsinformasjonResource> queryForSaksmappe(SaksmappeResource saksmappe) {
        //QueryInput queryInput = partFactory.createQueryInput(saksmappe);
        return null;
        //partsinformasjonFactory.toFintResourceList(noark5WebService.query(queryInput));
    }
}
