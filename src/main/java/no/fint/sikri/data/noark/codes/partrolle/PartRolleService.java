package no.fint.sikri.data.noark.codes.partrolle;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.Result;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.PartRolleResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
public class PartRolleService {

    @Autowired
    private Noark5WebService noark5WebService;

    public Stream<PartRolleResource> getPartRolle() {
        return noark5WebService.getCodeLists("type", "field")
                .getResults()
                .stream()
                .map(Result::getValues)
                .flatMap(List::stream)
                .map(BegrepMapper.mapValue(PartRolleResource::new));
    }
}
