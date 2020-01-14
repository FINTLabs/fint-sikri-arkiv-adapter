package no.fint.documaster.data.noark.klasse;

import no.documaster.model.QueryInput;
import no.documaster.model.Result__1;
import no.fint.documaster.data.utilities.FintUtils;
import no.fint.documaster.data.utilities.QueryUtils;
import no.fint.model.resource.administrasjon.arkiv.KlassifikasjonssystemResource;
import org.springframework.stereotype.Service;

@Service
public class KlassifikasjonssystemFactory {
    public QueryInput createQueryInput(String field, String value) {
        return QueryUtils.createQueryInput("Klassifikasjonssystem", field, value);
    }

    public KlassifikasjonssystemResource toFintResource(Result__1 input) {
        KlassifikasjonssystemResource result = new KlassifikasjonssystemResource();
        result.setSystemId(FintUtils.createIdentifikator(input.getId()));
        result.setTittel(input.getFields().getTittel());
        result.setBeskrivelse(input.getFields().getBeskrivelse());
        return result;
    }
}
