package no.fint.sikri.data.noark.journalpost;

import lombok.Data;
import no.fint.arkiv.sikri.oms.DocumentDescriptionType;
import no.fint.arkiv.sikri.oms.RegistryEntryType;
import no.fint.sikri.data.noark.dokument.CheckinDocument;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Data
public class RegistryEntryDocuments {
    private final RegistryEntryType registryEntry;
    private final List<Pair<String,Document>> documents = new ArrayList<>();

    public void addDocument(Pair<String,Document> document) {
        documents.add(document);
    }

    @Data
    public static class Document {
        private DocumentDescriptionType documentDescription;
        private List<CheckinDocument> checkinDocuments;
    }
}
