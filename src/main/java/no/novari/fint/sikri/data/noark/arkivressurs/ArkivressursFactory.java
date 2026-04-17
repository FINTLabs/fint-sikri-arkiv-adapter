package no.novari.fint.sikri.data.noark.arkivressurs;

import no.novari.fint.arkiv.sikri.oms.UserNameType;
import no.novari.fint.model.administrasjon.personal.Personalressurs;
import no.novari.fint.model.resource.Link;
import no.novari.fint.model.resource.arkiv.noark.ArkivressursResource;
import no.novari.fint.sikri.data.utilities.FintUtils;
import org.springframework.stereotype.Service;

@Service
public class ArkivressursFactory {
    public ArkivressursResource toFintResource(UserNameType input) {
        ArkivressursResource output = new ArkivressursResource();
        output.setSystemId(FintUtils.createIdentifikator(input.getId()));
        output.addPersonalressurs(Link.with(Personalressurs.class, "brukernavn", input.getInitials()));
        return output;
    }
}
