package com.cosium.openapi.annotation_processor.specification;

import com.cosium.openapi.annotation_processor.RoundDescriptor;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import io.swagger.models.Swagger;

import java.util.List;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface SpecificationGenerator {

    /**
     * Generate specification for given paths
     *
     * @param parsedPaths The parsed paths
     * @param roundDescriptor The descriptor of the current round
     * @return The generated specification
     */
    Swagger generate(List<ParsedPath> parsedPaths, RoundDescriptor roundDescriptor);

}
