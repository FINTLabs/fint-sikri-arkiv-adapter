package no.fint.documaster.data.noark.korrespondansepart;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.fint.documaster.data.exception.KorrespondansepartNotFound;
import no.fint.documaster.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.KorrespondansepartResource;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class KorrespondansepartService {

    @Autowired
    private KorrespondansepartFactory korrespondansepartFactory;

    @Autowired
    private Noark5WebService noark5WebService;

    public KorrespondansepartResource getKorrespondansepartBySystemId(String id) throws KorrespondansepartNotFound {
        return queryKorrespondansepart("id", id);
    }

    private KorrespondansepartResource queryKorrespondansepart(String field, String value) throws KorrespondansepartNotFound {
        QueryInput queryInput = korrespondansepartFactory.createQueryInput(field, value);
        return noark5WebService.query(queryInput)
                .getResults()
                .stream()
                .map(korrespondansepartFactory::toFintResource)
                .findAny()
                .orElseThrow(() -> new KorrespondansepartNotFound(value));
    }

    public KorrespondansepartResource getKorrespondansepartByFodselsnummer(String fodselsnummer) throws KorrespondansepartNotFound {
        return queryKorrespondansepart("foedselsnummer", fodselsnummer);
    }

    public KorrespondansepartResource getKorrespondansepartByOrganisasjonsnummer(String organisasjonsNummer) throws KorrespondansepartNotFound {
        return queryKorrespondansepart("dnummer", organisasjonsNummer);
    }

    public Stream<KorrespondansepartResource> search(Map<String, Object> queryParams) {
        QueryInput queryInput = korrespondansepartFactory.createQueryInput(queryParams);
        return noark5WebService.query(queryInput)
                .getResults()
                .stream()
                .map(korrespondansepartFactory::toFintResource);
    }

    public KorrespondansepartResource createKorrespondansepart(KorrespondansepartResource korrespondansepartResource) {
        throw new NotImplementedException("createKorrespondansepart");
    }

}
