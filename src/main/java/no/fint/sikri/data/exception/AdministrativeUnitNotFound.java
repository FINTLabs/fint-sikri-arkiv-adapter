package no.fint.sikri.data.exception;

public class AdministrativeUnitNotFound extends RuntimeException {
    public AdministrativeUnitNotFound(String errorMessage) {
        super(errorMessage);
    }
}
