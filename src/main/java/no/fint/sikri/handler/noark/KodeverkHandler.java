package no.fint.sikri.handler.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.arkiv.NoarkMetadataService;
import no.fint.event.model.Event;
import no.fint.event.model.ResponseStatus;
import no.fint.event.model.Status;
import no.fint.model.arkiv.kodeverk.KodeverkActions;
import no.fint.model.resource.FintLinks;
import no.fint.sikri.handler.Handler;
import no.fint.sikri.repository.KodeverkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static no.fint.model.arkiv.kodeverk.KodeverkActions.*;

@Service
@Slf4j
public class KodeverkHandler implements Handler {
    @Autowired
    private NoarkMetadataService noarkMetadataService;

    @Autowired
    private KodeverkRepository kodeverkRepository;

    private final EnumMap<KodeverkActions, Supplier<List<? extends FintLinks>>> suppliers = new EnumMap<>(KodeverkActions.class);

    @PostConstruct
    public void init() {
        suppliers.put(GET_ALL_DOKUMENTSTATUS, merge(noarkMetadataService::getDokumentStatus, kodeverkRepository::getDokumentStatus));
        suppliers.put(GET_ALL_DOKUMENTTYPE, merge(noarkMetadataService::getDokumentType, kodeverkRepository::getDokumentType));
        suppliers.put(GET_ALL_JOURNALPOSTTYPE, merge(noarkMetadataService::getJournalpostType, kodeverkRepository::getJournalpostType));
        suppliers.put(GET_ALL_JOURNALSTATUS, merge(noarkMetadataService::getJournalStatus, kodeverkRepository::getJournalStatus));
        suppliers.put(GET_ALL_KORRESPONDANSEPARTTYPE, merge(noarkMetadataService::getKorrespondansepartType, kodeverkRepository::getKorrespondansepartType));
        suppliers.put(GET_ALL_MERKNADSTYPE, kodeverkRepository::getMerknadstype);
        suppliers.put(GET_ALL_PARTROLLE, merge(noarkMetadataService::getPartRolle, kodeverkRepository::getPartRolle));
        suppliers.put(GET_ALL_SAKSSTATUS, merge(noarkMetadataService::getSaksStatus, kodeverkRepository::getSaksstatus));
        suppliers.put(GET_ALL_SKJERMINGSHJEMMEL, merge(noarkMetadataService::getSkjermingshjemmel, kodeverkRepository::getSkjermingshjemmel));
        suppliers.put(GET_ALL_TILGANGSRESTRIKSJON, merge(noarkMetadataService::getTilgangsrestriksjon, kodeverkRepository::getTilgangsrestriksjon));
        suppliers.put(GET_ALL_TILKNYTTETREGISTRERINGSOM, merge(noarkMetadataService::getTilknyttetRegistreringSom, kodeverkRepository::getTilknyttetRegistreringSom));
        suppliers.put(GET_ALL_VARIANTFORMAT, merge(noarkMetadataService::getVariantformat, kodeverkRepository::getVariantformat));

        // TODO actionsMap.put(GET_ALL_KLASSIFIKASJONSSYSTEM, kodeverkRepository::getKlassifikasjonssystem);
        // TODO actionsMap.put(GET_ALL_KLASSE, kodeverkRepository::getKlasse);
        // TODO actionsMap.put(GET_ALL_ADMINISTRATIVENHET, kodeverkRepository::getAdministrativEnhet);
    }


    @Override
    public void accept(Event<FintLinks> response) {
        if (!health()) {
            response.setStatus(Status.ADAPTER_REJECTED);
            response.setMessage("Health test failed");
            return;
        }
        response.setResponseStatus(ResponseStatus.ACCEPTED);
        suppliers.getOrDefault(KodeverkActions.valueOf(response.getAction()), Collections::emptyList)
                .get()
                .forEach(response::addData);
    }

    public List<? extends FintLinks> getCodes(KodeverkActions name) {
        return suppliers.get(name).get();
    }

    @Override
    public Set<String> actions() {
        return suppliers.keySet().stream().map(Enum::name).collect(Collectors.toSet());
    }

    @Override
    public boolean health() {
        return kodeverkRepository.health();
    }

    private Supplier<List<? extends FintLinks>> merge(Supplier<Stream<? extends FintLinks>> common, Supplier<List<? extends FintLinks>> local) {
        return () -> Stream.concat(common.get(), local.get().stream()).collect(Collectors.toList());
    }

}
