package no.fint.documaster.data.noark.klasse;

import no.documaster.model.QueryInput;
import no.documaster.model.Result__1;
import no.fint.documaster.data.utilities.FintUtils;
import no.fint.documaster.data.utilities.QueryUtils;
import no.fint.model.administrasjon.arkiv.Klassifikasjonssystem;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.KlasseResource;
import org.springframework.stereotype.Service;

@Service
public class KlasseFactory {
    public QueryInput createQueryInput(String field, String value) {
        return QueryUtils.createQueryInput("Klasse", field, value);
    }

    public KlasseResource toFintResource(Result__1 input) {
        KlasseResource result = new KlasseResource();
        result.setSystemId(FintUtils.createIdentifikator(input.getId()));
        result.setKlasseId(FintUtils.createIdentifikator(input.getFields().getKlasseIdent()));
        result.setOpprettetAv(input.getFields().getOpprettetAv());
        result.setOpprettetDato(FintUtils.parseIsoDate(input.getFields().getOpprettetDato()));
        result.setTittel(input.getFields().getTittel());
        result.setBeskrivelse(input.getFields().getBeskrivelse());

        result.addKlassifikasjonssystem(Link.with(Klassifikasjonssystem.class, "systemid", String.valueOf(input.getLinks().getRefKlassifikasjonssystem())));
        return result;
    }
}
