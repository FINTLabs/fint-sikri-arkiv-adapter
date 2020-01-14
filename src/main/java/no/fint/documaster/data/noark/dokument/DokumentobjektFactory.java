package no.fint.documaster.data.noark.dokument;

import no.documaster.model.QueryInput;
import no.documaster.model.Result__1;
import no.fint.documaster.data.utilities.QueryUtils;
import no.fint.model.administrasjon.arkiv.Arkivressurs;
import no.fint.model.administrasjon.arkiv.Dokumentfil;
import no.fint.model.administrasjon.arkiv.Variantformat;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.DokumentobjektResource;
import org.springframework.stereotype.Service;

@Service
public class DokumentobjektFactory {
    public QueryInput createQueryInput(String id) {
        return QueryUtils.createQueryInput("Dokumentversjon", "refDokument.id", id);
    }

    public DokumentobjektResource toFintResource(Result__1 result) {
        DokumentobjektResource resource = new DokumentobjektResource();

        resource.setFilstorrelse(String.valueOf(result.getFields().getFilstoerrelse()));
        resource.setFormat(result.getFields().getInnholdstype());
        resource.setFormatDetaljer(result.getFields().getFormat());
        resource.setSjekksum(result.getFields().getSjekksum());
        resource.setSjekksumAlgoritme(result.getFields().getSjekksumAlgoritme());
        resource.setVersjonsummer(Long.valueOf(result.getFields().getVersjonsnummer()));

        resource.addReferanseDokumentfil(Link.with(Dokumentfil.class, "systemid", result.getFields().getReferanseDokumentfil()));
        resource.addVariantFormat(Link.with(Variantformat.class, "systemid", result.getFields().getVariantformat()));
        resource.addOpprettetAv(Link.with(Arkivressurs.class, "systemid", result.getFields().getOpprettetAvBrukerIdent()));

        return resource;
    }
}
