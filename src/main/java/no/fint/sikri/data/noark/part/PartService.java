package no.fint.sikri.data.noark.part;

import no.fint.arkiv.sikri.oms.CasePartyType;
import no.novari.fint.model.resource.arkiv.noark.PartResource;
import no.novari.fint.model.resource.arkiv.noark.SaksmappeResource;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartService {

    @Autowired
    private PartFactory partFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<PartResource> queryForSaksmappe(SikriIdentity identity, SaksmappeResource saksmappe) {
        return sikriObjectModelService.getDataObjects(
                identity,
                SikriObjectTypes.CASE_PARTY,
                "CaseId=" + saksmappe.getSystemId().getIdentifikatorverdi()
        ).stream()
                .map(CasePartyType.class::cast)
                .map(partFactory::toFintResource)
                .collect(Collectors.toList());
    }
}
