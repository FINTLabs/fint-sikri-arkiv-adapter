package no.fint.documaster.data.noark.codes.journalstatus;

import no.documaster.model.Result;
import no.fint.documaster.data.utilities.BegrepMapper;
import no.fint.documaster.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.JournalStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class JournalStatusService {

    @Autowired
    private Noark5WebService noark5WebService;

    public Stream<JournalStatusResource> getJournalStatusTable() {
        return noark5WebService.getCodeLists("Journalpost", "journalstatus")
                .getResults()
                .stream()
                .map(Result::getValues)
                .flatMap(List::stream)
                .map(BegrepMapper.mapValue(JournalStatusResource::new));
    }
}
