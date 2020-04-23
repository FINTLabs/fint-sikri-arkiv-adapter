package no.fint.sikri.service;

import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.QueryUtils.getQueryParams;

@Service
public class CaseService {
    private final SikriObjectModelService objectModelService;

    public CaseService(SikriObjectModelService objectModelService) {
        this.objectModelService = objectModelService;
    }

    public Stream<CaseType> getCaseByCaseNumber(String caseNumber) throws IllegalCaseNumberFormat {
        String sequenceNumber = NOARKUtils.getCaseSequenceNumber(caseNumber);
        String caseYear = NOARKUtils.getCaseYear(caseNumber);

        return objectModelService.getDataObjects(
                SikriObjectTypes.CASE,
                "SequenceNumber=" + sequenceNumber + " AND CaseYear=" + caseYear,
                0,
                SikriObjectTypes.PRIMARY_CLASSIFICATION, SikriObjectTypes.ADMINISTRATIVE_UNIT)
                .stream()
                .map(CaseType.class::cast);
    }

    public Stream<CaseType> getCaseBySystemId(String systemId) {
        return objectModelService.getDataObjects(
                SikriObjectTypes.CASE,
                "Id=" + systemId,
                0,
                SikriObjectTypes.PRIMARY_CLASSIFICATION)
                .stream()
                .map(CaseType.class::cast);

    }

    public Stream<CaseType> getCaseByFilter(String query) {
        final Map<String, Object> queryParams = getQueryParams("?" + query);
        final String filter = String.format("Title=%s", queryParams.get("title"));
        final int maxResult = Integer.parseInt((String) queryParams.getOrDefault("maxResult", "10"));
        return objectModelService.getDataObjects(
                SikriObjectTypes.CASE,
                filter,
                maxResult,
                SikriObjectTypes.PRIMARY_CLASSIFICATION)
                .stream()
                .map(CaseType.class::cast);
    }
}
