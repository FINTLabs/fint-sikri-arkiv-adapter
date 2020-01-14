package no.fint.documaster.data.noark.part;

import no.documaster.model.QueryInput;
import no.fint.documaster.data.exception.PartNotFound;
import no.fint.documaster.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.PartResource;
import no.fint.model.resource.administrasjon.arkiv.PartsinformasjonResource;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
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
    private Noark5WebService noark5WebService;

    public PartResource getPartByPartId(String id) throws PartNotFound {
        QueryInput queryInput = partFactory.createQueryInput("id", id);
        return partFactory.toFintResourceList(noark5WebService.query(queryInput))
                .stream().findAny().orElseThrow(() -> new PartNotFound(id));
    }

    public List<PartsinformasjonResource> queryForSaksmappe(SaksmappeResource saksmappe) {
        QueryInput queryInput = partFactory.createQueryInput(saksmappe);
        return partsinformasjonFactory.toFintResourceList(noark5WebService.query(queryInput));
    }
}
