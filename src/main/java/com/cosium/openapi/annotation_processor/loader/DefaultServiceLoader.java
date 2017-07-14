package com.cosium.openapi.annotation_processor.loader;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 14/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultServiceLoader implements ServiceLoader {

    private final Map<Class<?>, List<?>> servicesByType = Collections.synchronizedMap(new HashMap<>());

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> load(Class<T> serviceType) {
        servicesByType.computeIfAbsent(serviceType, this::doLoad);
        return (List<T>) servicesByType.get(serviceType);
    }

    private <T> List<T> doLoad(Class<T> serviceType) {
        try (InputStream inputStream = getClass().getResourceAsStream("/META-INF/services/" + serviceType.getCanonicalName())) {
            return readLines(inputStream)
                    .stream()
                    .map(this::classForName)
                    .map(this::newInstance)
                    .map(serviceType::cast)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> readLines(InputStream inputStream) throws IOException {
        return IOUtils.readLines(inputStream);
    }

    private Class<?> classForName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T newInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
