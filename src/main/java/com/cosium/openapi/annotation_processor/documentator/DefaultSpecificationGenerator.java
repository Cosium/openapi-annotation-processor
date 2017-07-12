package com.cosium.openapi.annotation_processor.documentator;

import com.cosium.openapi.annotation_processor.model.ParsedPath;
import io.swagger.models.Swagger;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultSpecificationGenerator implements SpecificationGenerator {

    private final ISpecificationGeneratorOptions options;

    public DefaultSpecificationGenerator(ISpecificationGeneratorOptions options) {
        requireNonNull(options);
        this.options = options;
    }

    @Override
    public Swagger generate(List<ParsedPath> parsedPaths) {
        Swagger swagger = new Swagger();
        swagger.basePath(options.basePath());
        swagger.produces(options.produces());
        parsedPaths.forEach(parsedPath -> swagger.path(parsedPath.getPathTemplate(), parsedPath.getPath()));
        return swagger;
    }
}
