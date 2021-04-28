package no.fint.sikri.data.noark.codes.format;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.FileFormatType;
import no.fint.model.resource.arkiv.kodeverk.FormatResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Slf4j
@Service
public class FormatService {

    private final SikriObjectModelService objectModelService;
    private final SikriIdentityService identityService;

    public FormatService(SikriObjectModelService objectModelService, SikriIdentityService identityService) {
        this.objectModelService = objectModelService;
        this.identityService = identityService;
    }

    public Stream<FormatResource> getFileFormatTable() {
        return objectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.FILE_FORMAT)
                .stream()
                .map(FileFormatType.class::cast)
                .map(BegrepMapper::mapFormat);
    }
}
