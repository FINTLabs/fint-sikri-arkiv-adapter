package no.fint.sikri.data.noark.merknad;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.arkiv.MerknadResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MerknadService {

    @Autowired
    private MerknadFactory merknadFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<MerknadResource> queryForMappe(String id) {
        //QueryInput queryInput = merknadFactory.createQueryInput("refMappe.id", id);
        return null; //merknadFactory.toFintResourceList(noark5WebService.query(queryInput)
        //.getResults());
    }

    public List<MerknadResource> queryForRegistrering(String id) {
        //QueryInput queryInput = merknadFactory.createQueryInput("refRegistrering.id", id);
        return null;//merknadFactory.toFintResourceList(noark5WebService.query(queryInput)
        //.getResults());
    }

}
