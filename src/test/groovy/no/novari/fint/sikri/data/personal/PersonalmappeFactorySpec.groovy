package no.novari.fint.sikri.data.personal

import no.novari.fint.arkiv.sikri.oms.AdministrativeUnitType
import no.novari.fint.arkiv.sikri.oms.CaseType
import no.novari.fint.arkiv.sikri.oms.ClassificationType
import no.novari.fint.arkiv.sikri.oms.UserNameType
import no.novari.fint.sikri.data.noark.common.NoarkFactory
import no.novari.fint.sikri.data.personal.PersonalmappeFactory
import no.novari.fint.sikri.service.SikriCaseDefaultsService
import no.novari.fint.sikri.service.SikriIdentityService
import no.novari.fint.sikri.service.SikriObjectModelService
import no.novari.fint.arkiv.CaseDefaults
import no.novari.fint.model.resource.arkiv.personal.PersonalmappeResource
import spock.lang.Specification

class PersonalmappeFactorySpec extends Specification {

    def "Should return a valid PersonalmappeResource from a complete example"() {

        given:
        CaseType caseType = new CaseType(
                dataObjectId: "i1",
                accessCodeId: "P",
                administrativeUnit: new AdministrativeUnitType(
                        dataObjectId: "i2",
                        breadCrumb: "?1?-?24?-?25?-?40?-?292?",
                        description: "VGNES Idrettsfag",
                        id: 292,
                        parentalUnitId: 40,
                        registryManagementUnitId: "SP",
                        shortCode: "VGNES IDRETT",
                        shortCodeThisLevel: "394"
                ),
                administrativeUnitId: 292,
                caseStatusId: "B",
                caseYear: 2024,
                countOfRegistryEntries: 5,
                createdByUserNameId: 406,
                disposalCodeId: "B",
                fileOpen: false,
                fileTypeId: "PE",
                id: 84757,
                isPhysical: false,
                isPublished: true,
                isRestricted: true,
                loanedToUserNameId: 0,
                officerName: new UserNameType(
                        dataObjectId: "i3",
                        firstName: "Nils",
                        id: 2668,
                        initials: "LARLAR_X",
                        isCurrent: true,
                        lastName: "Nilsen",
                        name: "Nils Nilsen",
                        userId: 1354
                ),
                officerNameId: 2668,
                publicTitle: "Personalmappe - Larsen Lars",
                publicTitleNames: "Personalmappe - ##### ####_",
                pursuant: "Offl. § 13 jf. Fvl. § 13 1. ledd",
                registryManagementUnitId: "SP",
                sequenceNumber: 35335,
                seriesId: "PERS",
                title: "Personalmappe - Larsen Lars",
                primaryClassification: new ClassificationType(
                        classId: "24069411111",
                        description: "Lars Larsen"
                )
        )

        when:
        NoarkFactory noarkFactory = Mock(NoarkFactory)

        1 * noarkFactory.applyValuesForSaksmappe(_, _, _, _) >> new PersonalmappeResource()

        PersonalmappeFactory personalmappeFactory = new PersonalmappeFactory(
                identityService: Mock(SikriIdentityService),
                noarkFactory: noarkFactory,
                sikriObjectModelService: Mock(SikriObjectModelService),
                caseDefaultsService: Mock(SikriCaseDefaultsService),
                caseDefaults: Mock(CaseDefaults)

        )
        def resource = personalmappeFactory.toFintResource(caseType)

        then:
        resource.getArbeidssted().get(0).getHref() == "\${administrasjon.organisasjon.organisasjonselement}/systemid/394"
        resource.getPerson().get(0).getHref() == "\${felles.person}/fodselsnummer/24069411111"

    }

    def "Should get empty person when primary classification is missing"() {
        given:
        CaseType caseType = new CaseType(
                dataObjectId: "i1",
                accessCodeId: "P",
                administrativeUnit: new AdministrativeUnitType(
                        dataObjectId: "i2",
                        breadCrumb: "?1?-?24?-?25?-?40?-?292?",
                        description: "VGNES Idrettsfag",
                        id: 292,
                        parentalUnitId: 40,
                        registryManagementUnitId: "SP",
                        shortCode: "VGNES IDRETT",
                        shortCodeThisLevel: "394"
                ),
                administrativeUnitId: 292,
                caseStatusId: "B",
                caseYear: 2024,
                countOfRegistryEntries: 5,
                createdByUserNameId: 406,
                disposalCodeId: "B",
                fileOpen: false,
                fileTypeId: "PE",
                id: 84757,
                isPhysical: false,
                isPublished: true,
                isRestricted: true,
                loanedToUserNameId: 0,
                officerName: new UserNameType(
                        dataObjectId: "i3",
                        firstName: "Nils",
                        id: 2668,
                        initials: "LARLAR_X",
                        isCurrent: true,
                        lastName: "Nilsen",
                        name: "Nils Nilsen",
                        userId: 1354
                ),
                officerNameId: 2668,
                publicTitle: "Personalmappe - Larsen Lars",
                publicTitleNames: "Personalmappe - ##### ####_",
                pursuant: "Offl. § 13 jf. Fvl. § 13 1. ledd",
                registryManagementUnitId: "SP",
                sequenceNumber: 35335,
                seriesId: "PERS",
                title: "Personalmappe - Larsen Lars"
        )

        when:
        NoarkFactory noarkFactory = Mock(NoarkFactory)

        1 * noarkFactory.applyValuesForSaksmappe(_, _, _, _) >> new PersonalmappeResource()

        PersonalmappeFactory personalmappeFactory = new PersonalmappeFactory(
                identityService: Mock(SikriIdentityService),
                noarkFactory: noarkFactory,
                sikriObjectModelService: Mock(SikriObjectModelService),
                caseDefaultsService: Mock(SikriCaseDefaultsService),
                caseDefaults: Mock(CaseDefaults)

        )
        def resource = personalmappeFactory.toFintResource(caseType)

        then:
        resource.getArbeidssted().get(0).getHref() == "\${administrasjon.organisasjon.organisasjonselement}/systemid/394"
        resource.getPerson().size() == 0 // ingen person fra primærklassifisering
    }

}
