package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.arkiv.DokumentbeskrivelseResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DokumentbeskrivelseService {

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<DokumentbeskrivelseResource> queryForJournalpost(String id) {
        //QueryInput queryInput = dokumentbeskrivelseFactory.createQueryInput(id);
//        return noark5WebService.query(queryInput)
//                .getResults()
//                .stream()
//                .map(dokumentbeskrivelseFactory::toFintResource)
//                .collect(Collectors.toList());
        return null;
    }
}
