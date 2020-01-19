package no.fint.sikri.data.noark.common;

import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.administrasjon.arkiv.*;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import no.fint.sikri.data.noark.journalpost.JournalpostService;
import no.fint.sikri.data.noark.merknad.MerknadService;
import no.fint.sikri.data.noark.nokkelord.NokkelordService;
import no.fint.sikri.data.noark.part.PartService;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.NOARKUtils;
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

    public <T extends SaksmappeResource> T applyValuesForSaksmappe(CaseType input, T resource) {
        String caseNumber = NOARKUtils.getMappeId(
                input.getCaseYear().getValue().toString(),
                input.getSequenceNumber().getValue().toString()
        );
        Integer caseYear = input.getCaseYear().getValue();
        Integer sequenceNumber = input.getSequenceNumber().getValue();

//        resource.setBeskrivelse(input.getFields().getBeskrivelse());
        resource.setMappeId(FintUtils.createIdentifikator(caseNumber));
        resource.setSystemId(FintUtils.createIdentifikator(input.getId().toString()));
        resource.setSakssekvensnummer(String.valueOf(sequenceNumber));
        resource.setSaksaar(String.valueOf(caseYear));
        resource.setSaksdato(input.getCaseDate().getValue().toGregorianCalendar().getTime());
        resource.setOpprettetDato(input.getCreatedDate().getValue().toGregorianCalendar().getTime());
        resource.setTittel(input.getTitle().getValue());
        resource.setOffentligTittel(input.getPublicTitle().getValue());

        resource.setJournalpost(journalpostService.queryForSaksmappe(resource));
        resource.setPart(partService.queryForSaksmappe(resource));
        // FIXME: 16/01/2020 Add remarks support
//        resource.setMerknad(merknadService.queryForMappe(input.getId()));
        // FIXME: 16/01/2020 Add keyword support
//        resource.setNoekkelord(nokkelordService.queryForMappe(input.getId()));

        resource.addAdministrativEnhet(Link.with(AdministrativEnhet.class, "systemid", input.getAdministrativeUnitId().getValue().toString()));
        resource.addArkivdel(Link.with(Arkivdel.class, "systemid", String.valueOf(input.getRegistryManagementUnitId().getValue())));
        resource.addOpprettetAv(Link.with(Arkivressurs.class, "systemid", input.getCreatedByUserNameId().getValue().toString()));
        resource.addSaksansvarlig(Link.with(Arkivressurs.class, "systemid", input.getOfficerNameId().getValue().toString()));
        resource.addSaksstatus(Link.with(Saksstatus.class, "systemid", input.getCaseStatusId().getValue()));
        resource.addKlasse(Link.with(Klasse.class, "systemid", String.valueOf(input.getPrimaryClassification().getValue().getClassId().getValue())));

        return resource;
    }


}
