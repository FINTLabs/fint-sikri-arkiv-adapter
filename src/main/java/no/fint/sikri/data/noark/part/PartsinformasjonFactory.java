package no.fint.sikri.data.noark.part;

import no.documaster.model.QueryResult;
import no.documaster.model.Result__1;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.PartResource;
import no.fint.model.resource.administrasjon.arkiv.PartRolleResource;
import no.fint.model.resource.administrasjon.arkiv.PartsinformasjonResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PartsinformasjonFactory {

    public PartsinformasjonResource toFintResource(Result__1 result) {

        if (result == null) {
            return null;
        }

        PartsinformasjonResource resource = new PartsinformasjonResource();
        resource.addPart(Link.with(PartResource.class, "partid", result.getId()));
        resource.addPartRolle(Link.with(PartRolleResource.class, "systemid", result.getFields().getSakspartRolle()));
        return resource;
    }

    public List<PartsinformasjonResource> toFintResourceList(QueryResult result) {
        List<PartsinformasjonResource> output = new ArrayList<>(result.getResults().size());
        for (Result__1 item : result.getResults()) {
            output.add(toFintResource(item));
        }
        return output;
    }

}
