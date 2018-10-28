package com.cosium.openapi.annotation_processor.specification;

import com.cosium.openapi.annotation_processor.file.FileManager;
import io.swagger.models.Swagger;

import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17/08/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class SpecificationGeneratorFactory {

    private final AtomicReference<Swagger> runtimeCache = new AtomicReference<>();
    private final ISpecificationGeneratorOptions options;

    public SpecificationGeneratorFactory(ISpecificationGeneratorOptions options) {
        requireNonNull(options);
        this.options = options;
    }


    public SpecificationGenerator build(FileManager fileManager) {
        return new WriterSpecificationGenerator(
                new IncrementalSpecificationGenerator(
                        new BasicSpecificationGenerator(
                                runtimeCache,
                                options
                        ), fileManager
                ), fileManager
        );
    }

}
