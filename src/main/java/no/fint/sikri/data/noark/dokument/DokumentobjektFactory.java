package no.fint.sikri.data.noark.dokument;

import no.fint.arkiv.sikri.oms.DocumentObjectType;
import no.fint.model.administrasjon.arkiv.Arkivressurs;
import no.fint.model.administrasjon.arkiv.Dokumentfil;
import no.fint.model.administrasjon.arkiv.Variantformat;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.DokumentobjektResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.FintUtils.applyIdFromLink;

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

    public Stream<CheckinDocument> toCheckinDocument(DokumentobjektResource dokumentobjektResource) {
        return dokumentobjektResource
                .getReferanseDokumentfil()
                .stream()
                .filter(Objects::nonNull)
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .map(guid -> {
                    CheckinDocument document = new CheckinDocument();
                    applyIdFromLink(dokumentobjektResource.getVariantFormat(), document::setVariant);
                    document.setVersion(Optional.ofNullable(dokumentobjektResource.getVersjonsummer()).map(Math::toIntExact).orElse(1));
                    document.setGuid(guid);
                    return document;
                });
    }
}
