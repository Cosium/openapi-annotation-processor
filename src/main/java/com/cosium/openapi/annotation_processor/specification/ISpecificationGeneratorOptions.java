package com.cosium.openapi.annotation_processor.specification;

import org.immutables.value.Value;

import java.util.List;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
@Value.Immutable
public interface ISpecificationGeneratorOptions {

    /**
     * @return The specification base path
     */
    String basePath();

    /**
     * @return The produced global mime types
     */
    List<String> produces();

    /**
     * @return The consumes global mime types
     */
    List<String> consumes();

}
