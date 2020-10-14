package no.fint.sikri.data.noark.korrespondansepart;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.SenderRecipientType;
import no.fint.model.resource.administrasjon.arkiv.KorrespondansepartResource;
import no.fint.sikri.data.utilities.FintUtils;
import org.springframework.stereotype.Service;

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
        korrespondansepartResource.setKorrespondansepartNavn(result.getName());
        korrespondansepartResource.setSystemId(createIdentifikator(result.getId().toString()));

//        Optional.ofNullable(result.getFields().getFoedselsnummer())
//                .filter(StringUtils::isNotBlank)
//                .map(FintUtils::createIdentifikator)
//                .ifPresent(korrespondansepartResource::setFodselsnummer);

        return korrespondansepartResource;
    }
}
