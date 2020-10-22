package no.fint.sikri.data.noark.korrespondansepart;

import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.resource.arkiv.noark.KorrespondansepartResource;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KorrespondansepartService {
    @Autowired
    private KorrespondansepartFactory korrespondansepartFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<KorrespondansepartResource> queryForRegistrering(String refRegistrering) {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.SENDER_RECIPIENT, "RegistryEntryId=" + refRegistrering, 0, Collections.emptyList())
                .stream()
                .map(SenderRecipientType.class::cast)
                .map(korrespondansepartFactory::toFintResource)
                .collect(Collectors.toList());

    }

}
