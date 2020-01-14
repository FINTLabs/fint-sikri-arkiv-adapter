package no.fint.sikri.data.noark.klassifikasjonssystem;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.fint.sikri.data.noark.klasse.KlassifikasjonssystemFactory;
import no.fint.sikri.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.KlassifikasjonssystemResource;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KlassifikasjonssystemService {

    @Autowired
    private KlassifikasjonssystemFactory klassifikasjonssystemFactory;

    @Autowired
    private Noark5WebService noark5WebService;

    public KlassifikasjonssystemResource getKlassifikasjonssystemBySystemId(String id) {
        return queryKlassifikasjonssystem("id", id);
    }

    private KlassifikasjonssystemResource queryKlassifikasjonssystem(String field, String value) {
        QueryInput queryInput = klassifikasjonssystemFactory.createQueryInput(field, value);
        return noark5WebService.query(queryInput)
                .getResults()
                .stream()
                .map(klassifikasjonssystemFactory::toFintResource)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(value));
    }

    public KlassifikasjonssystemResource getKlassifikasjonssystemByKlassifikasjonssystemIdent(String klassifikasjonssystemIdent) {
        return queryKlassifikasjonssystem("klassifikasjonssystemIdent", klassifikasjonssystemIdent);
    }

    public KlassifikasjonssystemResource createKlassifikasjonssystem(KlassifikasjonssystemResource klassifikasjonssystemResource) {
        throw new NotImplementedException("createKlassifikasjonssystem");
    }

}
