package no.novari.fint.sikri.data.noark.codes.skjermingshjemmel;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimaps;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.StatutoryAutorityType;
import no.novari.fint.model.resource.arkiv.kodeverk.SkjermingshjemmelResource;
import no.novari.fint.sikri.data.utilities.FintUtils;
import no.novari.fint.sikri.service.SikriIdentityService;
import no.novari.fint.sikri.service.SikriObjectModelService;
import no.novari.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
@Service
public class SkjermingshjemmelService {

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    @Autowired
    private SikriIdentityService identityService;

    public Stream<SkjermingshjemmelResource> getStatutoryAuthority() {
        return sikriObjectModelService.getDataObjects(
                identityService.getDefaultIdentity(),
                SikriObjectTypes.STATUTORY_AUTORITY)
                .stream()
                .map(StatutoryAutorityType.class::cast)
                .collect(Multimaps.toMultimap(StatutoryAutorityType::getAccessCodeId, StatutoryAutorityType::getPursuant, LinkedListMultimap::create))
                .asMap()
                .entrySet()
                .stream()
                .flatMap(e -> {
                    String code = e.getKey();
                    if (e.getValue().size() == 1) {
                        SkjermingshjemmelResource r = new SkjermingshjemmelResource();
                        r.setSystemId(FintUtils.createIdentifikator(code));
                        r.setKode(code);
                        e.getValue().forEach(r::setNavn);
                        return Stream.of(r);
                    }
                    String[] names = e.getValue().toArray(new String[0]);
                    String[] keys = Arrays.stream(names).map(this::removeChars).toArray(String[]::new);
                    log.debug("Names: {}", Arrays.toString(keys));
                    int prefix = indexOfDifferences(keys).peek(i -> log.trace("Pos: {}", i)).max().orElse(0);
                    return IntStream.range(0, keys.length)
                            .mapToObj(it -> {
                                SkjermingshjemmelResource r = new SkjermingshjemmelResource();
                                final String name = substring(keys[it], 0, prefix + 1);
                                r.setSystemId(FintUtils.createIdentifikator(String.format("%s:%s", code, name)));
                                r.setKode(name);
                                r.setNavn(names[it]);
                                return r;
                            });
                });
    }

    public String removeChars(String str) {
        return replaceChars(str, " &$+.,/:;=?@#()<>[]{}|\\^%", "");
    }

    public IntStream indexOfDifferences(String... strings) {
        final IntStream.Builder builder = IntStream.builder();
        for (int i = 0; i < strings.length; i++) {
            for (int j = i + 1; j < strings.length; j++) {
                builder.add(indexOfDifference(strings[i], strings[j]));
            }

        }
        return builder.build();
    }

}
