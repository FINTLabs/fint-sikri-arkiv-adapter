package no.fint.sikri.data.noark.arkivressurs;

import no.fint.arkiv.sikri.oms.UserNameType;
import no.fint.model.administrasjon.personal.Personalressurs;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.ArkivressursResource;
import no.fint.sikri.data.utilities.FintUtils;
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
