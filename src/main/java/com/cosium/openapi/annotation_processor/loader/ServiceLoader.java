package com.cosium.openapi.annotation_processor.loader;

import java.util.List;

/**
 * Could not manage to make work the standard {@link java.util.ServiceLoader}.
 * This is a replacement.
 *
 * Created on 14/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface ServiceLoader {

    /**
     * @param serviceType The service type
     * @param <T> The service type
     * @return All the available instances for the service type
     */
    <T> List<T> load(Class<T> serviceType);

}
