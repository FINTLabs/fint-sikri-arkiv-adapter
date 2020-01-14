package no.fint.documaster.data.noark.nokkelord;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.fint.documaster.service.Noark5WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NokkelordService {

    @Autowired
    private NokkelordFactory nokkelordFactory;

    @Autowired
    private Noark5WebService noark5WebService;

    public List<String> queryForMappe(String id) {
        QueryInput queryInput = nokkelordFactory.createQueryInput("refMappe.id", id);
        return nokkelordFactory.toFintResourceList(noark5WebService.query(queryInput)
                .getResults());
    }

    public List<String> queryForRegistrering(String id) {
        QueryInput queryInput = nokkelordFactory.createQueryInput("refRegistrering.id", id);
        return nokkelordFactory.toFintResourceList(noark5WebService.query(queryInput)
                .getResults());
    }

}
