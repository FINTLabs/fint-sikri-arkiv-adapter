package no.novari.fint.sikri.data.noark.dokument;

import no.fint.arkiv.sikri.oms.DocumentObjectType;
import no.fint.arkiv.sikri.oms.FileFormatType;
import no.novari.fint.model.arkiv.kodeverk.Format;
import no.novari.fint.model.arkiv.kodeverk.Variantformat;
import no.novari.fint.model.arkiv.noark.Arkivressurs;
import no.novari.fint.model.arkiv.noark.Dokumentfil;
import no.novari.fint.model.resource.Link;
import no.novari.fint.model.resource.arkiv.noark.DokumentobjektResource;
import no.novari.fint.sikri.repository.InternalRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static no.novari.fint.sikri.data.utilities.FintUtils.applyIdFromLink;
import static no.novari.fint.sikri.data.utilities.SikriUtils.optionalValue;

@Service
public class DokumentobjektFactory {

    @Value("${fint.sikri.variantformat:}")
    private String variantFormat;

    private final InternalRepository internalRepository;

    public DokumentobjektFactory(InternalRepository internalRepository) {
        this.internalRepository = internalRepository;
    }

    public DokumentobjektResource toFintResource(DocumentObjectType result) {
        DokumentobjektResource resource = new DokumentobjektResource();

        optionalValue(result.getFileSize())
                .map(String::valueOf)
                .ifPresent(resource::setFilstorrelse);

        optionalValue(result.getFileformatId())
                .map(Link.apply(Format.class, "systemid"))
                .ifPresent(resource::addFilformat);

        optionalValue(result.getFileFormat())
                .map(FileFormatType::getDescription)
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
                        result.getVariantFormatId())
                ));

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
                .map(internalRepository::silentGetFile)
                .map(dokumentfilResource -> {
                    CheckinDocument document = new CheckinDocument();
                    applyIdFromLink(dokumentobjektResource.getVariantFormat(), document::setVariant);
                    applyIdFromLink(dokumentobjektResource.getFilformat(), document::setContentType);
                    document.setVersion(Optional.ofNullable(dokumentobjektResource.getVersjonsnummer()).map(Math::toIntExact).orElse(1));
                    document.setContent(Base64.getDecoder().decode(dokumentfilResource.getData()));
                    document.setFormat(dokumentobjektResource.getFormatDetaljer());
                    document.setFilename(dokumentfilResource.getFilnavn());
                    return document;
                });
    }

    public DocumentObjectType toDocumentObject(CheckinDocument checkinDocument, String filePath) {
        DocumentObjectType documentObject = new DocumentObjectType();
        documentObject.setDocumentDescriptionId(checkinDocument.getDocumentId());
        documentObject.setVersionNumber(checkinDocument.getVersion());
        if (StringUtils.isNotBlank(variantFormat)) {
            documentObject.setVariantFormatId(variantFormat);
        } else {
            documentObject.setVariantFormatId(checkinDocument.getVariant());
        }
        documentObject.setFileformatId(checkinDocument.getContentType());
        documentObject.setFilePath(filePath);
        return documentObject;
    }

}
