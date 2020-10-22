package no.fint.sikri.data.noark.codes.skjermingshjemmel;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimaps;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.StatutoryAutorityType;
import no.fint.model.resource.arkiv.kodeverk.SkjermingshjemmelResource;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
@Service
public class SkjermingshjemmelService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public Stream<SkjermingshjemmelResource> getStatutoryAuthority() {
        return sikriObjectModelService.getDataObjects(SikriObjectTypes.STATUTORY_AUTORITY)
                .stream()
                .map(StatutoryAutorityType.class::cast)
                .collect(Multimaps.toMultimap(StatutoryAutorityType::getAccessCodeId, StatutoryAutorityType::getPursuant, LinkedListMultimap::create))
                .asMap()
                .entrySet()
                .stream()
                .flatMap(e -> {
                    String code = e.getKey();
                    String[] values = e.getValue().toArray(new String[0]);
                    int prefix = indexOfDifference(values);
                    return Arrays.stream(values)
                            .map(it -> {
                                SkjermingshjemmelResource r = new SkjermingshjemmelResource();
                                final String name = stripAccents(deleteWhitespace(it.substring(0, prefix + 2)));
                                r.setSystemId(FintUtils.createIdentifikator(String.format("%s:%s", code, name)));
                                r.setKode(name);
                                r.setNavn(it);
                                return r;
                            });
                });
    }

}
