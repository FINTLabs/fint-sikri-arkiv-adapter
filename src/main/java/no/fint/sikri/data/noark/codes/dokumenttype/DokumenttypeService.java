package no.fint.sikri.data.noark.codes.dokumenttype;

import no.documaster.model.Result;
import no.fint.sikri.data.utilities.BegrepMapper;
import no.fint.sikri.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.DokumentTypeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class DokumenttypeService {

    @Autowired
    private Noark5WebService noark5WebService;

    public Stream<DokumentTypeResource> getDocumenttypeTable() {
        return noark5WebService.getCodeLists("Dokument", "dokumenttype")
                .getResults()
                .stream()
                .map(Result::getValues)
                .flatMap(List::stream)
                .map(BegrepMapper.mapValue(DokumentTypeResource::new));
    }
}
