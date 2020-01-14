package no.fint.sikri.data.noark.merknad;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.fint.sikri.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.MerknadResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MerknadService {

    @Autowired
    private MerknadFactory merknadFactory;

    @Autowired
    private Noark5WebService noark5WebService;

    public List<MerknadResource> queryForMappe(String id) {
        QueryInput queryInput = merknadFactory.createQueryInput("refMappe.id", id);
        return merknadFactory.toFintResourceList(noark5WebService.query(queryInput)
                .getResults());
    }

    public List<MerknadResource> queryForRegistrering(String id) {
        QueryInput queryInput = merknadFactory.createQueryInput("refRegistrering.id", id);
        return merknadFactory.toFintResourceList(noark5WebService.query(queryInput)
                .getResults());
    }

}
