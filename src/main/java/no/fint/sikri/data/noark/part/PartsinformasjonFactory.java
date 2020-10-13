package no.fint.sikri.data.noark.part;

import no.fint.arkiv.sikri.oms.CasePartyType;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.PartResource;
import no.fint.model.resource.arkiv.noark.PartRolleResource;
import no.fint.model.resource.arkiv.noark.PartsinformasjonResource;
import org.springframework.stereotype.Service;

@Service
public class PartsinformasjonFactory {

    public PartsinformasjonResource toFintResource(CasePartyType result) {

        if (result == null) {
            return null;
        }

        PartsinformasjonResource resource = new PartsinformasjonResource();
        resource.addPart(Link.with(PartResource.class, "partid", result.getId().getValue().toString()));
        resource.addPartRolle(Link.with(PartRolleResource.class, "systemid", result.getCasePartyRoleId().getValue()));
        return resource;
    }
    /*

    public List<PartsinformasjonResource> toFintResourceList(QueryResult result) {
        List<PartsinformasjonResource> output = new ArrayList<>(result.getResults().size());
        for (Result__1 item : result.getResults()) {
            output.add(toFintResource(item));
        }
        return output;
    }
*/
}
