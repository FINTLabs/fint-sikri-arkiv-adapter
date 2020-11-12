package no.fint.sikri.service;

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
    private transient ExternalSystemType fintExternalSystem;

    public ExternalSystemLinkService(
            @Value("${fint.sikri.external-system:FINT}") String externalSystemName,
            SikriObjectModelService objectModelService
    ) {
        this.externalSystemName = externalSystemName;
        this.objectModelService = objectModelService;
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
            final List<DataObject> externalSystems = objectModelService.getDataObjects("ExternalSystem", "ExternalSystemName=" + externalSystemName);
            if (externalSystems.isEmpty()) {
                log.info("Creating ExternalSystem {} ...", externalSystemName);
                ExternalSystemType externalSystem = new ExternalSystemType();
                externalSystem.setExternalSystemName(externalSystemName);
                externalSystem.setIsActive(true);
                final ExternalSystemType result = objectModelService.createDataObject(externalSystem);
                log.info("Result: {}", result);
                fintExternalSystem = result;
            } else {
                fintExternalSystem = (ExternalSystemType) externalSystems.get(0);
                log.info("Reusing ExternalSystem {}", externalSystems.get(0));
            }
        }
    }
}
