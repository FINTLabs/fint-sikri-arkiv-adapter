package no.fint.sikri.data.noark.codes.journalstatus;

import no.fint.model.resource.administrasjon.arkiv.JournalStatusResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class JournalStatusService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<JournalStatusResource> getJournalStatusTable() {
        return null;
    }
}
