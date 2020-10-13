package no.fint.sikri.data.noark.korrespondansepart;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.resource.arkiv.noark.KorrespondansepartResource;
import no.fint.sikri.data.exception.KorrespondansepartNotFound;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KorrespondansepartService {

    @Autowired
    private KorrespondansepartFactory korrespondansepartFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public KorrespondansepartResource getKorrespondansepartBySystemId(String id) throws KorrespondansepartNotFound {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.SENDER_RECIPIENT, "Id=" + id)
                .stream()
                .map(SenderRecipientType.class::cast)
                .map(korrespondansepartFactory::toFintResource)
                .findAny()
                .orElseThrow(() -> new KorrespondansepartNotFound("Sender recipient with id " + id + " not found"));
    }


    public KorrespondansepartResource createKorrespondansepart(KorrespondansepartResource korrespondansepartResource) {
        throw new NotImplementedException("Not implemented");
    }
}
