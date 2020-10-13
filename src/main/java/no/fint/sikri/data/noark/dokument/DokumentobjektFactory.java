package no.fint.sikri.data.noark.dokument;

import no.fint.arkiv.sikri.oms.DocumentObjectType;
import no.fint.arkiv.sikri.oms.FileFormatType;
import no.fint.model.arkiv.noark.Arkivressurs;
import no.fint.model.arkiv.noark.Dokumentfil;
import no.fint.model.arkiv.kodeverk.Variantformat;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.DokumentobjektResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static no.fint.sikri.data.utilities.FintUtils.applyIdFromLink;
import static no.fint.sikri.data.utilities.SikriUtils.optionalValue;
import static no.fint.sikri.data.utilities.SikriUtils.optionalValueFn;

@Service
public class DokumentobjektFactory {

    public DokumentobjektResource toFintResource(DocumentObjectType result) {
        DokumentobjektResource resource = new DokumentobjektResource();

        optionalValue(result.getFileSize())
                .map(String::valueOf)
                .ifPresent(resource::setFilstorrelse);

        optionalValue(result.getFileformatId())
                .ifPresent(resource::setFormat);

        optionalValue(result.getFileFormat())
                .flatMap(optionalValueFn(FileFormatType::getDescription))
                .ifPresent(resource::setFormatDetaljer);

        optionalValue(result.getChecksum())
                .ifPresent(resource::setSjekksum);

        optionalValue(result.getCheckSumAlgorithm())
                .ifPresent(resource::setSjekksumAlgoritme);

        resource.setVersjonsnummer(Long.valueOf(result.getVersionNumber()));

        resource.addReferanseDokumentfil(Link.with(Dokumentfil.class, "systemid",
                String.format("%d_%d_%s",
                        result.getDocumentDescriptionId(),
                        result.getVersionNumber(),
                        result.getVariantFormatId().getValue()
                )));

        optionalValue(result.getVariantFormatId())
                .map(Link.apply(Variantformat.class, "systemid"))
                .ifPresent(resource::addVariantFormat);

        optionalValue(result.getCreatedByUserNameId())
                .map(String::valueOf)
                .map(Link.apply(Arkivressurs.class, "systemid"))
                .ifPresent(resource::addOpprettetAv);

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
                    document.setVersion(Optional.ofNullable(dokumentobjektResource.getVersjonsnummer()).map(Math::toIntExact).orElse(1));
                    document.setGuid(guid);
                    document.setContentType(dokumentobjektResource.getFormat());
                    document.setFormat(dokumentobjektResource.getFormatDetaljer());
                    return document;
                });
    }
}
