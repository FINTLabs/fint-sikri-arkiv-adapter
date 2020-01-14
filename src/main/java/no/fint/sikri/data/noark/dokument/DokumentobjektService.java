package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.administrasjon.arkiv.DokumentobjektResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DokumentobjektService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private DokumentobjektFactory dokumentobjektFactory;

    public List<DokumentobjektResource> queryDokumentobjekt(String id) {
//        QueryInput queryInput = dokumentobjektFactory.createQueryInput(id);
//        return noark5WebService.query(queryInput)
//                .getResults()
//                .stream()
//                .map(dokumentobjektFactory::toFintResource)
//                .collect(Collectors.toList());
        return null;
    }
}
