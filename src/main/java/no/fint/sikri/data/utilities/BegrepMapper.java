package no.fint.sikri.data.utilities;

import no.fint.arkiv.sikri.oms.CaseStatusType;
import no.fint.arkiv.sikri.oms.DocumentCategoryType;
import no.fint.arkiv.sikri.oms.DocumentStatusType;
import no.fint.arkiv.sikri.oms.RegistryEntryTypeType;
import no.fint.model.resource.administrasjon.arkiv.DokumentStatusResource;
import no.fint.model.resource.administrasjon.arkiv.DokumentTypeResource;
import no.fint.model.resource.administrasjon.arkiv.JournalpostTypeResource;
import no.fint.model.resource.administrasjon.arkiv.SaksstatusResource;

public class BegrepMapper {

    public static SaksstatusResource mapSaksstatus(CaseStatusType caseStatusType) {
        SaksstatusResource saksstatusResource = new SaksstatusResource();

        saksstatusResource.setSystemId(FintUtils.createIdentifikator(caseStatusType.getId().getValue()));
        saksstatusResource.setKode(caseStatusType.getId().getValue());
        saksstatusResource.setNavn(caseStatusType.getDescription().getValue());

        return saksstatusResource;
    }

    public static DokumentStatusResource mapDokumentStatus(DocumentStatusType documentStatusType) {
        DokumentStatusResource dokumentStatusResource = new DokumentStatusResource();

        dokumentStatusResource.setSystemId(FintUtils.createIdentifikator(documentStatusType.getId().getValue()));
        dokumentStatusResource.setKode(documentStatusType.getId().getValue());
        dokumentStatusResource.setNavn(documentStatusType.getDescription().getValue());

        return dokumentStatusResource;
    }

    public static DokumentTypeResource mapDokumentType(DocumentCategoryType documentCategoryType) {
        DokumentTypeResource dokumentTypeResource = new DokumentTypeResource();

        dokumentTypeResource.setSystemId(FintUtils.createIdentifikator(documentCategoryType.getId().getValue()));
        dokumentTypeResource.setKode(documentCategoryType.getId().getValue());
        dokumentTypeResource.setNavn(documentCategoryType.getDescription().getValue());

        return dokumentTypeResource;
    }

    public static JournalpostTypeResource mapJournalpostType(RegistryEntryTypeType registryEntryTypeType) {

        JournalpostTypeResource journalpostTypeResource = new JournalpostTypeResource();

        journalpostTypeResource.setSystemId(FintUtils.createIdentifikator(registryEntryTypeType.getId().getValue()));
        journalpostTypeResource.setKode(registryEntryTypeType.getId().getValue());
        journalpostTypeResource.setNavn(registryEntryTypeType.getDescription().getValue());

        return journalpostTypeResource;

    }

}
