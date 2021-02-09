package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.CaseDefaults;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.arkiv.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.data.noark.common.NoarkFactory;
import no.fint.sikri.service.SikriIdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TilskuddFartoyFactory {

    @Autowired
    private NoarkFactory noarkFactory;

    @Autowired
    private SikriIdentityService identityService;

    @Autowired
    private CaseDefaults caseDefaults;

    public CaseType toCaseType(TilskuddFartoyResource tilskuddFartoy) {
        return noarkFactory.toCaseType(caseDefaults.getTilskuddfartoy(), tilskuddFartoy);
    }

    public TilskuddFartoyResource toFintResource(CaseType input) {
//        if (input.getFields().getVirksomhetsspesifikkeMetadata() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getKulturminneid() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy() == null
//                || input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getFartoynavn() == null) {
//            throw new NotTilskuddfartoyException(input.getFields().getMappeIdent());
//        }

        final TilskuddFartoyResource resource = new TilskuddFartoyResource();
        resource.setSoknadsnummer(new Identifikator());
        TilskuddFartoyResource tilskuddFartoy = noarkFactory.applyValuesForSaksmappe(
                identityService.getIdentityForCaseType(resource),
                caseDefaults.getTilskuddfartoy(),
                input, resource);

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
