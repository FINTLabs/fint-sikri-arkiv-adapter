package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.documaster.model.QueryInput;
import no.documaster.model.Result__1;
import no.fint.sikri.data.utilities.FintUtils;
import no.fint.sikri.data.utilities.QueryUtils;
import no.fint.model.administrasjon.arkiv.Arkivressurs;
import no.fint.model.administrasjon.arkiv.DokumentStatus;
import no.fint.model.administrasjon.arkiv.DokumentType;
import no.fint.model.administrasjon.arkiv.TilknyttetRegistreringSom;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.DokumentbeskrivelseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class DokumentbeskrivelseFactory {
    @Autowired
    private DokumentobjektService dokumentobjektService;

    public DokumentbeskrivelseResource toFintResource(Result__1 result) {
        DokumentbeskrivelseResource resource = new DokumentbeskrivelseResource();
        resource.setTittel(result.getFields().getTittel());
        resource.setDokumentnummer(Long.valueOf(result.getFields().getDokumentnummer()));
        resource.setBeskrivelse(result.getFields().getBeskrivelse());
        resource.setOpprettetDato(FintUtils.parseIsoDate(result.getFields().getOpprettetDato()));
        resource.setForfatter(Collections.singletonList(result.getFields().getOpprettetAv()));

        resource.addOpprettetAv(Link.with(Arkivressurs.class, "systemid", result.getFields().getOpprettetAvBrukerIdent()));
        resource.addDokumentstatus(Link.with(DokumentStatus.class, "systemid", result.getFields().getDokumentstatus()));
        resource.addTilknyttetRegistreringSom(Link.with(TilknyttetRegistreringSom.class, "systemid", result.getFields().getTilknyttetRegistreringSom()));
        resource.addDokumentType(Link.with(DokumentType.class, "systemid", result.getFields().getDokumenttype()));

        resource.setDokumentobjekt(dokumentobjektService.queryDokumentobjekt(result.getId()));

        return resource;
    }

    public QueryInput createQueryInput(String id) {
        return QueryUtils.createQueryInput("Dokument", "refRegistrering.id", id);
    }
}
