package no.fint.sikri.service;

import no.fint.sikri.SikriIdentities;
import no.fint.sikri.data.exception.InvalidIdentity;
import no.fint.sikri.model.SikriIdentity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class EphorteIdentityService {
    private final SikriIdentities identities;

    public EphorteIdentityService(SikriIdentities identities) {
        this.identities = identities;
    }

    public SikriIdentity getIdentityForClass(Class<?> clazz) {
        String caseTypeName = resourceName(clazz);
        final String account = identities.getCasetype().get(caseTypeName);
        if (StringUtils.isBlank(account)) {
            throw new InvalidIdentity("CaseType " + caseTypeName);
        }
        return getElementsIdentity(account);
    }

    public SikriIdentity getIdentityForCaseType(Object caseType) {
        return getIdentityForClass(caseType.getClass());
    }

    public SikriIdentity getDefaultIdentity() {
        return getElementsIdentity(identities.getCasetype().get("default"));
    }

    private SikriIdentity getElementsIdentity(String account) {
        final SikriIdentity sikriIdentity = identities.getAccount().get(account);
        if (sikriIdentity == null) {
            throw new InvalidIdentity("Account " + account);
        }
        return sikriIdentity;
    }

    private static String resourceName(Class<?> clazz) {
        return StringUtils.removeEnd(StringUtils.lowerCase(clazz.getSimpleName()), "resource");
    }

}
