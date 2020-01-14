package no.fint.sikri.data.noark.codes.variantformat;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.arkiv.VariantformatResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Slf4j
@Service
public class VariantformatService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<VariantformatResource> getVersionFormatTable() {
        return null;
    }

}
