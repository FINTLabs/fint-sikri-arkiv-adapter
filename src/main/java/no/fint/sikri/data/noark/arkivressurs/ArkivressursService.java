package no.fint.sikri.data.noark.arkivressurs;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.UserNameType;
import no.fint.arkiv.sikri.oms.UserRoleType;
import no.fint.model.arkiv.noark.Tilgang;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.ArkivressursResource;
import no.fint.sikri.service.SikriIdentityService;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ArkivressursService {
    private final SikriObjectModelService sikriObjectModelService;
    private final ArkivressursFactory factory;
    private final SikriIdentityService identityService;
    private final Map<String, Integer> userIdInitialsMap = new ConcurrentHashMap<>();

    public ArkivressursService(SikriObjectModelService sikriObjectModelService, ArkivressursFactory factory, SikriIdentityService identityService) {
        this.sikriObjectModelService = sikriObjectModelService;
        this.factory = factory;
        this.identityService = identityService;
    }

    public Stream<ArkivressursResource> getArkivressurser() {
        final Map<Integer, ArkivressursResource> userMap = sikriObjectModelService.getDataObjects(identityService.getDefaultIdentity(), SikriObjectTypes.USER_NAME, "IsCurrent=true")
                .stream()
                .map(UserNameType.class::cast)
                .peek(u -> {
                    if (StringUtils.isNotBlank(u.getInitials())) {
                        userIdInitialsMap.merge(u.getInitials(), u.getId(), Integer::max);
                    }
                    log.debug("{} = {}", u.getId(), u.getInitials());
                })
                .collect(Collectors.toMap(UserNameType::getUserId, factory::toFintResource, (a,b) -> b));
        log.debug("User ID Map contains {} entries", userIdInitialsMap.size());
        sikriObjectModelService.getDataObjects(identityService.getDefaultIdentity(), SikriObjectTypes.USER_ROLE)
                .stream()
                .map(UserRoleType.class::cast)
                .forEach(role -> userMap.compute(role.getUserId(), (k,v)-> {
                    if (v==null) return null;
                    v.addTilgang(Link.with(Tilgang.class, "systemid", String.valueOf(role.getId())));
                    return v;
                }));
        return userMap.values().stream();
    }

    public Integer lookupUserId(String input) {
        log.trace("Lookup initials {}", input);
        if (StringUtils.isNumeric(input)) {
            return Integer.valueOf(input);
        }
        final Integer id = userIdInitialsMap.getOrDefault(input, 0);
        log.debug("Lookup initials {} = {}", input, id);
        return id;
    }
}
