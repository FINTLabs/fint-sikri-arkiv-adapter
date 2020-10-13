package no.fint.sikri.data.noark.part;

import no.fint.arkiv.sikri.oms.CasePartyType;
import no.fint.model.resource.arkiv.noark.PartResource;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.repository.KodeverkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartFactory {

    @Autowired
    KodeverkRepository kodeverkRepository;

    public PartResource toFintResource(CasePartyType result) {

        if (result == null) {
            return null;
        }

        PartResource partResource = new PartResource();
        partResource.setAdresse(FintUtils.createAdresse(result));
        partResource.setKontaktinformasjon(FintUtils.createKontaktinformasjon(result));
        partResource.setPartNavn(result.getName().getValue());
        partResource.setPartId(FintUtils.createIdentifikator(result.getId().getValue().toString()));

        return partResource;
    }
}
