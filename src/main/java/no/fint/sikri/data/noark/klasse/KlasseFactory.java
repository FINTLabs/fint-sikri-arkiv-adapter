package no.fint.sikri.data.noark.klasse;

import no.fint.arkiv.sikri.oms.ClassType;
import no.fint.model.arkiv.noark.Klassifikasjonssystem;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.KlasseResource;
import org.springframework.stereotype.Service;

@Service
public class KlasseFactory {

    public KlasseResource toFintResource(ClassType input) {
        KlasseResource result = new KlasseResource();
        result.setKlasseId(input.getId());
        result.setTittel(input.getDescription());
        result.addKlassifikasjonssystem(Link.with(Klassifikasjonssystem.class, "systemid", input.getClassificationSystemId()));
        //TODO input.getAccessCode();
        return result;
    }
}
