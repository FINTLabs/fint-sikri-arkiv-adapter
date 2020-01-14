package no.fint.documaster.data.noark.sak;

import no.fint.documaster.data.exception.CaseNotFound;
import no.fint.documaster.data.exception.GetCaseException;
import no.fint.documaster.data.exception.GetDocumentException;
import no.fint.documaster.data.exception.IllegalCaseNumberFormat;
import no.fint.documaster.service.Noark5WebService;
import no.fint.model.resource.administrasjon.arkiv.SakResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SakService {

    @Autowired
    private SakFactory sakFactory;

    @Autowired
    private Noark5WebService noark5WebService;

    public List<SakResource> searchSakByQueryParams(Map<String, Object> query) throws GetDocumentException, IllegalCaseNumberFormat, GetCaseException {
        return sakFactory.toFintResourceList(noark5WebService.query(sakFactory.getQueryInputFromQueryParams(query)));
    }

    public SakResource getSakByCaseNumber(String caseNumber) throws GetDocumentException, IllegalCaseNumberFormat, CaseNotFound, GetCaseException {
        return sakFactory.toFintResourceList(noark5WebService.query(sakFactory.getQueryInputFromMappeId(caseNumber)))
                .stream().findAny().orElseThrow(() -> new CaseNotFound(caseNumber));
    }

    public SakResource getSakBySystemId(String systemId) throws GetDocumentException, IllegalCaseNumberFormat, CaseNotFound, GetCaseException {
        return sakFactory.toFintResourceList(noark5WebService.query(sakFactory.getQueryInputFromSystemId(systemId)))
                .stream().findAny().orElseThrow(() -> new CaseNotFound(systemId));
    }

}
