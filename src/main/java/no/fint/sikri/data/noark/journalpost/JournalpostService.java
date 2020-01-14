package no.fint.sikri.data.noark.journalpost;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.arkiv.JournalpostResource;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class JournalpostService {

    @Autowired
    private JournalpostFactory journalpostFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<JournalpostResource> queryForSaksmappe(SaksmappeResource saksmappe) {
        return null;
    }
}
