package no.fint.sikri.data.noark.codes.skjermingshjemmel;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.arkiv.kodeverk.SkjermingshjemmelResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Slf4j
@Service
public class SkjermingshjemmelService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<SkjermingshjemmelResource> getLawTable() {
        // TODO Does Skjermingshjemmel exist in Elements?
        return Stream.empty();
    }

}
