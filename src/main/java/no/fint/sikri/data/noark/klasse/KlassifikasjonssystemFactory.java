package no.fint.sikri.data.noark.klasse;

import no.fint.arkiv.sikri.oms.ClassificationSystemType;
import no.fint.model.arkiv.noark.Klasse;
import no.fint.model.arkiv.noark.Klassifikasjonssystem;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.KlassifikasjonssystemResource;
import no.fint.sikri.data.utilities.FintUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KlassifikasjonssystemFactory {

    @Autowired
    private KlasseService klasseService;

    public KlassifikasjonssystemResource toFintResource(ClassificationSystemType input) {
        KlassifikasjonssystemResource result = new KlassifikasjonssystemResource();
        result.setSystemId(FintUtils.createIdentifikator(input.getId().getValue()));
        result.setTittel(input.getCaption().getValue());
        result.setBeskrivelse(input.getDescription().getValue());

        /* TODO klasseService.getKlasserByEmneId(input.getId().getValue())
                .forEach(c -> result.addKlasse(Link.with(Klasse.class, "systemid", c.getKlasseId().getIdentifikatorverdi()))); */
        return result;
    }
}
