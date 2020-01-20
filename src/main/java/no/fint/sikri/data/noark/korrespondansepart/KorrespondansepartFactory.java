package no.fint.sikri.data.noark.korrespondansepart;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.SenderRecipientType;
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

    public KorrespondansepartResource toFintResource(SenderRecipientType result) {

        if (result == null) {
            return null;
        }

        KorrespondansepartResource korrespondansepartResource = new KorrespondansepartResource();
        korrespondansepartResource.setAdresse(FintUtils.createAdresse(result));
        korrespondansepartResource.setKontaktinformasjon(FintUtils.createKontaktinformasjon(result));
        korrespondansepartResource.setKorrespondansepartNavn(result.getName().getValue());
        korrespondansepartResource.setSystemId(createIdentifikator(result.getId().toString()));

//        Optional.ofNullable(result.getFields().getFoedselsnummer())
//                .filter(StringUtils::isNotBlank)
//                .map(FintUtils::createIdentifikator)
//                .ifPresent(korrespondansepartResource::setFodselsnummer);

        return korrespondansepartResource;
    }
}
