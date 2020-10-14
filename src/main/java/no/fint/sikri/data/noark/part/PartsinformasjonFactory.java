package no.fint.sikri.data.noark.part;

import no.fint.arkiv.sikri.oms.CasePartyType;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.kodeverk.PartRolleResource;
import no.fint.model.resource.arkiv.noark.PartResource;
import org.springframework.stereotype.Service;

@Service
public class PartsinformasjonFactory {

    public PartResource toFintResource(CasePartyType result) {

        if (result == null) {
            return null;
        }

        PartResource resource = new PartResource();
        // TODO resource.addPart(Link.with(PartResource.class, "partid", result.getId().toString()));
        resource.addPartRolle(Link.with(PartRolleResource.class, "systemid", result.getCasePartyRoleId()));
        return resource;
    }
    /*

    public List<PartResource> toFintResourceList(QueryResult result) {
        List<PartResource> output = new ArrayList<>(result.getResults().size());
        for (Result__1 item : result.getResults()) {
            output.add(toFintResource(item));
        }
        return output;
    }
*/
}
