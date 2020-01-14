package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.fint.sikri.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.DokumentobjektResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DokumentobjektService {

    @Autowired
    private Noark5WebService noark5WebService;

    @Autowired
    private DokumentobjektFactory dokumentobjektFactory;

    public List<DokumentobjektResource> queryDokumentobjekt(String id) {
        QueryInput queryInput = dokumentobjektFactory.createQueryInput(id);
        return noark5WebService.query(queryInput)
                .getResults()
                .stream()
                .map(dokumentobjektFactory::toFintResource)
                .collect(Collectors.toList());
    }
}
