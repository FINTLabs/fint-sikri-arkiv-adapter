package no.fint.sikri.data.utilities;

import no.fint.model.resource.Link;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public enum SikriUtils {
    ;

    public static <T> void applyParameter(T value, Consumer<T> consumer) {
        applyParameter(value, consumer, Function.identity());
    }

    public static <T, U> void applyParameter(T value, Consumer<U> consumer, Function<T, U> mapper) {
        if (value != null) {
            consumer.accept(mapper.apply(value));
        }
    }

    public static <T> void applyParameterFromLink(List<Link> links, Function<String, T> mapper, Consumer<T> consumer) {
        links.stream()
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .map(mapper)
                .findFirst()
                .ifPresent(consumer);
    }

    public static void applyParameterFromLink(List<Link> links, Consumer<String> consumer) {
        links.stream()
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .findFirst()
                .ifPresent(consumer);
    }

    public static Optional<String> optionalValue(String string) {
        if (StringUtils.isBlank(string)) {
            return Optional.empty();
        }
        return Optional.of(string);
    }

    public static <T> Optional<T> optionalValue(T object) {
        return Optional.ofNullable(object);
    }

    public static <T, U> Function<U, Optional<T>> optionalValueFn(Function<U, T> function) {
        return f -> optionalValue(function.apply(f));
    }

    public static boolean notNil(JAXBElement<?> e) {
        return !e.isNil();
    }
}
