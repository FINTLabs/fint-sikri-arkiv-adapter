package no.fint.sikri.data.noark.sak;

import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.resource.administrasjon.arkiv.SakResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.service.SikriObjectModelService;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SakService {

    @Autowired
    private SakFactory sakFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<SakResource> searchSakByQueryParams(Map<String, Object> query) {

        String filter = String.format("Title=%s", query.get("title"));

        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.CASE,
                filter,
                Integer.parseInt((String) query.getOrDefault("maxResult", "10")),
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION))
                .stream()
                .map(CaseType.class::cast)
                .map(sakFactory::toFintResource)
                .collect(Collectors.toList());
    }

    public SakResource getSakByCaseNumber(String caseNumber) throws IllegalCaseNumberFormat, CaseNotFound {
        String sequenceNumber = NOARKUtils.getCaseSequenceNumber(caseNumber);
        String caseYear = NOARKUtils.getCaseYear(caseNumber);

        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.CASE,
                "SequenceNumber=" + sequenceNumber + " AND CaseYear=" + caseYear,
                0,
                Collections.singletonList(SikriObjectTypes.PRIMARY_CLASSIFICATION))
                .stream()
                .map(CaseType.class::cast)
                .findAny()
                .map(sakFactory::toFintResource)
                .orElseThrow(() -> new CaseNotFound("Could not find case number " + caseNumber));
    }

    public SakResource getSakBySystemId(String systemId) throws CaseNotFound {
        return sikriObjectModelService.getDataObjects(
                SikriObjectTypes.CASE,
                "Id=" + systemId,
                0,
                Arrays.asList(SikriObjectTypes.PRIMARY_CLASSIFICATION))
                .stream()
                .map(CaseType.class::cast)
                .findAny()
                .map(sakFactory::toFintResource)
                .orElseThrow(() -> new CaseNotFound("Could not find case with systemId " + systemId));
    }

}
