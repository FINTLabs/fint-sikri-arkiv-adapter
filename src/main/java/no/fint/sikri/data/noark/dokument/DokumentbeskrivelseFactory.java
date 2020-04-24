package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.DocumentDescriptionType;
import no.fint.arkiv.sikri.oms.ObjectFactory;
import no.fint.arkiv.sikri.oms.RegistryEntryDocumentType;
import no.fint.model.administrasjon.arkiv.Arkivressurs;
import no.fint.model.administrasjon.arkiv.DokumentStatus;
import no.fint.model.administrasjon.arkiv.DokumentType;
import no.fint.model.administrasjon.arkiv.TilknyttetRegistreringSom;
import no.fint.model.resource.Link;
import no.fint.model.resource.administrasjon.arkiv.DokumentbeskrivelseResource;
import no.fint.sikri.data.utilities.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

import static no.fint.sikri.data.utilities.SikriUtils.applyParameter;
import static no.fint.sikri.data.utilities.SikriUtils.applyParameterFromLink;

@Slf4j
@Service
public class DokumentbeskrivelseFactory {
    @Autowired
    private DokumentobjektService dokumentobjektService;

    @Autowired
    private XmlUtils xmlUtils;

    private ObjectFactory objectFactory = new ObjectFactory();

    public DokumentbeskrivelseResource toFintResource(RegistryEntryDocumentType result) {
        DokumentbeskrivelseResource resource = new DokumentbeskrivelseResource();

        resource.setTittel(result.getDocumentDescription().getValue().getDocumentTitle().getValue());
        resource.setDokumentnummer(Long.valueOf(result.getSortOrder()));
        resource.setOpprettetDato(result.getCreatedDate().getValue().toGregorianCalendar().getTime());
        resource.setForfatter(Collections.singletonList(result.getDocumentDescription().getValue().getCreatedByUserNameId().getValue().toString()));

        resource.addOpprettetAv(Link.with(Arkivressurs.class, "systemid", result.getDocumentDescription().getValue().getCreatedByUserNameId().getValue().toString()));
        resource.addDokumentstatus(Link.with(DokumentStatus.class, "systemid", result.getDocumentDescription().getValue().getDocumentStatusId().getValue()));
        resource.addTilknyttetRegistreringSom(Link.with(TilknyttetRegistreringSom.class, "systemid", result.getDocumentLinkTypeId().getValue()));
        resource.addDokumentType(Link.with(DokumentType.class, "systemid", result.getDocumentDescription().getValue().getDocumentCategoryId().getValue()));

        resource.setDokumentobjekt(dokumentobjektService.queryDokumentobjekt(result.getDocumentDescriptionId().toString()));

        return resource;
    }

    public Pair<String, DocumentDescriptionType> toDocumentDescription(DokumentbeskrivelseResource dokumentbeskrivelseResource) {
        DocumentDescriptionType result = objectFactory.createDocumentDescriptionType();

        applyParameter(
                dokumentbeskrivelseResource.getTittel(),
                objectFactory::createDocumentDescriptionTypeDocumentTitle,
                result::setDocumentTitle
        );

        applyParameter(
                dokumentbeskrivelseResource.getOpprettetDato(),
                objectFactory::createDocumentDescriptionTypeCreatedDate,
                result::setCreatedDate,
                xmlUtils::xmlDate
        );

        applyParameterFromLink(
                dokumentbeskrivelseResource.getOpprettetAv(),
                v -> objectFactory.createDocumentDescriptionTypeCreatedByUserNameId(Integer.valueOf(v)),
                result::setCreatedByUserNameId
        );

        applyParameterFromLink(
                dokumentbeskrivelseResource.getDokumentstatus(),
                objectFactory::createDocumentDescriptionTypeDocumentStatusId,
                result::setDocumentStatusId
        );

        applyParameterFromLink(
                dokumentbeskrivelseResource.getDokumentType(),
                objectFactory::createDocumentDescriptionTypeDocumentCategoryId,
                result::setDocumentCategoryId
        );

        final String linkType = dokumentbeskrivelseResource
                .getTilknyttetRegistreringSom()
                .stream()
                .filter(Objects::nonNull)
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .findFirst()
                .orElse(null);

        return Pair.of(linkType, result);
    }

    public RegistryEntryDocumentType toRegistryEntryDocument(Integer registryEntryId, String linkType, Integer documentDescriptionId) {
        RegistryEntryDocumentType result = objectFactory.createRegistryEntryDocumentType();
        result.setRegistryEntryId(registryEntryId);
        result.setDocumentLinkTypeId(objectFactory.createRegistryEntryDocumentTypeDocumentLinkTypeId(linkType));
        result.setDocumentDescriptionId(documentDescriptionId);
        return result;
    }
}
