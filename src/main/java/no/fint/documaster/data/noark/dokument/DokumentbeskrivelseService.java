package no.fint.documaster.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.fint.documaster.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.DokumentbeskrivelseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DokumentbeskrivelseService {

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

    @Autowired
    private Noark5WebService noark5WebService;

    public List<DokumentbeskrivelseResource> queryForJournalpost(String id) {
        QueryInput queryInput = dokumentbeskrivelseFactory.createQueryInput(id);
        return noark5WebService.query(queryInput)
                .getResults()
                .stream()
                .map(dokumentbeskrivelseFactory::toFintResource)
                .collect(Collectors.toList());
    }
}
