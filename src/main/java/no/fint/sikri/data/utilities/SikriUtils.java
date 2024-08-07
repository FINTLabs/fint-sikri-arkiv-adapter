package no.fint.sikri.data.utilities;

import io.netty.util.internal.StringUtil;
import no.fint.model.resource.Link;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public enum SikriUtils {
    ;

    private final static String MARKED_REGEX_PATTERN = "#([^#]+)#";
    private final static String SHIELDED_REGEX_PATTERN = "@((\\S+@\\S+\\.\\S+)|([^@]+))@";

    public static <T> void applyParameter(T value, Consumer<T> consumer) {
        applyParameter(value, consumer, Function.identity());
    }

    public static <T, U> void applyParameter(T value, Consumer<U> consumer, Function<T, U> mapper) {
        if (value != null) {
            consumer.accept(mapper.apply(value));
        }
    }

    public static <T> void applyParameterFromLink(List<Link> links, Function<String, T> mapper, Consumer<T> consumer) {
        getLinkTargets(links)
                .map(mapper)
                .findFirst()
                .ifPresent(consumer);
    }

    public static Stream<String> getLinkTargets(List<Link> links) {
        return links.stream()
                .map(Link::getHref)
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.substringAfterLast(s, "/"));
    }

    public static boolean applyParameterFromLink(List<Link> links, Consumer<String> consumer) {
        return getLinkTargets(links)
                .findFirst()
                .map(success(consumer))
                .orElse(false);
    }

    public static <T> Function<T, Boolean> success(Consumer<T> consumer) {
        return it -> {
            consumer.accept(it);
            return true;
        };
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

    public static URL getURL(String location) throws MalformedURLException {
        if (StringUtils.startsWithAny(location, "file:", "http:", "https:")) {
            return new URL(location);
        }
        return new URL("file:" + location);
    }

    public static String getShieldedTitle(String title) {
        if (StringUtil.isNullOrEmpty(title)) {
            return null;
        }

        String shieldedTitle = getCustomizedTitle(title, SHIELDED_REGEX_PATTERN, "*****");
        return cleanUpTitle(shieldedTitle, MARKED_REGEX_PATTERN);
    }

    public static String getMarkedTitle(String title) {
        if (StringUtil.isNullOrEmpty(title)) {
            return null;
        }
        String markedTitle = getCustomizedTitle(title, MARKED_REGEX_PATTERN, "#####");
        return getMarkedTextInsideShildTitle(markedTitle, SHIELDED_REGEX_PATTERN, "*****");
    }

    private static String getCustomizedTitle(String title, String pattern, String replacement) {
        Matcher matcher = Pattern.compile(pattern).matcher(title);
        String markedTitle = title;
        while (matcher.find()) {
            String match = matcher.group();

            String markedMatch = match
                    .replaceAll("#", "")
                    .replaceAll("@", "")
                    .replaceAll("[^ ]+", replacement)
                    .replaceFirst("#####(?= *$)", "####_");

            markedTitle = markedTitle.replace(match, markedMatch);
        }
        return markedTitle;
    }

    private static String getMarkedTextInsideShildTitle(String title, String pattern, String replacement) {
        Matcher matcher = Pattern.compile(pattern).matcher(title);
        String markedTitle = title;
        while (matcher.find()) {
            String match = matcher.group();

            String markedMatch = match
                    .replaceAll("(?!([@#_]+))[^ ]+", replacement)
                    .replaceAll("#####", "+++++")
                    .replaceAll("####_", "++++_")
                    .replaceFirst("^@", "")
                    .replaceFirst("@$", "");

            markedTitle = markedTitle.replace(match, markedMatch);
        }
        return markedTitle;
    }

    private static String cleanUpTitle(String title, String pattern) {
        return Pattern.compile(pattern).matcher(title).replaceAll("$1");
    }
}
