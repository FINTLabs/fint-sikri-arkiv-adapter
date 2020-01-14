package no.fint.sikri.data.noark.common;

import no.documaster.model.Result__1;
import no.fint.sikri.data.noark.journalpost.JournalpostService;
import no.fint.sikri.data.noark.merknad.MerknadService;
import no.fint.sikri.data.noark.nokkelord.NokkelordService;
import no.fint.sikri.data.noark.part.PartService;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.model.administrasjon.arkiv.*;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoarkFactory {

    @Autowired
    private JournalpostService journalpostService;

    @Autowired
    private PartService partService;

    @Autowired
    private MerknadService merknadService;

    @Autowired
    private NokkelordService nokkelordService;

    public <T extends SaksmappeResource> T applyValuesForSaksmappe(Result__1 input, T resource) {
        String caseNumber = input.getFields().getMappeIdent();
        Integer caseYear = input.getFields().getSaksaar();
        Integer sequenceNumber = input.getFields().getSakssekvensnummer();

        resource.setBeskrivelse(input.getFields().getBeskrivelse());
        resource.setMappeId(FintUtils.createIdentifikator(caseNumber));
        resource.setSystemId(FintUtils.createIdentifikator(input.getId()));
        resource.setSakssekvensnummer(String.valueOf(sequenceNumber));
        resource.setSaksaar(String.valueOf(caseYear));
        resource.setSaksdato(FintUtils.parseDate(input.getFields().getSaksdato()));
        resource.setOpprettetDato(FintUtils.parseIsoDate(input.getFields().getOpprettetDato()));
        resource.setTittel(input.getFields().getTittel());
        resource.setOffentligTittel(input.getFields().getOffentligTittel());

        resource.setJournalpost(journalpostService.queryForSaksmappe(resource));
        resource.setPart(partService.queryForSaksmappe(resource));
        resource.setMerknad(merknadService.queryForMappe(input.getId()));
        resource.setNoekkelord(nokkelordService.queryForMappe(input.getId()));

        resource.addAdministrativEnhet(Link.with(AdministrativEnhet.class, "systemid", input.getFields().getAdministrativEnhet()));
        resource.addArkivdel(Link.with(Arkivdel.class, "systemid", String.valueOf(input.getLinks().getRefArkivdel())));
        resource.addOpprettetAv(Link.with(Arkivressurs.class, "systemid", input.getFields().getOpprettetAvBrukerIdent()));
        resource.addSaksansvarlig(Link.with(Arkivressurs.class, "systemid", input.getFields().getSaksansvarligBrukerIdent()));
        resource.addSaksstatus(Link.with(Saksstatus.class, "systemid", input.getFields().getSaksstatus()));
        resource.addKlasse(Link.with(Klasse.class, "systemid", String.valueOf(input.getLinks().getRefPrimaerKlasse())));

        return resource;
    }
    
}
