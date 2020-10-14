package no.fint.sikri.handler.noark;

import lombok.extern.slf4j.Slf4j;
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

import static no.fint.model.arkiv.kodeverk.KodeverkActions.*;

@Service
@Slf4j
public class KodeverkHandler implements Handler {

    @Autowired
    private KodeverkRepository kodeverkRepository;

    private final EnumMap<KodeverkActions, Supplier<List<? extends FintLinks>>> actionsMap = new EnumMap<>(KodeverkActions.class);

    @PostConstruct
    public void init() {
        actionsMap.put(GET_ALL_DOKUMENTSTATUS, kodeverkRepository::getDokumentStatus);
        actionsMap.put(GET_ALL_DOKUMENTTYPE, kodeverkRepository::getDokumentType);
        actionsMap.put(GET_ALL_JOURNALPOSTTYPE, kodeverkRepository::getJournalpostType);
        actionsMap.put(GET_ALL_JOURNALSTATUS, kodeverkRepository::getJournalStatus);
        //actionsMap.put(GET_ALL_KORRESPONDANSEPARTTYPE, kodeverkRepository::getKorrespondansepartType);
        actionsMap.put(GET_ALL_MERKNADSTYPE, kodeverkRepository::getMerknadstype);
        actionsMap.put(GET_ALL_PARTROLLE, kodeverkRepository::getPartRolle);
        actionsMap.put(GET_ALL_SAKSSTATUS, kodeverkRepository::getSaksstatus);
        //actionsMap.put(GET_ALL_SKJERMINGSHJEMMEL, kodeverkRepository::getSkjermingshjemmel);
        actionsMap.put(GET_ALL_TILGANGSRESTRIKSJON, kodeverkRepository::getTilgangsrestriksjon);
        actionsMap.put(GET_ALL_TILKNYTTETREGISTRERINGSOM, kodeverkRepository::getTilknyttetRegistreringSom);
        actionsMap.put(GET_ALL_VARIANTFORMAT, kodeverkRepository::getVariantformat);

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
        actionsMap.getOrDefault(KodeverkActions.valueOf(response.getAction()), Collections::emptyList)
                .get()
                .forEach(response::addData);
    }

    @Override
    public Set<String> actions() {
        return actionsMap.keySet().stream().map(Enum::name).collect(Collectors.toSet());
    }

    @Override
    public boolean health() {
        return kodeverkRepository.health();
    }

}
