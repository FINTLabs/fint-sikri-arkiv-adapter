package no.fint.sikri.data.noark.klasse;

import no.fint.arkiv.sikri.oms.ClassType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.model.arkiv.noark.Klassifikasjonssystem;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.KlasseResource;
import org.springframework.stereotype.Service;

import static no.fint.sikri.data.utilities.SikriUtils.applyParameterFromLink;
import static no.fint.sikri.data.utilities.SikriUtils.optionalValue;

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

    public KlasseResource toFintResource(ClassificationType input) {
        KlasseResource result = new KlasseResource();
        result.setKlasseId(input.getClassId());
        result.setTittel(input.getDescription());
        optionalValue(input.getSortOrder()).map(Integer::parseInt).ifPresent(result::setRekkefolge);
        result.addKlassifikasjonssystem(Link.with(Klassifikasjonssystem.class, "systemid", input.getClassificationSystemId()));
        //TODO input.getAccessCode();
        return result;
    }

    public ClassificationType toClassificationType(KlasseResource input) {
        ClassificationType output = new ClassificationType();
        output.setClassId(input.getKlasseId());
        output.setDescription(input.getTittel());
        output.setSortOrder(String.valueOf(input.getRekkefolge()));
        applyParameterFromLink(input.getKlassifikasjonssystem(), output::setClassificationSystemId);
        return output;
    }
}
