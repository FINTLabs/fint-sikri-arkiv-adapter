package no.fint.sikri.data.noark.korrespondansepart;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.documaster.model.Result__1;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.QueryUtils;
import no.fint.model.resource.administrasjon.arkiv.KorrespondansepartResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static no.fint.sikri.data.utilities.FintUtils.createIdentifikator;

@Service
@Slf4j
public class KorrespondansepartFactory {


    public KorrespondansepartResource toFintResource(Result__1 result) {

        if (result == null) {
            return null;
        }

        KorrespondansepartResource korrespondansepartResource = new KorrespondansepartResource();
        korrespondansepartResource.setAdresse(FintUtils.createAdresse(result.getFields()));
        korrespondansepartResource.setKontaktinformasjon(FintUtils.createKontaktinformasjon(result.getFields()));
        korrespondansepartResource.setKorrespondansepartNavn(result.getFields().getKorrespondansepartNavn());
        korrespondansepartResource.setSystemId(createIdentifikator(result.getId()));

        Optional.ofNullable(result.getFields().getFoedselsnummer())
                .filter(StringUtils::isNotBlank)
                .map(FintUtils::createIdentifikator)
                .ifPresent(korrespondansepartResource::setFodselsnummer);

        return korrespondansepartResource;
    }

    public QueryInput createQueryInput(String field, String value) {
        return QueryUtils.createQueryInput("Korrespondansepart", field, value);
    }

    public QueryInput createQueryInput(Map<String, Object> queryParams) {
        return QueryUtils.createQueryInput("Korrespondansepart", queryParams);
    }
}
