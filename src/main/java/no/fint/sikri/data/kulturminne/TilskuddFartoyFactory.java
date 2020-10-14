package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.AdditionalFieldService;
import no.fint.arkiv.TitleService;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ExternalSystemLinkCaseType;
import no.fint.model.resource.kultur.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.data.noark.journalpost.JournalpostFactory;
import no.fint.sikri.data.noark.korrespondansepart.KorrespondansepartFactory;
import no.fint.sikri.repository.KodeverkRepository;
import no.fint.sikri.service.SikriCaseDefaultsService;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

import static no.fint.sikri.data.utilities.SikriUtils.applyParameterFromLink;

@Slf4j
@Service
public class TilskuddFartoyFactory {

    @Autowired
    private NoarkFactory noarkFactory;

    private ObjectFactory objectFactory;

    public TilskuddFartoyFactory() {
        objectFactory = new ObjectFactory();
    }

    public CaseType toCaseType(TilskuddFartoyResource tilskuddFartoy) {
        return noarkFactory.toCaseType(tilskuddFartoy);
    }

    public ExternalSystemLinkCaseType externalSystemLink(Integer caseId, String externalKey) {
        ExternalSystemLinkCaseType externalSystemLinkCaseType = new ExternalSystemLinkCaseType();
        externalSystemLinkCaseType.setCaseId(caseId);
        externalSystemLinkCaseType.setExternalKey(externalKey);
        externalSystemLinkCaseType.setExternalSystemCode(4);

        return externalSystemLinkCaseType;
    }

    public TilskuddFartoyResource toFintResource(CaseType input) {
//        if (input.getFields().getVirksomhetsspesifikkeMetadata() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getKulturminneid() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getFartoynavn() == null) {
//            throw new NotTilskuddfartoyException(input.getFields().getMappeIdent());
//        }

        TilskuddFartoyResource tilskuddFartoy = noarkFactory.applyValuesForSaksmappe(input, new TilskuddFartoyResource());

//        tilskuddFartoy.setFartoyNavn(input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getFartoynavn().().get(0));
//        tilskuddFartoy.setKallesignal(input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getKallesignal().().get(0));
//        tilskuddFartoy.setSoknadsnummer(createIdentifikator(input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getSoeknadsnummer().().get(0)));
//        tilskuddFartoy.setKulturminneId(input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getKulturminneid().().get(0));

//        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "soknadsnummer", tilskuddFartoy.getSoknadsnummer().getIdentifikatorverdi()));
//        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "mappeid", input.getFields().getMappeIdent()));
//        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "systemid", input.getId()));

        return tilskuddFartoy;
    }


/*


    public List<TilskuddFartoyResource> toFintResourceList(QueryResult results) throws GetDocumentException, IllegalCaseNumberFormat {
        List<TilskuddFartoyResource> resources = new ArrayList<>(results.getResults().size());
        for (Result__1 result : results.getResults()) {
            resources.add(toFintResource(result));
        }
        return resources;
    }

 */


}
