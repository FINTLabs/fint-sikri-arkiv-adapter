package no.fint.sikri.data.noark.korrespondansepart;

import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.resource.arkiv.noark.KorrespondanseResource;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KorrespondanseService {
    @Autowired
    private KorrespondanseFactory korrespondanseFactory;

    @Autowired
    private KorrespondansepartFactory korrespondansepartFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<KorrespondanseResource> queryForRegistrering(String refRegistrering) {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.SENDER_RECIPIENT, "RegistryEntryId=" + refRegistrering, 0, Collections.emptyList())
                .stream()
                .map(SenderRecipientType.class::cast)
                .map(korrespondanseFactory::toFintResource)
                .collect(Collectors.toList());

    }

}
