package no.fint.sikri.data.noark.codes.tilgangsrestriksjon;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.arkiv.TilgangsrestriksjonResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Slf4j
@Service
public class TilgangsrestriksjonService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<TilgangsrestriksjonResource> getAccessCodeTable() {
        return null;
    }

}
