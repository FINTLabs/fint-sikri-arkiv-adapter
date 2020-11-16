package no.fint.sikri.data.noark.arkivressurs;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.UserNameType;
import no.fint.arkiv.sikri.oms.UserRoleType;
import no.fint.model.arkiv.noark.Tilgang;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.ArkivressursResource;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ArkivressursService {
    private final SikriObjectModelService sikriObjectModelService;
    private final ArkivressursFactory factory;

    public ArkivressursService(SikriObjectModelService sikriObjectModelService, ArkivressursFactory factory) {
        this.sikriObjectModelService = sikriObjectModelService;
        this.factory = factory;
    }

    public Stream<ArkivressursResource> getArkivressurser() {
        final Map<Integer, ArkivressursResource> userMap = sikriObjectModelService.getDataObjects(SikriObjectTypes.USER_NAME, "IsCurrent=true")
                .stream()
                .map(UserNameType.class::cast)
                .peek(u -> log.debug("{} = {}", u.getUserId(), u.getInitials()))
                .collect(Collectors.toMap(UserNameType::getUserId, factory::toFintResource));
        sikriObjectModelService.getDataObjects(SikriObjectTypes.USER_ROLE)
                .stream()
                .map(UserRoleType.class::cast)
                .forEach(role -> userMap.compute(role.getUserId(), (k,v)-> {
                    if (v==null) return null;
                    v.addTilgang(Link.with(Tilgang.class, "systemid", String.valueOf(role.getId())));
                    return v;
                }));
        return userMap.values().stream();
    }
}
