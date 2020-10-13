package no.fint.sikri.data.noark.korrespondansepart;

import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.arkiv.noark.Korrespondansepart;
import no.fint.model.arkiv.kodeverk.KorrespondansepartType;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.KorrespondansepartResource;
import org.springframework.stereotype.Service;

@Service
public class KorrespondanseFactory {
    public KorrespondansepartResource toFintResource(SenderRecipientType result) {
        KorrespondansepartResource resource = new KorrespondansepartResource();
        // TODO resource.addKorrespondansepart(Link.with(Korrespondansepart.class, "systemid", result.getId().toString()));
        if (result.getIsRecipient().getValue()) {
            resource.addKorrespondanseparttype(Link.with(KorrespondansepartType.class, "systemid", "mottaker"));
        } else {
            resource.addKorrespondanseparttype(Link.with(KorrespondansepartType.class, "systemid", "avsender"));
        }

        return resource;
    }
}
