package com.cosium.openapi.annotation_processor.specification;

import com.cosium.openapi.annotation_processor.RoundDescriptor;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
class BasicSpecificationGenerator implements SpecificationGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(BasicSpecificationGenerator.class);

    private final AtomicReference<Swagger> runtimeCache;
    private final ISpecificationGeneratorOptions options;

    public BasicSpecificationGenerator(AtomicReference<Swagger> runtimeCache, ISpecificationGeneratorOptions options) {
        requireNonNull(runtimeCache);
        requireNonNull(options);
        this.runtimeCache = runtimeCache;
        this.options = options;
    }

    @Override
    public Swagger generate(List<ParsedPath> parsedPaths, RoundDescriptor roundDescriptor) {
        LOG.debug("Generating specification for {}", parsedPaths);

        Swagger swagger = runtimeCache.updateAndGet(spec -> ofNullable(spec).orElseGet(Swagger::new))
                .info(new Info().title(options.title()))
                .basePath(options.basePath())
                .produces(options.produces())
                .consumes(options.consumes());
        SwaggerUtils.addParsedPaths(parsedPaths, swagger);
        return swagger;
    }

}
