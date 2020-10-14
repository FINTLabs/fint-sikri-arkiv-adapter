package no.fint.sikri.data.noark.klasse;

import no.fint.arkiv.sikri.oms.ClassificationSystemType;
import no.fint.model.resource.arkiv.noark.KlassifikasjonssystemResource;
import no.fint.sikri.data.utilities.FintUtils;
import org.springframework.stereotype.Service;

@Service
public class KlassifikasjonssystemFactory {

    public KlassifikasjonssystemResource toFintResource(ClassificationSystemType input) {
        KlassifikasjonssystemResource result = new KlassifikasjonssystemResource();
        result.setSystemId(FintUtils.createIdentifikator(input.getId()));
        result.setTittel(input.getCaption());
        result.setBeskrivelse(input.getDescription());

        /* TODO klasseService.getKlasserByEmneId(input.getId())
                .forEach(c -> result.addKlasse(Link.with(Klasse.class, "systemid", c.getKlasseId().getIdentifikatorverdi()))); */
        return result;
    }
}
