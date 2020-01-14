package no.fint.documaster.data.noark.klasse;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.fint.documaster.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.KlasseResource;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KlasseService {

    @Autowired
    private KlasseFactory klasseFactory;

    @Autowired
    private Noark5WebService noark5WebService;

    public KlasseResource getKlasseBySystemId(String id) {
        return queryKlasse("id", id);
    }

    private KlasseResource queryKlasse(String field, String value) {
        QueryInput queryInput = klasseFactory.createQueryInput(field, value);
        return noark5WebService.query(queryInput)
                .getResults()
                .stream()
                .map(klasseFactory::toFintResource)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(value));
    }

    public KlasseResource getKlasseByKlasseIdent(String klasseIdent) {
        return queryKlasse("klasseIdent", klasseIdent);
    }

    public KlasseResource createKlasse(KlasseResource klasseResource) {
        throw new NotImplementedException("createKlasse");
    }

}
