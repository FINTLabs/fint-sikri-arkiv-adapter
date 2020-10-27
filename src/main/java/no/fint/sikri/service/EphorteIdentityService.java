package no.fint.sikri.service;

import no.fint.sikri.SikriIdentities;
import no.fint.sikri.data.exception.InvalidIdentity;
import no.fint.sikri.model.ElementsIdentity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class EphorteIdentityService {
    private final SikriIdentities identities;

    public EphorteIdentityService(SikriIdentities identities) {
        this.identities = identities;
    }

    public ElementsIdentity getIdentityForClass(Class<?> clazz) {
        String caseTypeName = resourceName(clazz);
        final String account = identities.getCasetype().get(caseTypeName);
        if (StringUtils.isBlank(account)) {
            throw new InvalidIdentity("CaseType " + caseTypeName);
        }
        return getElementsIdentity(account);
    }

    public ElementsIdentity getIdentityForCaseType(Object caseType) {
        return getIdentityForClass(caseType.getClass());
    }

    public ElementsIdentity getDefaultIdentity() {
        return getElementsIdentity(identities.getCasetype().get("default"));
    }

    private ElementsIdentity getElementsIdentity(String account) {
        final ElementsIdentity elementsIdentity = identities.getAccount().get(account);
        if (elementsIdentity == null) {
            throw new InvalidIdentity("Account " + account);
        }
        return elementsIdentity;
    }

    private static String resourceName(Class<?> clazz) {
        return StringUtils.removeEnd(StringUtils.lowerCase(clazz.getSimpleName()), "resource");
    }

}
