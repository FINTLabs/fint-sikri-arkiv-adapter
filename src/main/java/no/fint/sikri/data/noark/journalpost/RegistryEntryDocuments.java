package no.fint.sikri.data.noark.journalpost;

import lombok.Data;
import no.fint.arkiv.sikri.oms.DocumentDescriptionType;
import no.fint.arkiv.sikri.oms.RegistryEntryType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Data
public class RegistryEntryDocuments {
    private final RegistryEntryType registryEntry;
    private final List<Pair<String,DocumentDescriptionType>> documents = new ArrayList<>();

    public void addDocument(Pair<String,DocumentDescriptionType> document) {
        documents.add(document);
    }
}
