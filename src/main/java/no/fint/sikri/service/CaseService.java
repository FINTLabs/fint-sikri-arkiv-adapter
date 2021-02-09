package no.fint.sikri.service;

import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ExternalSystemLinkCaseType;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.QueryUtils.getQueryParams;

@Service
public class CaseService {
    private final SikriObjectModelService objectModelService;
    private final ExternalSystemLinkService externalSystemLinkService;
    private final String[] relatedObjects;

    public CaseService(SikriObjectModelService objectModelService, ExternalSystemLinkService externalSystemLinkService) {
        this.objectModelService = objectModelService;
        this.externalSystemLinkService = externalSystemLinkService;
        relatedObjects = new String[] {
                SikriObjectTypes.PRIMARY_CLASSIFICATION,
                SikriObjectTypes.SECONDARY_CLASSIFICATION,
                SikriObjectTypes.ADMINISTRATIVE_UNIT
        };
    }

    public Stream<CaseType> getCaseByCaseNumber(SikriIdentity identity, String caseNumber) throws IllegalCaseNumberFormat {
        String sequenceNumber = NOARKUtils.getCaseSequenceNumber(caseNumber);
        String caseYear = NOARKUtils.getCaseYear(caseNumber);

        return objectModelService.getDataObjects(
                identity,
                SikriObjectTypes.CASE,
                "SequenceNumber=" + sequenceNumber + " AND CaseYear=" + caseYear,
                0,
                relatedObjects)
                .stream()
                .map(CaseType.class::cast);
    }

    public Stream<CaseType> getCaseBySystemId(SikriIdentity identity, String systemId) {
        return objectModelService.getDataObjects(
                identity,
                SikriObjectTypes.CASE,
                "Id=" + systemId,
                0,
                relatedObjects)
                .stream()
                .map(CaseType.class::cast);

    }

    public Stream<CaseType> getCaseByFilter(SikriIdentity identity, String query) {
        final Map<String, Object> queryParams = getQueryParams("?" + query);
        final String filter = String.format("Title=%s", queryParams.get("title"));
        final int maxResult = Integer.parseInt((String) queryParams.getOrDefault("maxResult", "10"));
        return objectModelService.getDataObjects(
                identity,
                SikriObjectTypes.CASE,
                filter,
                maxResult,
                relatedObjects)
                .stream()
                .map(CaseType.class::cast);
    }

    public Stream<CaseType> getCaseByExternalKey(SikriIdentity identity, String externalKey) {
        return objectModelService.getDataObjects(
                identity,
                "ExternalSystemLinkCase",
                "ExternalSystem.ExternalSystemName="
                        + externalSystemLinkService.getExternalSystemName()
                        + " and ExternalKey="
                        + externalKey)
                .stream()
                .map(ExternalSystemLinkCaseType.class::cast)
                .map(ExternalSystemLinkCaseType::getCaseId)
                .filter(i -> i > 0)
                .map(String::valueOf)
                .flatMap(systemId -> getCaseBySystemId(identity, systemId));
    }
}
