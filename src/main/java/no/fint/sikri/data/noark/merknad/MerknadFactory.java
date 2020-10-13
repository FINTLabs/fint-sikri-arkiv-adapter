package no.fint.sikri.data.noark.merknad;

import no.fint.arkiv.sikri.oms.RemarkType;
import no.fint.model.arkiv.noark.Arkivressurs;
import no.fint.model.arkiv.noark.Merknadstype;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.MerknadResource;
import no.fint.sikri.data.utilities.FintUtils;
import org.springframework.stereotype.Service;

@Service
public class MerknadFactory {


    public MerknadResource toFintResource(RemarkType input) {
        MerknadResource result = new MerknadResource();
        result.setMerknadsdato(input.getCreatedDate().getValue().toGregorianCalendar().getTime());
        result.setMerknadstekst(input.getText().getValue());
        result.addMerknadRegistrertAv(Link.with(Arkivressurs.class, "systemid", input.getCreatedByUserNameId().getValue().toString()));
        result.addMerknadstype(Link.with(Merknadstype.class, "systemid", input.getRemarkTypeId().getValue()));
        return result;
    }
}
