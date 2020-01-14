package no.fint.documaster.data.utilities;

import no.documaster.model.Value;
import no.fint.model.felles.basisklasser.Begrep;

import java.util.function.Function;
import java.util.function.Supplier;

public class BegrepMapper {
    public static <T extends Begrep> Function<Value,T> mapValue(Supplier<T> constructor) {
        return value -> {
            T result = constructor.get();
            result.setSystemId(FintUtils.createIdentifikator(value.getCode()));
            result.setKode(value.getCode());
            result.setNavn(value.getName());
            return result;
        };
    }
}
