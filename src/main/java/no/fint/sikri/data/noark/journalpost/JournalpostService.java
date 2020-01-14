package no.fint.sikri.data.noark.journalpost;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.fint.sikri.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.JournalpostResource;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JournalpostService {

    @Autowired
    private JournalpostFactory journalpostFactory;

    @Autowired
    private Noark5WebService noark5WebService;

    public List<JournalpostResource> queryForSaksmappe(SaksmappeResource saksmappe) {
        QueryInput queryInput = journalpostFactory.createQueryInput(saksmappe);
        return noark5WebService.query(queryInput)
                .getResults()
                .stream()
                .map(journalpostFactory::toFintResource)
                .collect(Collectors.toList());
    }
}
