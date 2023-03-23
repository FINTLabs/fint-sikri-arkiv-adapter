package no.fint.sikri.service;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import no.fint.antlr.FintFilterService;
import no.fint.antlr.ODataLexer;
import no.fint.antlr.ODataParser;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ExternalSystemLinkCaseType;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.data.exception.IllegalOdataFilterProperty;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.model.SikriIdentity;
import no.fint.sikri.utilities.SikriObjectTypes;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.QueryUtils.getQueryParams;

@Service
@Slf4j
public class CaseService {
    private final SikriObjectModelService objectModelService;
    private final ExternalSystemLinkService externalSystemLinkService;
    private final String[] relatedObjects;
    private final ImmutableMap<String, String> odataFilterFieldMapper;

    public CaseService(SikriObjectModelService objectModelService, ExternalSystemLinkService externalSystemLinkService, FintFilterService oDataFilterService) {
        this.objectModelService = objectModelService;
        this.externalSystemLinkService = externalSystemLinkService;

        relatedObjects = new String[] {
                SikriObjectTypes.PRIMARY_CLASSIFICATION,
                SikriObjectTypes.SECONDARY_CLASSIFICATION,
                SikriObjectTypes.ADMINISTRATIVE_UNIT
        };

        odataFilterFieldMapper = new ImmutableMap.Builder<String, String>()
                .put("saksaar", "CaseYear")
                .put("sakssekvensnummer", "SequenceNumber")
                .build();

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

    @Deprecated
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

    public Stream<CaseType> getCaseByODataFilter(SikriIdentity identity, String query) throws IllegalOdataFilterProperty {
        log.info("The Query, proudly present to you by the arkivlaget.io: " + query);

        return objectModelService.getDataObjects(
                        identity,
                        SikriObjectTypes.CASE,
                        getSikriFilterExpression(query),
                        10, // consider using 0 aka no limit
                        relatedObjects)
                .stream()
                .map(CaseType.class::cast);
    }

    public Stream<CaseType> getCaseByExternalKey(SikriIdentity identity, String externalKey) {
        return objectModelService.getDataObjects(
                identity,
                SikriObjectTypes.EXTERNAL_SYSTEM_LINK_CASE,
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

    private String getSikriFilterExpression(String query) throws IllegalOdataFilterProperty {
        ODataLexer lexer = new ODataLexer(CharStreams.fromString(query));
        CommonTokenStream commonTokens = new CommonTokenStream(lexer);
        ODataParser oDataParser = new ODataParser(commonTokens);

        return oDataParser.filter().comparison().stream()
                .map(this::fromODataToSikriComparisonFilter)
                .collect(Collectors.joining(" AND "));
    }

    private String fromODataToSikriComparisonFilter(ODataParser.ComparisonContext context) throws IllegalOdataFilterProperty {
        String oDataProperty = context.property().getText();
        String oDataOperator = context.comparisonOperator().getText();
        String oDataValue = context.value().getText();

        String sikriProperty = odataFilterFieldMapper.getOrDefault(oDataProperty, null);
        if (sikriProperty == null) {
            throw new IllegalOdataFilterProperty(String.format("OData property %s is not supported", oDataProperty));
        }
        return sikriProperty + "=" + oDataValue;
    }
}
