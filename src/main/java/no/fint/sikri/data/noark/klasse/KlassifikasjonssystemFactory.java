package no.fint.sikri.data.noark.klasse;

import no.fint.arkiv.sikri.oms.ClassificationSystemType;
import no.fint.model.administrasjon.arkiv.Klasse;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.KlassifikasjonssystemResource;
import no.fint.sikri.data.utilities.FintUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KlassifikasjonssystemFactory {

    @Autowired
    private KlasseService klasseService;

    public KlassifikasjonssystemResource toFintResource(ClassificationSystemType input) {
        KlassifikasjonssystemResource result = new KlassifikasjonssystemResource();
        result.setSystemId(FintUtils.createIdentifikator(input.getId()));
        result.setTittel(input.getCaption());
        result.setBeskrivelse(input.getDescription());

        klasseService.getKlasserByEmneId(input.getId())
                .forEach(c -> result.addKlasse(Link.with(Klasse.class, "systemid", c.getKlasseId().getIdentifikatorverdi())));
        return result;
    }
}
