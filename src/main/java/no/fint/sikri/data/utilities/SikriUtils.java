package no.fint.sikri.data.utilities;

import no.fint.model.resource.Link;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public enum SikriUtils {
    ;

    public static <T> void applyParameterFromLink(List<Link> links, Function<String, T> mapper, Consumer<T> consumer) {
        links.stream()
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"))
                .map(mapper)
                .findFirst()
                .ifPresent(consumer);
    }
}
