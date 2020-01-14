package no.fint.sikri.utilities;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

public enum SikriUtils {
    ;


    public static URL getURL(String location) throws MalformedURLException {
        if (StringUtils.startsWithAny(location, "file:", "http:", "https:")) {
            return new URL(location);
        }
        return new URL("file:" + location);
    }






}
