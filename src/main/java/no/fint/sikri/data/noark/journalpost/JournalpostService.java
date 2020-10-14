package no.fint.sikri.data.noark.journalpost;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.RegistryEntryType;
import no.fint.model.resource.administrasjon.arkiv.JournalpostResource;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
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
    private SikriObjectModelService sikriObjectModelService;

    public List<JournalpostResource> queryForSaksmappe(SaksmappeResource saksmappe) {
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.REGISTRY_ENTRY,
                "CaseId=" + saksmappe.getSystemId().getIdentifikatorverdi(),
                0,
                SikriObjectTypes.OFFICER_NAME)
                .stream()
                .map(RegistryEntryType.class::cast)
                .map(journalpostFactory::toFintResource)
                .collect(Collectors.toList());
    }
}
