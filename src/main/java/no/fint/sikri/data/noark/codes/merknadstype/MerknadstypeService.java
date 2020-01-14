package no.fint.sikri.data.noark.codes.merknadstype;

import no.documaster.model.Result;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.MerknadstypeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class MerknadstypeService {

    @Autowired
    private Noark5WebService noark5WebService;

    public Stream<MerknadstypeResource> getMerknadstype() {
        return noark5WebService.getCodeLists("Merknad", "merknadstype")
                .getResults()
                .stream()
                .map(Result::getValues)
                .flatMap(List::stream)
                .map(BegrepMapper.mapValue(MerknadstypeResource::new));
    }
}
