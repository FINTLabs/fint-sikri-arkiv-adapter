package no.fint.sikri.data.noark.dokument;

import lombok.Data;

@Data
public class CheckinDocument {
    private String variant;
    private int version;
    private int documentId;
    private String guid;
}
