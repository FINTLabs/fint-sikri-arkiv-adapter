package no.fint.sikri.data.personal;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.model.administrasjon.arkiv.*;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.personal.PersonalmappeResource;
import no.fint.sikri.CaseDefaults;
import no.fint.sikri.data.CaseProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class PersonalmappeDefaults {
    @Autowired
    private CaseDefaults caseDefaults;

    private CaseProperties properties;
    private ObjectFactory objectFactory;


    @PostConstruct
    public void init() {
        log.info("Case Defaults: {}", caseDefaults);
        properties = caseDefaults.getCasetype().get("tilskudd-fartoy");
        log.info("Defaults for TilskuddFartoy: {}", properties);
        objectFactory = new ObjectFactory();
    }

    public void applyDefaultsForCreation(PersonalmappeResource personalmappeResource) {
        if (personalmappeResource.getSaksstatus().isEmpty()) {
            personalmappeResource.addSaksstatus(Link.with(
                    Saksstatus.class,
                    "systemid",
                    properties.getSaksstatus()
            ));
        }
        if (personalmappeResource.getArkivdel().isEmpty()) {
            personalmappeResource.addArkivdel(Link.with(
                    Arkivdel.class,
                    "systemid",
                    properties.getArkivdel()
            ));
        }
//        if (personalmappeResource.getAdministrativEnhet().isEmpty()) {
//            personalmappeResource.addAdministrativEnhet(Link.with(
//                    Arkivdel.class,
//                    "systemid",
//                    properties.getAdministrativEnhet()
//            ));
//        }
        applyDefaultsForUpdate(personalmappeResource);
    }

    public void applyDefaultsForUpdate(PersonalmappeResource personalmappeResource) {
        if (personalmappeResource.getJournalpost() == null || personalmappeResource.getJournalpost().isEmpty()) {
            return;
        }
        personalmappeResource.getJournalpost().forEach(journalpost -> {
            journalpost.getKorrespondansepart().forEach(korrespondanse -> {
                if (korrespondanse.getKorrespondanseparttype().isEmpty()) {
                    korrespondanse.addKorrespondanseparttype(Link.with(
                            KorrespondansepartType.class,
                            "systemid",
                            properties.getKorrespondansepartType()));
                }
            });
            journalpost.getDokumentbeskrivelse().forEach(dokumentbeskrivelse -> {
                if (dokumentbeskrivelse.getDokumentstatus().isEmpty()) {
                    dokumentbeskrivelse.addDokumentstatus(Link.with(
                            DokumentStatus.class,
                            "systemid",
                            properties.getDokumentstatus()
                    ));
                }
                if (dokumentbeskrivelse.getDokumentType().isEmpty()) {
                    dokumentbeskrivelse.addDokumentType(Link.with(
                            DokumentType.class,
                            "systemid",
                            properties.getDokumentType()
                    ));
                }
                if (dokumentbeskrivelse.getTilknyttetRegistreringSom().isEmpty()) {
                    dokumentbeskrivelse.addTilknyttetRegistreringSom(Link.with(
                            TilknyttetRegistreringSom.class,
                            "systemid",
                            properties.getTilknyttetRegistreringSom()
                    ));
                }
            });
            if (journalpost.getJournalposttype().isEmpty()) {
                journalpost.addJournalposttype(Link.with(
                        JournalpostType.class,
                        "systemid",
                        properties.getJournalpostType()));
            }
            if (journalpost.getJournalstatus().isEmpty()) {
                journalpost.addJournalstatus(Link.with(
                        JournalStatus.class,
                        "systemid",
                        properties.getJournalstatus()));
            }
            if (journalpost.getJournalenhet().isEmpty()) {
                journalpost.addJournalenhet(Link.with(
                        AdministrativEnhet.class,
                        "systemid",
                        properties.getAdministrativEnhet()
                ));
            }
            if (journalpost.getAdministrativEnhet().isEmpty()) {
                journalpost.addAdministrativEnhet(Link.with(
                        AdministrativEnhet.class,
                        "systemid",
                        properties.getAdministrativEnhet()
                ));
            }
            if (journalpost.getArkivdel().isEmpty()) {
                journalpost.addArkivdel(Link.with(
                        Arkivdel.class,
                        "systemid",
                        properties.getArkivdel()
                ));
            }
        });

    }

    public void applyDefaultsToCaseType(PersonalmappeResource personalmappeResource, CaseType caseType) {

        caseType.setIsPhysical(objectFactory.createCaseTypeIsPhysical(false));

    }
}