package no.fint.sikri.data.utilities;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Slf4j
public final class FintPropertyUtils {
    private FintPropertyUtils() {
    }

    /**
     * Conditionally copies properties from source to target.
     * @param source object.
     * @param target object.
     * @param propertyPredicate determines whether property should be copied.
     * @param mergeFunction merges values from source and target.
     * @param <T> any type with public getter and setter methods for all properties.
     */
    public static <T> void copyProperties(T source, T target,
                                          Predicate<PropertyDescriptor> propertyPredicate,
                                          BiFunction<Object, Object, Object> mergeFunction) {
        for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(source)) {
            if (propertyPredicate.test(propertyDescriptor)) {
                final Method readMethod = propertyDescriptor.getReadMethod();
                final Method writeMethod = propertyDescriptor.getWriteMethod();
                try {
                    Object sourceValue = readMethod.invoke(source);
                    Object targetValue = readMethod.invoke(target);
                    final Object result = mergeFunction.apply(sourceValue, targetValue);
                    writeMethod.invoke(target, result);
                    log.trace("Set property {} to {}", propertyDescriptor.getName(), result);
                } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
                    log.debug("Unable to copy property {}", propertyDescriptor.getName(), e);
                }
            }
        }
    }
}
