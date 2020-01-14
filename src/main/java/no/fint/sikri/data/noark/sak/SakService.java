package no.fint.sikri.data.noark.sak;

import no.fint.sikri.service.SikriObjectModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SakService {

    @Autowired
    private SakFactory sakFactory;

    @Autowired
    private SikriObjectModelService sikriObjectModelService;

//    public List<SakResource> searchSakByQueryParams(Map<String, Object> query) throws GetDocumentException, IllegalCaseNumberFormat, GetCaseException {
//        return sakFactory.toFintResourceList(noark5WebService.query(sakFactory.getQueryInputFromQueryParams(query)));
//    }
//
//    public SakResource getSakByCaseNumber(String caseNumber) throws GetDocumentException, IllegalCaseNumberFormat, CaseNotFound, GetCaseException {
//        return sakFactory.toFintResourceList(noark5WebService.query(sakFactory.getQueryInputFromMappeId(caseNumber)))
//                .stream().findAny().orElseThrow(() -> new CaseNotFound(caseNumber));
//    }
//
//    public SakResource getSakBySystemId(String systemId) throws GetDocumentException, IllegalCaseNumberFormat, CaseNotFound, GetCaseException {
//        return sakFactory.toFintResourceList(noark5WebService.query(sakFactory.getQueryInputFromSystemId(systemId)))
//                .stream().findAny().orElseThrow(() -> new CaseNotFound(systemId));
//    }

}
