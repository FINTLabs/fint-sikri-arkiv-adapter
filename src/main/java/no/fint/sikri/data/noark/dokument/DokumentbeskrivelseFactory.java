package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.DocumentDescriptionType;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.arkiv.sikri.oms.RegistryEntryDocumentType;
import no.fint.model.arkiv.kodeverk.DokumentStatus;
import no.fint.model.arkiv.kodeverk.DokumentType;
import no.fint.model.arkiv.kodeverk.TilknyttetRegistreringSom;
import no.fint.model.arkiv.noark.Arkivressurs;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.DokumentbeskrivelseResource;
import no.fint.sikri.data.noark.journalpost.RegistryEntryDocuments;
import no.fint.sikri.data.utilities.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.stream.Collectors;

import static no.fint.sikri.data.utilities.SikriUtils.*;

@Slf4j
@Service
public class DokumentbeskrivelseFactory {
    @Autowired
    private DokumentobjektFactory dokumentobjektFactory;

    @Autowired
    private DokumentobjektService dokumentobjektService;

    @Autowired
    private XmlUtils xmlUtils;

    private ObjectFactory objectFactory = new ObjectFactory();

    public DokumentbeskrivelseResource toFintResource(RegistryEntryDocumentType result) {
        DokumentbeskrivelseResource resource = new DokumentbeskrivelseResource();

        optionalValue(result.getDocumentDescription()).ifPresent(
                documentDescription -> {
                    optionalValue(documentDescription.getDocumentTitle()).ifPresent(resource::setTittel);
                    optionalValue(documentDescription.getCreatedByUserNameId()).map(String::valueOf).map(Collections::singletonList).ifPresent(resource::setForfatter);
                    optionalValue(documentDescription.getCreatedByUserNameId()).map(String::valueOf).map(Link.apply(Arkivressurs.class, "systemid")).ifPresent(resource::addOpprettetAv);
                    optionalValue(documentDescription.getDocumentStatusId()).map(Link.apply(DokumentStatus.class, "systemid")).ifPresent(resource::addDokumentstatus);
                    optionalValue(documentDescription.getDocumentCategoryId()).map(Link.apply(DokumentType.class, "systemid")).ifPresent(resource::addDokumentType);

                    optionalValue(documentDescription.getCurrentVersion()).map(dokumentobjektFactory::toFintResource).map(Collections::singletonList).ifPresent(resource::setDokumentobjekt);
                }
        );

        resource.setDokumentnummer(Long.valueOf(result.getSortOrder()));

        optionalValue(result.getCreatedDate()).map(XMLGregorianCalendar::toGregorianCalendar).map(GregorianCalendar::getTime).ifPresent(resource::setOpprettetDato);
        optionalValue(result.getDocumentLinkTypeId()).map(Link.apply(TilknyttetRegistreringSom.class, "systemid")).ifPresent(resource::addTilknyttetRegistreringSom);

        return resource;
    }

    public Pair<String, RegistryEntryDocuments.Document> toDocumentDescription(DokumentbeskrivelseResource dokumentbeskrivelseResource) {
        DocumentDescriptionType documentDescriptionType = new DocumentDescriptionType();

        applyParameter(
                dokumentbeskrivelseResource.getTittel(),
                documentDescriptionType::setDocumentTitle
        );

        applyParameter(
                dokumentbeskrivelseResource.getOpprettetDato(),
                documentDescriptionType::setCreatedDate,
                xmlUtils::xmlDate
        );

        applyParameterFromLink(
                dokumentbeskrivelseResource.getOpprettetAv(),
                Integer::valueOf,
                documentDescriptionType::setCreatedByUserNameId
        );

        applyParameterFromLink(
                dokumentbeskrivelseResource.getDokumentstatus(),
                documentDescriptionType::setDocumentStatusId
        );

        applyParameterFromLink(
                dokumentbeskrivelseResource.getDokumentType(),
                documentDescriptionType::setDocumentCategoryId
        );

        RegistryEntryDocuments.Document document = new RegistryEntryDocuments.Document();
        document.setDocumentDescription(documentDescriptionType);
        document.setCheckinDocuments(
                dokumentbeskrivelseResource
                        .getDokumentobjekt()
                        .stream()
                        .flatMap(dokumentobjektFactory::toCheckinDocument)
                        .collect(Collectors.toList()));

        final String linkType = dokumentbeskrivelseResource
                .getTilknyttetRegistreringSom()
                .stream()
                .filter(Objects::nonNull)
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .findFirst()
                .orElse(null);

        return Pair.of(linkType, document);
    }

    public RegistryEntryDocumentType toRegistryEntryDocument(Integer registryEntryId, String linkType, Integer documentDescriptionId) {
        RegistryEntryDocumentType result = new RegistryEntryDocumentType();
        result.setRegistryEntryId(registryEntryId);
        result.setDocumentLinkTypeId(linkType);
        result.setDocumentDescriptionId(documentDescriptionId);
        return result;
    }
}
