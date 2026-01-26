package no.novari.fint.sikri.data.noark.klasse;

import no.fint.arkiv.sikri.oms.ClassificationSystemType;
import no.novari.fint.model.arkiv.kodeverk.Klassifikasjonstype;
import no.novari.fint.model.resource.Link;
import no.novari.fint.model.resource.arkiv.noark.KlassifikasjonssystemResource;
import no.novari.fint.sikri.data.utilities.FintUtils;
import no.novari.fint.sikri.data.utilities.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static no.novari.fint.sikri.data.utilities.SikriUtils.optionalValue;

@Service
public class KlassifikasjonssystemFactory {

    @Autowired
    private KlasseService klasseService;

    public KlassifikasjonssystemResource toFintResource(ClassificationSystemType input) {
        KlassifikasjonssystemResource result = new KlassifikasjonssystemResource();
        result.setSystemId(FintUtils.createIdentifikator(input.getId()));
        result.setTittel(input.getCaption());
        result.setBeskrivelse(input.getDescription());
        result.setKlasse(klasseService.getKlasserByEmneId(input.getId()).collect(Collectors.toList()));

        optionalValue(input.getClosedByUserNameId()).map(String::valueOf).ifPresent(result::setAvsluttetAv);
        optionalValue(input.getClosedDate()).map(XmlUtils::javaDate).ifPresent(result::setAvsluttetDato);

        optionalValue(input.getCreatedByUserNameId()).map(String::valueOf).ifPresent(result::setOpprettetAv);
        optionalValue(input.getCreatedDate()).map(XmlUtils::javaDate).ifPresent(result::setOpprettetDato);

        result.addKlassifikasjonstype(Link.with(Klassifikasjonstype.class, "systemid", input.getClassificationSystemTypeId()));
        return result;
    }
}
