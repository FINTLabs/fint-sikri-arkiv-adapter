package no.fint.sikri.data.noark.common;

import no.fint.arkiv.sikri.oms.AdministrativeUnitType;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.model.administrasjon.arkiv.*;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import no.fint.sikri.data.noark.journalpost.JournalpostService;
import no.fint.sikri.data.noark.merknad.MerknadService;
import no.fint.sikri.data.noark.part.PartService;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.NOARKUtils;
import no.fint.sikri.data.utilities.SikriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static no.fint.sikri.data.utilities.SikriUtils.optionalValue;

@Service
public class NoarkFactory {

    @Autowired
    private JournalpostService journalpostService;

    @Autowired
    private PartService partService;

    @Autowired
    private MerknadService merknadService;


    public <T extends SaksmappeResource> T applyValuesForSaksmappe(CaseType input, T resource) {
        String caseNumber = NOARKUtils.getMappeId(
                input.getCaseYear().toString(),
                input.getSequenceNumber().toString()
        );
        Integer caseYear = input.getCaseYear();
        Integer sequenceNumber = input.getSequenceNumber();

        resource.setMappeId(FintUtils.createIdentifikator(caseNumber));
        resource.setSystemId(FintUtils.createIdentifikator(input.getId().toString()));
        resource.setSakssekvensnummer(String.valueOf(sequenceNumber));
        resource.setSaksaar(String.valueOf(caseYear));
        resource.setSaksdato(input.getCaseDate().toGregorianCalendar().getTime());
        resource.setOpprettetDato(input.getCreatedDate().toGregorianCalendar().getTime());
        resource.setTittel(input.getTitle());
        resource.setOffentligTittel(input.getPublicTitle());

        resource.setJournalpost(journalpostService.queryForSaksmappe(resource));
        resource.setPart(partService.queryForSaksmappe(resource));

        resource.setMerknad(merknadService.getRemarkForCase(input.getId().toString()));

        optionalValue(input.getAdministrativeUnit())
                .map(AdministrativeUnitType::getShortCodeThisLevel)
                .flatMap(SikriUtils::optionalValue)
                .map(Link.apply(AdministrativEnhet.class, "systemid"))
                .ifPresent(resource::addAdministrativEnhet);

        optionalValue(input.getRegistryManagementUnitId())
                .map(Link.apply(Arkivdel.class, "systemid"))
                .ifPresent(resource::addArkivdel);

        optionalValue(input.getCreatedByUserNameId())
                .map(String::valueOf)
                .map(Link.apply(Arkivressurs.class, "systemid"))
                .ifPresent(resource::addOpprettetAv);

        optionalValue(input.getOfficerNameId())
                .map(String::valueOf)
                .map(Link.apply(Arkivressurs.class, "systemid"))
                .ifPresent(resource::addSaksansvarlig);

        optionalValue(input.getCaseStatusId())
                .map(String::valueOf)
                .map(Link.apply(Saksstatus.class, "systemid"))
                .ifPresent(resource::addSaksstatus);

        optionalValue(input.getPrimaryClassification())
                .ifPresent(c -> resource.addKlasse(
                        Link.with(
                                Klasse.class,
                                "systemid",
                                String.valueOf(input.getPrimaryClassification().getClassId()))
                        )
                );

        return resource;
    }


}
