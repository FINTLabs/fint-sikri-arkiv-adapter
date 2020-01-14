package no.fint.sikri.data.noark.korrespondansepart;

import no.documaster.model.QueryInput;
import no.fint.sikri.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.KorrespondanseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KorrespondanseService {
    @Autowired
    private KorrespondanseFactory korrespondanseFactory;

    @Autowired
    private KorrespondansepartFactory korrespondansepartFactory;

    @Autowired
    private Noark5WebService noark5WebService;

    public List<KorrespondanseResource> queryForRegistrering(String refRegistrering) {
        QueryInput queryInput = korrespondansepartFactory.createQueryInput("refRegistrering.id", refRegistrering);
        return noark5WebService.query(queryInput)
                .getResults()
                .stream()
                .map(korrespondanseFactory::toFintResource)
                .collect(Collectors.toList());
    }

}
