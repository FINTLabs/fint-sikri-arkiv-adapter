package no.fint.sikri.data.noark.nokkelord;

import lombok.extern.slf4j.Slf4j;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NokkelordService {

    @Autowired
    private NokkelordFactory nokkelordFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<String> queryForMappe(String id) {
        //QueryInput queryInput = nokkelordFactory.createQueryInput("refMappe.id", id);
        return null; //nokkelordFactory.toFintResourceList(noark5WebService.query(queryInput)
        //.getResults());
    }

    public List<String> queryForRegistrering(String id) {
        //QueryInput queryInput = nokkelordFactory.createQueryInput("refRegistrering.id", id);
        return null; //nokkelordFactory.toFintResourceList(noark5WebService.query(queryInput)
        // .getResults());
    }

}
