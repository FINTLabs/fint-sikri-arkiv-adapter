package no.fint.sikri.data.utilities;

import no.fint.arkiv.sikri.oms.*;
import no.fint.model.administrasjon.organisasjon.Organisasjonselement;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.noark.*;

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

    public static AdministrativEnhetResource mapAdministrativEnhet(AdministrativeUnitType administrativeUnitType) {
        AdministrativEnhetResource administrativEnhetResource = new AdministrativEnhetResource();

        administrativEnhetResource.setNavn(administrativeUnitType.getDescription().getValue());
        administrativEnhetResource.setSystemId(FintUtils.createIdentifikator(administrativeUnitType.getShortCodeThisLevel().getValue()));
        administrativEnhetResource.addOrganisasjonselement(Link.with(Organisasjonselement.class, "organisasjonsId", administrativeUnitType.getShortCodeThisLevel().getValue()));

        return administrativEnhetResource;
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

    public static JournalStatusResource mapJournalStatus(RecordsStatusType recordsStatusType) {
        JournalStatusResource journalStatusResource = new JournalStatusResource();

        journalStatusResource.setSystemId(FintUtils.createIdentifikator(recordsStatusType.getId().getValue()));
        journalStatusResource.setKode(recordsStatusType.getId().getValue());
        journalStatusResource.setNavn(recordsStatusType.getDescription().getValue());

        return journalStatusResource;
    }

    public static TilknyttetRegistreringSomResource mapTilknyttetTegistreringSom(DocumentLinkTypeType documentLinkTypeType) {
        TilknyttetRegistreringSomResource tilknyttetRegistreringSomResource = new TilknyttetRegistreringSomResource();

        tilknyttetRegistreringSomResource.setSystemId(FintUtils.createIdentifikator(documentLinkTypeType.getId().getValue()));
        tilknyttetRegistreringSomResource.setKode(documentLinkTypeType.getId().getValue());
        tilknyttetRegistreringSomResource.setNavn(documentLinkTypeType.getDescription().getValue());

        return tilknyttetRegistreringSomResource;
    }

    public static PartRolleResource mapPartRolle(CasePartyRoleType casePartyRoleType) {
        PartRolleResource partRolleResource = new PartRolleResource();

        partRolleResource.setSystemId(FintUtils.createIdentifikator(casePartyRoleType.getId().getValue()));
        partRolleResource.setKode(casePartyRoleType.getId().getValue());
        partRolleResource.setNavn(casePartyRoleType.getDescription().getValue());

        return partRolleResource;
    }

    public static MerknadstypeResource mapMerkandstype(InformationTypeType informationTypeType) {
        MerknadstypeResource merknadstypeResource = new MerknadstypeResource();

        merknadstypeResource.setSystemId(FintUtils.createIdentifikator(informationTypeType.getId().getValue()));
        merknadstypeResource.setKode(informationTypeType.getId().getValue());
        merknadstypeResource.setNavn(informationTypeType.getDescription().getValue());

        return merknadstypeResource;
    }

    public static TilgangsrestriksjonResource mapTilgangsrestriksjon(AccessCodeType accessCodeType) {
        TilgangsrestriksjonResource tilgangsrestriksjonResource = new TilgangsrestriksjonResource();

        tilgangsrestriksjonResource.setSystemId(FintUtils.createIdentifikator(accessCodeType.getId().getValue()));
        tilgangsrestriksjonResource.setKode(accessCodeType.getId().getValue());
        tilgangsrestriksjonResource.setNavn(accessCodeType.getDescription().getValue());

        return tilgangsrestriksjonResource;
    }

    public static VariantformatResource mapVariantFormat(VariantFormatType variantFormatType) {
        VariantformatResource variantformatResource = new VariantformatResource();

        variantformatResource.setSystemId(FintUtils.createIdentifikator(variantFormatType.getId().getValue()));
        variantformatResource.setKode(variantFormatType.getId().getValue());
        variantformatResource.setNavn(variantFormatType.getDescription().getValue());

        return variantformatResource;
    }
}
