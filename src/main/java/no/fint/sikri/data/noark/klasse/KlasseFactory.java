package no.fint.sikri.data.noark.klasse;

import no.fint.arkiv.sikri.oms.ClassType;
import no.fint.arkiv.sikri.oms.ClassificationType;
import no.fint.model.arkiv.noark.Klassifikasjonssystem;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.KlasseResource;
import no.fint.sikri.service.EphorteIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static no.fint.sikri.data.utilities.SikriUtils.applyParameterFromLink;
import static no.fint.sikri.data.utilities.SikriUtils.optionalValue;

@Service
public class KlasseFactory {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private EphorteIdentityService identityService;

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
        output.setSortOrder(String.valueOf(input.getRekkefolge()));
        /* TODO if (StringUtils.isNotBlank(input.getTittel())) {
            output.setRemark(input.getTittel());
        }
        */
        // TODO Possible to detect which classes reject description modifications?
        sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.CLASS, "Id=" + input.getKlasseId())
                .stream()
                .map(ClassType.class::cast)
                .map(ClassType::getDescription)
                .forEach(output::setDescription);

        applyParameterFromLink(input.getKlassifikasjonssystem(), output::setClassificationSystemId);
        return output;
    }
}
