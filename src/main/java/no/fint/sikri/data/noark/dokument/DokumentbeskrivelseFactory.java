package no.fint.sikri.data.noark.dokument;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.sikri.oms.RegistryEntryDocumentType;
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


    public DokumentbeskrivelseResource toFintResource(RegistryEntryDocumentType result) {
        DokumentbeskrivelseResource resource = new DokumentbeskrivelseResource();

        resource.setTittel(result.getDocumentDescription().getValue().getDocumentTitle().getValue());
        resource.setDokumentnummer(Long.valueOf(result.getSortOrder()));
        //resource.setBeskrivelse(result.getFields().getBeskrivelse());
        resource.setOpprettetDato(result.getCreatedDate().getValue().toGregorianCalendar().getTime());
        resource.setForfatter(Collections.singletonList(result.getDocumentDescription().getValue().getCreatedByUserNameId().getValue().toString()));

        resource.addOpprettetAv(Link.with(Arkivressurs.class, "systemid", result.getDocumentDescription().getValue().getCreatedByUserNameId().getValue().toString()));
        resource.addDokumentstatus(Link.with(DokumentStatus.class, "systemid", result.getDocumentDescription().getValue().getDocumentStatusId().getValue()));
        resource.addTilknyttetRegistreringSom(Link.with(TilknyttetRegistreringSom.class, "systemid", result.getDocumentLinkTypeId().getValue()));
        resource.addDokumentType(Link.with(DokumentType.class, "systemid", result.getDocumentDescription().getValue().getDocumentCategoryId().getValue()));

        resource.setDokumentobjekt(dokumentobjektService.queryDokumentobjekt(result.getDocumentDescriptionId().toString()));

        return resource;
    }
/*
    public QueryInput createQueryInput(String id) {
        return QueryUtils.createQueryInput("Dokument", "refRegistrering.id", id);
    }

     */
}
