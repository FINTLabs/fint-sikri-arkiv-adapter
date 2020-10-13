package no.fint.sikri.data.noark.korrespondansepart;

import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.administrasjon.arkiv.Korrespondansepart;
import no.fint.model.administrasjon.arkiv.KorrespondansepartType;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.KorrespondanseResource;
import org.springframework.stereotype.Service;

@Service
public class KorrespondanseFactory {
    public KorrespondanseResource toFintResource(SenderRecipientType result) {
        KorrespondanseResource resource = new KorrespondanseResource();
        resource.addKorrespondansepart(Link.with(Korrespondansepart.class, "systemid", result.getId().toString()));
        if (result.getIsRecipient().getValue()) {
            resource.addKorrespondanseparttype(Link.with(KorrespondansepartType.class, "systemid", "mottaker"));
        } else {
            resource.addKorrespondanseparttype(Link.with(KorrespondansepartType.class, "systemid", "avsender"));
        }

        return resource;
    }
}
