package no.fint.sikri.data.noark.korrespondansepart;

import no.fint.model.resource.administrasjon.arkiv.KorrespondanseResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KorrespondanseService {
    @Autowired
    private KorrespondanseFactory korrespondanseFactory;

    @Autowired
    private KorrespondansepartFactory korrespondansepartFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<KorrespondanseResource> queryForRegistrering(String refRegistrering) {
//        QueryInput queryInput = korrespondansepartFactory.createQueryInput("refRegistrering.id", refRegistrering);
//        return noark5WebService.query(queryInput)
//                .getResults()
//                .stream()
//                .map(korrespondanseFactory::toFintResource)
//                .collect(Collectors.toList());
        return null;
    }

}
