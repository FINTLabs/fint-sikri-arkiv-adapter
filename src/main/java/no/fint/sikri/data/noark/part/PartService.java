package no.fint.sikri.data.noark.part;

import no.fint.arkiv.sikri.oms.CasePartyType;
import no.fint.model.resource.administrasjon.arkiv.PartResource;
import no.fint.model.resource.administrasjon.arkiv.PartsinformasjonResource;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import no.fint.sikri.data.exception.PartNotFound;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.CASE_PARTY,
                "CaseId=" + saksmappe.getSystemId().getIdentifikatorverdi()
        ).stream()
                .map(CasePartyType.class::cast)
                .map(partsinformasjonFactory::toFintResource)
                .collect(Collectors.toList());
    }
}
