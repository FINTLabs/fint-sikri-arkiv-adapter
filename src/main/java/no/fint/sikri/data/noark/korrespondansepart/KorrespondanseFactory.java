package no.fint.sikri.data.noark.korrespondansepart;

import no.documaster.model.Result__1;
import no.fint.model.administrasjon.arkiv.Korrespondansepart;
import no.fint.model.administrasjon.arkiv.KorrespondansepartType;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.KorrespondanseResource;
import org.springframework.stereotype.Service;

@Service
public class KorrespondanseFactory {
    public KorrespondanseResource toFintResource(Result__1 result) {
        KorrespondanseResource resource = new KorrespondanseResource();
        resource.addKorrespondansepart(Link.with(Korrespondansepart.class, "systemid", result.getId()));
        resource.addKorrespondanseparttype(Link.with(KorrespondansepartType.class, "systemid", result.getFields().getKorrespondanseparttype()));
        return resource;
    }
}
