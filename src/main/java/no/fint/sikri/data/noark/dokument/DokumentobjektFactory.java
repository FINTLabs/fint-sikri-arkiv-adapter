package no.fint.sikri.data.noark.dokument;

import no.fint.arkiv.sikri.oms.DocumentObjectType;
import no.fint.model.administrasjon.arkiv.Arkivressurs;
import no.fint.model.administrasjon.arkiv.Dokumentfil;
import no.fint.model.administrasjon.arkiv.Variantformat;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.DokumentobjektResource;
import org.springframework.stereotype.Service;

@Service
public class DokumentobjektFactory {

    public DokumentobjektResource toFintResource(DocumentObjectType result) {
        DokumentobjektResource resource = new DokumentobjektResource();

        resource.setFilstorrelse(String.valueOf(result.getFileSize().getValue()));
        resource.setFormat(result.getContentType().getValue());
        resource.setFormatDetaljer(result.getFileFormat().getValue().getFileExtension().getValue());
        resource.setSjekksum(result.getChecksum().getValue());
        resource.setSjekksumAlgoritme(result.getCheckSumAlgorithm().getValue());
        resource.setVersjonsummer(Long.valueOf(result.getVersionNumber()));

        resource.addReferanseDokumentfil(Link.with(Dokumentfil.class, "systemid",
                String.format("%d_%d_%s",
                        result.getDocumentDescriptionId(),
                        result.getVersionNumber(),
                        result.getVariantFormatId().getValue()
                )));
        resource.addVariantFormat(Link.with(Variantformat.class, "systemid", result.getVariantFormatId().getValue()));
        resource.addOpprettetAv(Link.with(Arkivressurs.class, "systemid", String.valueOf(result.getCreatedByUserNameId().getValue())));

        return resource;
    }
}
