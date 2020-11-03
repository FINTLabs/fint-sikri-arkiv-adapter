package no.fint.sikri.data.utilities;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@Slf4j
public final class PropUtils {
    private PropUtils() {
    }

    public static <T> void copyProperties(T source, T target) {
        Arrays.stream(PropertyUtils.getPropertyDescriptors(source))
                .map(PropertyDescriptor::getName)
                .forEach(property -> {
                    try {
                        final Object sourceValue = PropertyUtils.getSimpleProperty(source, property);
                        final Object targetValue = PropertyUtils.getSimpleProperty(target, property);

                        if (targetValue == null && sourceValue != null) {
                            log.trace("Setting property {} to {}", property, sourceValue);
                            PropertyUtils.setSimpleProperty(target, property, sourceValue);
                        }
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        log.trace(property, e);
                    }
                });
    }
}
