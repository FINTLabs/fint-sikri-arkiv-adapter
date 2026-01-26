package no.novari.fint.sikri.data.noark.codes.journalstatus;

import no.fint.arkiv.sikri.oms.RecordsStatusType;
import no.novari.fint.model.resource.arkiv.kodeverk.JournalStatusResource;
import no.novari.fint.sikri.data.utilities.BegrepMapper;
import no.novari.fint.sikri.service.SikriIdentityService;
import no.novari.fint.sikri.service.SikriObjectModelService;
import no.novari.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class JournalStatusService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<JournalStatusResource> getJournalStatusTable() {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.RECORDS_STATUS)
                .stream()
                .map(RecordsStatusType.class::cast)
                .map(BegrepMapper::mapJournalStatus);
    }
}
