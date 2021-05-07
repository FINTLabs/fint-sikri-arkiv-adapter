package no.fint.sikri.data.noark.merknad;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.RemarkType;
import no.fint.model.resource.arkiv.noark.MerknadResource;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MerknadService {

    @Autowired
    private MerknadFactory merknadFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<MerknadResource> getRemarkForCase(SikriIdentity identity, String id) {
        try {
            return sikriObjectModelService.getDataObjects(
                    identity,
                    SikriObjectTypes.REMARK,
                    "CaseId=" + id + " AND RegistryentryId=@",
                    0,
                    Collections.emptyList()
            )
                    .stream()
                    .map(RemarkType.class::cast)
                    .map(merknadFactory::toFintResource)
                    .collect(Collectors.toList());
        } catch (SOAPFaultException e) {
            log.warn("Fault for getRemarkForCase({})", id ,e);
            return Collections.emptyList();
        }
    }

    public List<MerknadResource> getRemarkForRegistryEntry(SikriIdentity identity, String id) {
        try {
            return sikriObjectModelService.getDataObjects(
                    identity,
                    SikriObjectTypes.REMARK,
                    "RegistryentryId=" + id,
                    0,
                    Collections.emptyList()
            )
                    .stream()
                    .map(RemarkType.class::cast)
                    .map(merknadFactory::toFintResource)
                    .collect(Collectors.toList());
        } catch (SOAPFaultException e) {
            log.warn("Fault for getRemarkForRegistryEntry({})", id, e);
            return Collections.emptyList();
        }
    }


}
