package no.fint.sikri.data.noark.codes.variantformat;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.VariantFormatType;
import no.fint.model.resource.arkiv.kodeverk.VariantformatResource;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Slf4j
@Service
public class VariantformatService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<VariantformatResource> getVariantFormatTable() {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.VARIANT_FORMAT)
                .stream()
                .map(VariantFormatType.class::cast)
                .map(BegrepMapper::mapVariantFormat);
    }

}
