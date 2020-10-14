package no.fint.sikri.data.noark.merknad;

import no.fint.arkiv.sikri.oms.RemarkType;
import no.fint.model.administrasjon.arkiv.Arkivressurs;
import no.fint.model.administrasjon.arkiv.Merknadstype;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.MerknadResource;
import org.springframework.stereotype.Service;

@Service
public class MerknadFactory {


    public MerknadResource toFintResource(RemarkType input) {
        MerknadResource result = new MerknadResource();
        result.setMerknadsdato(input.getCreatedDate().toGregorianCalendar().getTime());
        result.setMerknadstekst(input.getText());
        result.addMerknadRegistrertAv(Link.with(Arkivressurs.class, "systemid", input.getCreatedByUserNameId().toString()));
        result.addMerknadstype(Link.with(Merknadstype.class, "systemid", input.getRemarkTypeId()));
        return result;
    }
}
