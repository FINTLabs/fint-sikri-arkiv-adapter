package no.fint.sikri.data.noark.merknad;

import no.documaster.model.QueryInput;
import no.documaster.model.Result__1;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.QueryUtils;
import no.fint.model.administrasjon.arkiv.Arkivressurs;
import no.fint.model.administrasjon.arkiv.Merknadstype;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.MerknadResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MerknadFactory {
    public QueryInput createQueryInput(String field, String value) {
        return QueryUtils.createQueryInput("Merknad", field, value);
    }

    public MerknadResource toFintResource(Result__1 input) {
        MerknadResource result = new MerknadResource();
        result.setMerknadsdato(FintUtils.parseIsoDate(input.getFields().getOpprettetDato()));
        result.setMerknadstekst(input.getFields().getTekst());
        result.addMerknadRegistrertAv(Link.with(Arkivressurs.class, "systemid", input.getFields().getOpprettetAvBrukerIdent()));
        result.addMerknadstype(Link.with(Merknadstype.class, "systemid", input.getFields().getMerknadstype()));
        return result;
    }

    public List<MerknadResource> toFintResourceList(List<Result__1> input) {
        return input.stream()
                .map(this::toFintResource)
                .collect(Collectors.toList());
    }
}
