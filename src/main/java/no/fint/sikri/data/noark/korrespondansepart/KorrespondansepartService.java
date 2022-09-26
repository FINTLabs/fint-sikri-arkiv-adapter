package no.fint.sikri.data.noark.korrespondansepart;

import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Stream;

@Service
public class KorrespondansepartService {
    @Autowired
    private KorrespondansepartFactory korrespondansepartFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<SenderRecipientType> queryForRegistrering(SikriIdentity identity, String refRegistrering) {
        return sikriObjectModelService.getDataObjects(identity,
                SikriObjectTypes.SENDER_RECIPIENT, "RegistryEntryId=" + refRegistrering, 0, Collections.emptyList())
                .stream()
                .map(SenderRecipientType.class::cast);
    }

}
