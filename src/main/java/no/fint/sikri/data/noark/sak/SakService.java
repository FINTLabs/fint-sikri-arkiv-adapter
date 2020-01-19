package no.fint.sikri.data.noark.sak;

import no.fint.model.resource.administrasjon.arkiv.SakResource;
import no.fint.sikri.data.exception.CaseNotFound;
import no.fint.sikri.data.exception.GetCaseException;
import no.fint.sikri.data.exception.GetDocumentException;
import no.fint.sikri.data.exception.IllegalCaseNumberFormat;
import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SakService {

    @Autowired
    private SakFactory sakFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

    public List<SakResource> searchSakByQueryParams(Map<String, Object> query) throws GetDocumentException, IllegalCaseNumberFormat, GetCaseException, CaseNotFound {
        return sakFactory.toFintResourceList(sikriObjectModelService.getGetCasesQueryByTitle(query));

    }

    public SakResource getSakByCaseNumber(String caseNumber) throws GetDocumentException, IllegalCaseNumberFormat, CaseNotFound, GetCaseException, CaseNotFound {
        return sakFactory.toFintResource(sikriObjectModelService.getSakByCaseNumber(caseNumber));

    }

    public SakResource getSakBySystemId(String systemId) throws GetDocumentException, IllegalCaseNumberFormat, CaseNotFound, GetCaseException {
        return sakFactory.toFintResource(sikriObjectModelService.getSakBySystemId(systemId));
    }

}
