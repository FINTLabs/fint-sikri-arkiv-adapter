package no.novari.fint.sikri.service;

import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.DataObject;
import no.fint.arkiv.sikri.oms.ExternalSystemType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ExternalSystemLinkService {

    @Getter
    private final String externalSystemName;
    private final SikriObjectModelService objectModelService;
    private final SikriIdentityService identityService;
    private transient ExternalSystemType fintExternalSystem;

    public ExternalSystemLinkService(
            @Value("${fint.sikri.external-system:FINT}") String externalSystemName,
            SikriObjectModelService objectModelService,
            SikriIdentityService identityService) {
        this.externalSystemName = externalSystemName;
        this.objectModelService = objectModelService;
        this.identityService = identityService;
    }

    public Integer getExternalSystemLinkId() {
        if (fintExternalSystem == null) {
            updateExternalSystem();
        }
        return fintExternalSystem.getId();
    }

    @Synchronized
    private void updateExternalSystem() {
        if (fintExternalSystem == null) {
            final List<DataObject> externalSystems = objectModelService.getDataObjects(identityService.getDefaultIdentity(), "ExternalSystem", "ExternalSystemName=" + externalSystemName);
            if (externalSystems.isEmpty()) {
                log.info("Creating ExternalSystem {} ...", externalSystemName);
                ExternalSystemType externalSystem = new ExternalSystemType();
                externalSystem.setExternalSystemName(externalSystemName);
                externalSystem.setIsActive(true);
                final ExternalSystemType result = objectModelService.createDataObject(identityService.getDefaultIdentity(), externalSystem);
                log.info("Result: {}", result);
                fintExternalSystem = result;
            } else {
                fintExternalSystem = (ExternalSystemType) externalSystems.get(0);
                log.info("Reusing ExternalSystem {}", externalSystems.get(0));
            }
        }
    }
}
