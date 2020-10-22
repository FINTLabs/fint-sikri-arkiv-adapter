package no.fint.sikri.data.noark.skjerming;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.arkiv.kodeverk.Skjermingshjemmel;
import no.fint.model.arkiv.kodeverk.Tilgangsrestriksjon;
import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.resource.Link;
import no.fint.model.resource.arkiv.kodeverk.SkjermingshjemmelResource;
import no.fint.model.resource.arkiv.noark.SkjermingResource;
import no.fint.sikri.repository.KodeverkRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static no.fint.sikri.data.utilities.SikriUtils.*;

@Service
@Slf4j
public class SkjermingService {
    private final KodeverkRepository kodeverkRepository;

    public SkjermingService(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
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

    public void applyAccessCodeAndPursuant(SkjermingResource skjerming, Consumer<String> accessCodeConsumer, Consumer<String> pursuantConsumer) {
        optionalValue(skjerming)
                .ifPresent(s -> {
                    applyParameterFromLink(s.getTilgangsrestriksjon(), accessCodeConsumer);
                    getLinkTargets(s.getSkjermingshjemmel())
                            .flatMap(id -> kodeverkRepository
                                    .getSkjermingshjemmel()
                                    .stream()
                                    .filter(v -> v.getSystemId().getIdentifikatorverdi().equals(id)))
                            .map(SkjermingshjemmelResource::getNavn)
                            .findFirst()
                            .ifPresent(pursuantConsumer);
                });
    }

}
