package no.novari.fint.sikri.data.noark.merknad;

import no.novari.fint.arkiv.sikri.oms.RemarkType;
import no.novari.fint.model.arkiv.kodeverk.Merknadstype;
import no.novari.fint.model.arkiv.noark.Arkivressurs;
import no.novari.fint.model.resource.Link;
import no.novari.fint.model.resource.arkiv.noark.MerknadResource;
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
