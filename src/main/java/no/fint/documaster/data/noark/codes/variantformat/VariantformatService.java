package no.fint.documaster.data.noark.codes.variantformat;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.Result;
import no.fint.documaster.data.utilities.BegrepMapper;
import no.fint.documaster.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.VariantformatResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class VariantformatService {

    @Autowired
    private Noark5WebService noark5WebService;

    public Stream<VariantformatResource> getVersionFormatTable() {
        return noark5WebService.getCodeLists("type", "field")
                .getResults()
                .stream()
                .map(Result::getValues)
                .flatMap(List::stream)
                .map(BegrepMapper.mapValue(VariantformatResource::new));
    }

}
