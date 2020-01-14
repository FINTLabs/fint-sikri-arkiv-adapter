package no.fint.sikri.data.noark.klasse;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.arkiv.KlassifikasjonssystemResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KlassifikasjonssystemService {

    @Autowired
    private KlassifikasjonssystemFactory klassifikasjonssystemFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public KlassifikasjonssystemResource getKlassifikasjonssystemBySystemId(String id) {
        return null; //queryKlassifikasjonssystem("id", id);
    }

//    private KlassifikasjonssystemResource queryKlassifikasjonssystem(String field, String value) {
//        QueryInput queryInput = klassifikasjonssystemFactory.createQueryInput(field, value);
//        return noark5WebService.query(queryInput)
//                .getResults()
//                .stream()
//                .map(klassifikasjonssystemFactory::toFintResource)
//                .findAny()
//                .orElseThrow(() -> new IllegalArgumentException(value));
//    }

//    public KlassifikasjonssystemResource getKlassifikasjonssystemByKlassifikasjonssystemIdent(String klassifikasjonssystemIdent) {
//        return queryKlassifikasjonssystem("klassifikasjonssystemIdent", klassifikasjonssystemIdent);
//    }

    public KlassifikasjonssystemResource createKlassifikasjonssystem(KlassifikasjonssystemResource klassifikasjonssystemResource) {
        throw new NotImplementedException("createKlassifikasjonssystem");
    }

}
