package no.fint.sikri.data.noark.klasse;

import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.model.arkiv.noark.Klassifikasjonssystem;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.KlasseResource;
import no.fint.sikri.data.utilities.FintUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KlasseFactory {

    public KlasseResource toFintResource(ClassificationType input) {
        KlasseResource result = new KlasseResource();

        // TODO result.setSystemId(FintUtils.createIdentifikator(input.getId()));
        // TODO result.setKlasseId(FintUtils.createIdentifikator(input.getId()));
        //result.setOpprettetAv(input.getFields().getOpprettetAv());
        /* TODO optionalValue(input.getCreatedDate())
                .map(XMLGregorianCalendar::toGregorianCalendar)
                .map(GregorianCalendar::getTime)
                .ifPresent(result::setOpprettetDato); */
        result.setTittel(input.getDescription());
        // TODO result.setBeskrivelse(input.getDescription());

        result.addKlassifikasjonssystem(Link.with(Klassifikasjonssystem.class, "systemid", input.getClassificationSystemId()));

        /* TODO klasseService.getUnderKlasser(input.getId())
                .forEach(c -> result.addUnderklasse(Link.with(Klasse.class, "systemid", c.getSystemId().getIdentifikatorverdi()))); */

        return result;
    }
}
