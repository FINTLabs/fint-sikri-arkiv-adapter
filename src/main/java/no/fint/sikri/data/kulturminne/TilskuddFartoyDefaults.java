package no.fint.sikri.data.kulturminne;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.CaseType;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.model.administrasjon.arkiv.*;
import no.fint.model.resource.Link;
import no.fint.model.resource.kultur.kulturminnevern.TilskuddFartoyResource;
import no.fint.sikri.CaseDefaults;
import no.fint.sikri.data.CaseProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Slf4j
public class TilskuddFartoyDefaults {
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

    public void applyDefaultsForCreation(TilskuddFartoyResource tilskuddFartoy) {
        if (isNotBlank(properties.getSaksstatus()) && tilskuddFartoy.getSaksstatus().isEmpty()) {
            tilskuddFartoy.addSaksstatus(Link.with(
                    Saksstatus.class,
                    "systemid",
                    properties.getSaksstatus()
            ));
        }
        if (isNotBlank(properties.getArkivdel()) && tilskuddFartoy.getArkivdel().isEmpty()) {
            tilskuddFartoy.addArkivdel(Link.with(
                    Arkivdel.class,
                    "systemid",
                    properties.getArkivdel()
            ));
        }
        if (isNotBlank(properties.getAdministrativEnhet()) && tilskuddFartoy.getAdministrativEnhet().isEmpty()) {
            tilskuddFartoy.addAdministrativEnhet(Link.with(
                    Arkivdel.class,
                    "systemid",
                    properties.getAdministrativEnhet()
            ));
        }
        applyDefaultsForUpdate(tilskuddFartoy);
    }

    public void applyDefaultsForUpdate(TilskuddFartoyResource tilskuddFartoy) {
        if (tilskuddFartoy.getJournalpost() == null || tilskuddFartoy.getJournalpost().isEmpty()) {
            return;
        }
        tilskuddFartoy.getJournalpost().forEach(journalpost -> {

            if (isNotBlank(properties.getKorrespondansepartType())) {
                journalpost.getKorrespondansepart().forEach(korrespondanse -> {
                    if (korrespondanse.getKorrespondanseparttype().isEmpty()) {
                        korrespondanse.addKorrespondanseparttype(Link.with(
                                KorrespondansepartType.class,
                                "systemid",
                                properties.getKorrespondansepartType()));
                    }
                });
            }

            journalpost.getDokumentbeskrivelse().forEach(dokumentbeskrivelse -> {
                if (isNotBlank(properties.getDokumentstatus()) && dokumentbeskrivelse.getDokumentstatus().isEmpty()) {
                    dokumentbeskrivelse.addDokumentstatus(Link.with(
                            DokumentStatus.class,
                            "systemid",
                            properties.getDokumentstatus()
                    ));
                }
                if (isNotBlank(properties.getDokumentType()) && dokumentbeskrivelse.getDokumentType().isEmpty()) {
                    dokumentbeskrivelse.addDokumentType(Link.with(
                            DokumentType.class,
                            "systemid",
                            properties.getDokumentType()
                    ));
                }
                if (isNotBlank(properties.getTilknyttetRegistreringSom()) && dokumentbeskrivelse.getTilknyttetRegistreringSom().isEmpty()) {
                    dokumentbeskrivelse.addTilknyttetRegistreringSom(Link.with(
                            TilknyttetRegistreringSom.class,
                            "systemid",
                            properties.getTilknyttetRegistreringSom()
                    ));
                }
            });
            if (isNotBlank(properties.getJournalpostType()) && journalpost.getJournalposttype().isEmpty()) {
                journalpost.addJournalposttype(Link.with(
                        JournalpostType.class,
                        "systemid",
                        properties.getJournalpostType()));
            }
            if (isNotBlank(properties.getJournalstatus()) && journalpost.getJournalstatus().isEmpty()) {
                journalpost.addJournalstatus(Link.with(
                        JournalStatus.class,
                        "systemid",
                        properties.getJournalstatus()));
            }
            if (isNotBlank(properties.getAdministrativEnhet()) && journalpost.getJournalenhet().isEmpty()) {
                journalpost.addJournalenhet(Link.with(
                        AdministrativEnhet.class,
                        "systemid",
                        properties.getAdministrativEnhet()
                ));
            }
            if (isNotBlank(properties.getAdministrativEnhet()) && journalpost.getAdministrativEnhet().isEmpty()) {
                journalpost.addAdministrativEnhet(Link.with(
                        AdministrativEnhet.class,
                        "systemid",
                        properties.getAdministrativEnhet()
                ));
            }
            if (isNotBlank(properties.getArkivdel()) && journalpost.getArkivdel().isEmpty()) {
                journalpost.addArkivdel(Link.with(
                        Arkivdel.class,
                        "systemid",
                        properties.getArkivdel()
                ));
            }
        });

    }

    public void applyDefaultsToCaseType(TilskuddFartoyResource tilskuddFartoy, CaseType caseType) {

        caseType.setIsPhysical(false);

    }
}