package no.novari.fint.sikri.data.noark.skjerming;

import lombok.extern.slf4j.Slf4j;
import no.novari.fint.arkiv.NoarkMetadataService;
import no.novari.fint.model.arkiv.kodeverk.Skjermingshjemmel;
import no.novari.fint.model.arkiv.kodeverk.Tilgangsrestriksjon;
import no.novari.fint.model.felles.basisklasser.Begrep;
import no.novari.fint.model.felles.kompleksedatatyper.Identifikator;
import no.novari.fint.model.resource.Link;
import no.novari.fint.model.resource.arkiv.kodeverk.SkjermingshjemmelResource;
import no.novari.fint.model.resource.arkiv.noark.SkjermingResource;
import no.novari.fint.sikri.data.utilities.FintUtils;
import no.novari.fint.sikri.data.utilities.SikriUtils;
import no.novari.fint.sikri.repository.KodeverkRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static no.novari.fint.sikri.data.utilities.SikriUtils.*;

@Service
@Slf4j
public class SkjermingService {
    private final KodeverkRepository kodeverkRepository;
    private final NoarkMetadataService noarkMetadataService;

    public SkjermingService(KodeverkRepository kodeverkRepository, NoarkMetadataService noarkMetadataService) {
        this.kodeverkRepository = kodeverkRepository;
        this.noarkMetadataService = noarkMetadataService;
    }


    public SkjermingResource getSkjermingResource(Supplier<String> accessCodeSupplier, Supplier<String> pursuantSupplier) {
        SkjermingResource skjerming = new SkjermingResource();
        optionalValue(accessCodeSupplier.get())
                .map(Link.apply(Tilgangsrestriksjon.class, "systemid"))
                .ifPresent(skjerming::addTilgangsrestriksjon);
        optionalValue(pursuantSupplier.get())
                .flatMap(pursuant ->
                        kodeverkRepository.getSkjermingshjemmel()
                                .stream()
                                .filter(it -> it.getNavn().equals(pursuant))
                                .map(SkjermingshjemmelResource::getSystemId)
                                .map(Identifikator::getIdentifikatorverdi)
                                .filter(it -> StringUtils.startsWith(it, accessCodeSupplier.get()))
                                .map(Link.apply(Skjermingshjemmel.class, "systemid"))
                                .findAny())
                .ifPresent(skjerming::addSkjermingshjemmel);
        if (!skjerming.equals(new SkjermingResource())) {
            return skjerming;
        }
        return null;
    }

    public boolean applyAccessCodeAndPursuant(SkjermingResource skjerming, Consumer<String> accessCodeConsumer, Consumer<String> pursuantConsumer) {
        return optionalValue(skjerming)
                .map(s ->
                        applyParameterFromLink(s.getTilgangsrestriksjon(), accessCodeConsumer)
                                &&
                                getLinkTargets(s.getSkjermingshjemmel())
                                        .flatMap(id ->
                                                Stream.concat(
                                                        Stream.concat(
                                                                // First, try finding pursuant from Sikri codes
                                                                kodeverkRepository.getSkjermingshjemmel().stream(),
                                                                // Second, try finding in Noark Skjermingshjemmel
                                                                noarkMetadataService.getSkjermingshjemmel())
                                                                .filter(it -> it.getSystemId().getIdentifikatorverdi().equals(id)),
                                                        getLinkTargets(s.getTilgangsrestriksjon())
                                                                // Third, try finding in Noark Tilgangsrestriksjon
                                                                .flatMap(acc -> noarkMetadataService.getTilgangsrestriksjon().filter(it -> it.getKode().equals(acc)))
                                                ))
                                        .map(Begrep::getNavn)
                                        .filter(StringUtils::isNotBlank)
                                        .findFirst()
                                        .map(SikriUtils.success(pursuantConsumer))
                                        .orElse(false))
                .orElse(false);
    }

    public static boolean hasTilgangsrestriksjon(SkjermingResource skjermingResource) {
        if (skjermingResource == null || skjermingResource.getTilgangsrestriksjon() == null) {
            return false;
        }
        return FintUtils.getIdFromLink(skjermingResource.getTilgangsrestriksjon()).isPresent();
    }

}
