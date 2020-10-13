package no.fint.sikri.data.noark.merknad;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.RemarkType;
import no.fint.model.resource.arkiv.noark.MerknadResource;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<MerknadResource> getRemarkForCase(String id) {
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.REMARK,
                "CaseId=" + id + " AND RegistryentryId=@",
                0,
                Collections.emptyList()
        )
                .stream()
                .map(RemarkType.class::cast)
                .map(merknadFactory::toFintResource)
                .collect(Collectors.toList());
    }

    public List<MerknadResource> getRemarkForRegistryEntry(String id) {
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.REMARK,
                "RegistryentryId=" + id,
                0,
                Collections.emptyList()
        )
                .stream()
                .map(RemarkType.class::cast)
                .map(merknadFactory::toFintResource)
                .collect(Collectors.toList());
    }


}
