package no.fint.documaster.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryResult;
import no.documaster.model.Result__1;
import no.fint.documaster.data.exception.GetDocumentException;
import no.fint.documaster.data.exception.IllegalCaseNumberFormat;
import no.fint.documaster.data.exception.NotTilskuddfartoyException;
import no.fint.documaster.data.noark.common.NoarkFactory;
import no.fint.documaster.data.noark.journalpost.JournalpostFactory;
import no.fint.documaster.data.noark.korrespondansepart.KorrespondansepartFactory;
import no.fint.documaster.repository.KodeverkRepository;
import no.fint.model.kultur.kulturminnevern.TilskuddFartoy;
import no.fint.model.resource.Link;
import no.fint.model.resource.kultur.kulturminnevern.TilskuddFartoyResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static no.fint.documaster.data.utilities.FintUtils.createIdentifikator;

@Slf4j
@Service
public class TilskuddFartoyFactory {

    @Autowired
    private KodeverkRepository kodeverkRepository;

    @Autowired
    private NoarkFactory noarkFactory;

    @Autowired
    private KorrespondansepartFactory korrespondansepartFactory;

    @Autowired
    private JournalpostFactory journalpostFactory;

    @Autowired
    private TilskuddFartoyDefaults tilskuddFartoyDefaults;

    public TilskuddFartoyResource toFintResource(Result__1 input) {
        if (input.getFields().getVirksomhetsspesifikkeMetadata() == null
                || input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak() == null
                || input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getKulturminneid() == null
                || input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy() == null
                || input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getFartoynavn() == null) {
            throw new NotTilskuddfartoyException(input.getFields().getMappeIdent());
        }

        TilskuddFartoyResource tilskuddFartoy = noarkFactory.applyValuesForSaksmappe(input, new TilskuddFartoyResource());

        tilskuddFartoy.setFartoyNavn(input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getFartoynavn().getValues().get(0));
        tilskuddFartoy.setKallesignal(input.getFields().getVirksomhetsspesifikkeMetadata().getFartoy().getKallesignal().getValues().get(0));
        tilskuddFartoy.setSoknadsnummer(createIdentifikator(input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getSoeknadsnummer().getValues().get(0)));
        tilskuddFartoy.setKulturminneId(input.getFields().getVirksomhetsspesifikkeMetadata().getDigisak().getKulturminneid().getValues().get(0));

        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "soknadsnummer", tilskuddFartoy.getSoknadsnummer().getIdentifikatorverdi()));
        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "mappeid", input.getFields().getMappeIdent()));
        tilskuddFartoy.addSelf(Link.with(TilskuddFartoy.class, "systemid", input.getId()));

        return tilskuddFartoy;
    }


    public List<TilskuddFartoyResource> toFintResourceList(QueryResult results) throws GetDocumentException, IllegalCaseNumberFormat {
        List<TilskuddFartoyResource> resources = new ArrayList<>(results.getResults().size());
        for (Result__1 result : results.getResults()) {
            resources.add(toFintResource(result));
        }
        return resources;
    }

}
