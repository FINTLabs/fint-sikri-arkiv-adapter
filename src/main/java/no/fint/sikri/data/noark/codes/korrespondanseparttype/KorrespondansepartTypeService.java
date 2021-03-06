package no.fint.sikri.data.noark.codes.korrespondanseparttype;

import no.fint.model.resource.arkiv.kodeverk.KorrespondansepartTypeResource;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class KorrespondansepartTypeService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<KorrespondansepartTypeResource> getKorrespondansepartType() {
        // TODO Does KorrespondansepartType exist in Elements?
        return Stream.empty();
    }
}
