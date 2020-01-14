package no.fint.sikri.data.noark.merknad;

import org.springframework.stereotype.Service;

@Service
public class MerknadFactory {
    /*
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
     */
}
