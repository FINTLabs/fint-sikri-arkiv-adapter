package no.fint.sikri.data.noark.merknad;

import no.fint.arkiv.sikri.oms.RemarkType;
import no.fint.model.administrasjon.arkiv.Arkivressurs;
import no.fint.model.administrasjon.arkiv.Merknadstype;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.MerknadResource;
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

    /*
    public List<MerknadResource> toFintResourceList(List<Result__1> input) {
        return input.stream()
                .map(this::toFintResource)
                .collect(Collectors.toList());
    }

     */

}
