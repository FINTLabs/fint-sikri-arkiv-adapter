package no.fint.documaster.data.exception;

public class NoSuchTitleDimension extends Exception {
    public NoSuchTitleDimension(Integer dimension) {
        super("No such dimension: " + dimension);
    }
}
