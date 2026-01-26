package no.novari.fint.sikri.data.utilities;

import no.fint.arkiv.sikri.oms.*;
import no.novari.fint.model.administrasjon.organisasjon.Organisasjonselement;
import no.novari.fint.model.arkiv.noark.Klassifikasjonssystem;
import no.novari.fint.model.resource.Link;
import no.novari.fint.model.resource.arkiv.kodeverk.*;
import no.novari.fint.model.resource.arkiv.noark.AdministrativEnhetResource;
import no.novari.fint.model.resource.arkiv.noark.ArkivdelResource;

public class BegrepMapper {


    public static SaksstatusResource mapSaksstatus(CaseStatusType caseStatusType) {
        SaksstatusResource saksstatusResource = new SaksstatusResource();

        saksstatusResource.setSystemId(FintUtils.createIdentifikator(caseStatusType.getId()));
        saksstatusResource.setKode(caseStatusType.getId());
        saksstatusResource.setNavn(caseStatusType.getDescription());

        return saksstatusResource;
    }

    public static DokumentStatusResource mapDokumentStatus(DocumentStatusType documentStatusType) {
        DokumentStatusResource dokumentStatusResource = new DokumentStatusResource();

        dokumentStatusResource.setSystemId(FintUtils.createIdentifikator(documentStatusType.getId()));
        dokumentStatusResource.setKode(documentStatusType.getId());
        dokumentStatusResource.setNavn(documentStatusType.getDescription());

        return dokumentStatusResource;
    }

    public static AdministrativEnhetResource mapAdministrativEnhet(AdministrativeUnitType administrativeUnitType) {
        AdministrativEnhetResource administrativEnhetResource = new AdministrativEnhetResource();

        //administrativEnhetResource.setKode(administrativeUnitType.getShortCode());
        administrativEnhetResource.setNavn(administrativeUnitType.getDescription());
        administrativEnhetResource.setSystemId(FintUtils.createIdentifikator(String.valueOf(administrativeUnitType.getId())));
        administrativEnhetResource.addOrganisasjonselement(Link.with(Organisasjonselement.class, "organisasjonsId", administrativeUnitType.getShortCodeThisLevel()));

        return administrativEnhetResource;
    }

    public static DokumentTypeResource mapDokumentType(DocumentCategoryType documentCategoryType) {
        DokumentTypeResource dokumentTypeResource = new DokumentTypeResource();

        dokumentTypeResource.setSystemId(FintUtils.createIdentifikator(documentCategoryType.getId()));
        dokumentTypeResource.setKode(documentCategoryType.getId());
        dokumentTypeResource.setNavn(documentCategoryType.getDescription());

        return dokumentTypeResource;
    }

    public static JournalpostTypeResource mapJournalpostType(RegistryEntryTypeType registryEntryTypeType) {

        JournalpostTypeResource journalpostTypeResource = new JournalpostTypeResource();

        journalpostTypeResource.setSystemId(FintUtils.createIdentifikator(registryEntryTypeType.getId()));
        journalpostTypeResource.setKode(registryEntryTypeType.getId());
        journalpostTypeResource.setNavn(registryEntryTypeType.getDescription());

        return journalpostTypeResource;

    }

    public static JournalStatusResource mapJournalStatus(RecordsStatusType recordsStatusType) {
        JournalStatusResource journalStatusResource = new JournalStatusResource();

        journalStatusResource.setSystemId(FintUtils.createIdentifikator(recordsStatusType.getId()));
        journalStatusResource.setKode(recordsStatusType.getId());
        journalStatusResource.setNavn(recordsStatusType.getDescription());

        return journalStatusResource;
    }

    public static TilknyttetRegistreringSomResource mapTilknyttetTegistreringSom(DocumentLinkTypeType documentLinkTypeType) {
        TilknyttetRegistreringSomResource tilknyttetRegistreringSomResource = new TilknyttetRegistreringSomResource();

        tilknyttetRegistreringSomResource.setSystemId(FintUtils.createIdentifikator(documentLinkTypeType.getId()));
        tilknyttetRegistreringSomResource.setKode(documentLinkTypeType.getId());
        tilknyttetRegistreringSomResource.setNavn(documentLinkTypeType.getDescription());

        return tilknyttetRegistreringSomResource;
    }

    public static PartRolleResource mapPartRolle(CasePartyRoleType casePartyRoleType) {
        PartRolleResource partRolleResource = new PartRolleResource();

        partRolleResource.setSystemId(FintUtils.createIdentifikator(casePartyRoleType.getId()));
        partRolleResource.setKode(casePartyRoleType.getId());
        partRolleResource.setNavn(casePartyRoleType.getDescription());

        return partRolleResource;
    }

    public static MerknadstypeResource mapMerkandstype(InformationTypeType informationTypeType) {
        MerknadstypeResource merknadstypeResource = new MerknadstypeResource();

        merknadstypeResource.setSystemId(FintUtils.createIdentifikator(informationTypeType.getId()));
        merknadstypeResource.setKode(informationTypeType.getId());
        merknadstypeResource.setNavn(informationTypeType.getDescription());

        return merknadstypeResource;
    }

    public static TilgangsrestriksjonResource mapTilgangsrestriksjon(AccessCodeType accessCodeType) {
        TilgangsrestriksjonResource tilgangsrestriksjonResource = new TilgangsrestriksjonResource();

        tilgangsrestriksjonResource.setSystemId(FintUtils.createIdentifikator(accessCodeType.getId()));
        tilgangsrestriksjonResource.setKode(accessCodeType.getId());
        tilgangsrestriksjonResource.setNavn(accessCodeType.getDescription());

        return tilgangsrestriksjonResource;
    }

    public static VariantformatResource mapVariantFormat(VariantFormatType variantFormatType) {
        VariantformatResource variantformatResource = new VariantformatResource();

        variantformatResource.setSystemId(FintUtils.createIdentifikator(variantFormatType.getId()));
        variantformatResource.setKode(variantFormatType.getId());
        variantformatResource.setNavn(variantFormatType.getDescription());

        return variantformatResource;
    }

    public static FormatResource mapFormat(FileFormatType input) {
        FormatResource output = new FormatResource();

        output.setSystemId(FintUtils.createIdentifikator(input.getId()));
        output.setKode(input.getId());
        output.setNavn(input.getDescription());

        return output;
    }

    public static ArkivdelResource mapArkivdel(SeriesType seriesType) {
        ArkivdelResource resource = new ArkivdelResource();
        resource.setSystemId(FintUtils.createIdentifikator(seriesType.getId()));
        resource.setTittel(seriesType.getDescription());
        resource.addKlassifikasjonssystem(Link.with(Klassifikasjonssystem.class, "systemid", seriesType.getPrimaryClassificationSystemId()));
        return resource;
    }

    public static SaksmappetypeResource mapSaksmappetype(FileTypeType fileTypeType) {
        SaksmappetypeResource resource = new SaksmappetypeResource();
        resource.setSystemId(FintUtils.createIdentifikator(fileTypeType.getId()));
        resource.setKode(fileTypeType.getId());
        resource.setNavn(fileTypeType.getDescription());
        return resource;
    }
}
