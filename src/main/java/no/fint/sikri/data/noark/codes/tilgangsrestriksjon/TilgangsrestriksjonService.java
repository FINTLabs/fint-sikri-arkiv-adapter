package no.fint.sikri.data.noark.codes.tilgangsrestriksjon;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.Result;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.TilgangsrestriksjonResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class TilgangsrestriksjonService {

    @Autowired
    private Noark5WebService noark5WebService;

    public Stream<TilgangsrestriksjonResource> getAccessCodeTable() {
        return noark5WebService.getCodeLists("type", "field")
                .getResults()
                .stream()
                .map(Result::getValues)
                .flatMap(List::stream)
                .map(BegrepMapper.mapValue(TilgangsrestriksjonResource::new));
    }

}
