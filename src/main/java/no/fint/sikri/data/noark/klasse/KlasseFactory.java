package no.fint.sikri.data.noark.klasse;

import no.fint.arkiv.sikri.oms.ClassType;
import no.fint.model.administrasjon.arkiv.Klasse;
import no.fint.model.administrasjon.arkiv.Klassifikasjonssystem;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.KlasseResource;
import no.fint.sikri.data.utilities.FintUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

import static no.fint.sikri.data.utilities.SikriUtils.optionalValue;

@Service
public class KlasseFactory {

    @Autowired
    private KlasseService klasseService;

    public KlasseResource toFintResource(ClassType input) {
        KlasseResource result = new KlasseResource();

        result.setSystemId(FintUtils.createIdentifikator(input.getId().getValue()));
        result.setKlasseId(FintUtils.createIdentifikator(input.getId().getValue()));
        //result.setOpprettetAv(input.getFields().getOpprettetAv());
        optionalValue(input.getCreatedDate())
                .map(XMLGregorianCalendar::toGregorianCalendar)
                .map(GregorianCalendar::getTime)
                .ifPresent(result::setOpprettetDato);
        result.setTittel(input.getDescription().getValue());
        result.setBeskrivelse(input.getDescription().getValue());

        result.addKlassifikasjonssystem(Link.with(Klassifikasjonssystem.class, "systemid", input.getClassificationSystemId().getValue()));

        klasseService.getUnderKlasser(input.getId().getValue())
                .forEach(c -> result.addUnderklasse(Link.with(Klasse.class, "systemid", c.getSystemId().getIdentifikatorverdi())));

        return result;
    }
}
