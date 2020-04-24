package no.fint.sikri.data.utilities;

import no.fint.model.resource.Link;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public enum SikriUtils {
    ;

    public static <T> void applyParameter(T value, Function<T, JAXBElement<T>> wrapper, Consumer<JAXBElement<T>> consumer) {
        applyParameter(value, wrapper, consumer, Function.identity());
    }

    public static <T, U> void applyParameter(T value, Function<U, JAXBElement<U>> wrapper, Consumer<JAXBElement<U>> consumer, Function<T, U> mapper) {
        if (value != null) {
            consumer.accept(wrapper.apply(mapper.apply(value)));
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

    public static <T> Optional<T> optionalValue(JAXBElement<T> element) {
        if (!element.isNil()) {
            return Optional.of(element.getValue());
        }
        return Optional.empty();
    }

    public static boolean notNil(JAXBElement<?> e) {
        return !e.isNil();
    }
}
