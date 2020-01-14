package no.fint.sikri.data.noark.codes.tilknyttetregistreringsom;

import no.fint.model.resource.administrasjon.arkiv.TilknyttetRegistreringSomResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class TilknyttetRegistreringSomService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<TilknyttetRegistreringSomResource> getDocumentRelationTable() {
        return null;
    }

}
