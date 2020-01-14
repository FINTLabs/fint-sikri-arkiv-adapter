package no.fint.sikri.data.noark.journalpost;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.documaster.model.Result__1;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseFactory;
import no.fint.sikri.data.noark.dokument.DokumentbeskrivelseService;
import no.fint.sikri.data.noark.korrespondansepart.KorrespondanseService;
import no.fint.sikri.data.noark.merknad.MerknadService;
import no.fint.sikri.data.noark.nokkelord.NokkelordService;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.QueryUtils;
import no.fint.sikri.repository.KodeverkRepository;
import no.fint.model.administrasjon.arkiv.Arkivressurs;
import no.fint.model.administrasjon.arkiv.JournalStatus;
import no.fint.model.administrasjon.arkiv.JournalpostType;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.JournalpostResource;
import no.fint.model.resource.administrasjon.arkiv.SaksmappeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.FintUtils.validIdentifikator;

@Slf4j
@Service
public class JournalpostFactory {

    @Autowired
    private KodeverkRepository kodeverkRepository;

    @Autowired
    private DokumentbeskrivelseFactory dokumentbeskrivelseFactory;

    @Autowired
    private DokumentbeskrivelseService dokumentbeskrivelseService;

    @Autowired
    private KorrespondanseService korrespondanseService;

    @Autowired
    private MerknadService merknadService;

    @Autowired
    private NokkelordService nokkelordService;

    public JournalpostResource toFintResource(Result__1 result) {
        JournalpostResource journalpost = new JournalpostResource();

        journalpost.setTittel(result.getFields().getTittel());
        journalpost.setOffentligTittel(result.getFields().getOffentligTittel());
        journalpost.setOpprettetDato(FintUtils.parseIsoDate(result.getFields().getOpprettetDato()));
        journalpost.setJournalDato(FintUtils.parseDate(result.getFields().getRegistreringsDato()));
        journalpost.setJournalAr(String.valueOf(result.getFields().getJournalaar()));
        journalpost.setJournalPostnummer(Long.valueOf(result.getFields().getJournalpostnummer()));
        journalpost.setJournalSekvensnummer(Long.valueOf(result.getFields().getJournalsekvensnummer()));
        journalpost.setBeskrivelse(result.getFields().getBeskrivelse());

/*
        optionalValue(result.getFiles())
                .map(ArrayOfDocumentFileResult::getDocumentFileResult)
                .map(List::size)
                .map(Integer::longValue)
                .ifPresent(journalpost::setAntallVedlegg);
*/
/*
        optionalValue(result.getDocumentDate())
                .map(XMLGregorianCalendar::toGregorianCalendar)
                .map(GregorianCalendar::getTime)
                .ifPresent(journalpost::setDokumentetsDato);
*/

        // FIXME: 2019-05-08 check for empty
        journalpost.setReferanseArkivDel(Collections.emptyList());

        // TODO: 2019-05-08 Check noark if this is correct
        journalpost.setForfatter(
                Stream.<String>builder()
                        .add(result.getFields().getOpprettetAv())
                        .add(result.getFields().getJournalansvarlig())
                        .build()
                        .distinct()
                        .collect(Collectors.toList()));

        journalpost.setKorrespondansepart(korrespondanseService.queryForRegistrering(result.getId()));
        journalpost.setMerknad(merknadService.queryForRegistrering(result.getId()));
        journalpost.setNokkelord(nokkelordService.queryForRegistrering(result.getId()));
        journalpost.setDokumentbeskrivelse(dokumentbeskrivelseService.queryForJournalpost(result.getId()));
/*
        optionalValue(result.getResponsibleEnterprise())
                .map(ResponsibleEnterprise::getRecno)
                .map(String::valueOf)
                .map(Link.apply(Organisasjonselement.class, "organisasjonsid"))
                .ifPresent(journalpost::addAdministrativEnhet);
*/


        journalpost.addSaksbehandler(Link.with(Arkivressurs.class, "systemid", result.getFields().getJournalansvarligBrukerIdent()));
        journalpost.addOpprettetAv(Link.with(Arkivressurs.class, "systemid", result.getFields().getOpprettetAvBrukerIdent()));
        journalpost.addJournalposttype(Link.with(JournalpostType.class, "systemid", result.getFields().getJournalposttype()));
        journalpost.addJournalstatus(Link.with(JournalStatus.class, "systemid", result.getFields().getJournalstatus()));

        return journalpost;
    }

    public QueryInput createQueryInput(SaksmappeResource saksmappe) {
        if (saksmappe == null)
            return null;

        if (validIdentifikator(saksmappe.getMappeId())) {
            return QueryUtils.createQueryInput("Journalpost", "refMappe.mappeIdent", saksmappe.getMappeId().getIdentifikatorverdi());
        } else if (validIdentifikator(saksmappe.getSystemId())) {
            return QueryUtils.createQueryInput("Journalpost", "refMappe.id", saksmappe.getSystemId().getIdentifikatorverdi());
        }
        throw new IllegalArgumentException("Illegal arguments: " + saksmappe);
    }
}
