package com.cosium.openapi.annotation_processor.specification;

import com.cosium.openapi.annotation_processor.RoundDescriptor;
import com.cosium.openapi.annotation_processor.file.FileManager;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

import javax.tools.FileObject;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created on 18/08/17.
 *
 * @author Reda.Housni-Alaoui
 */
class WriterSpecificationGenerator implements SpecificationGenerator {

    private final SpecificationGenerator delegate;
    private final FileManager fileManager;

    public WriterSpecificationGenerator(SpecificationGenerator delegate, FileManager fileManager) {
        requireNonNull(delegate);
        requireNonNull(fileManager);
        this.delegate = delegate;
        this.fileManager = fileManager;
    }

    @Override
    public Swagger generate(List<ParsedPath> parsedPaths, RoundDescriptor roundDescriptor) {
        Swagger swagger = delegate.generate(parsedPaths, roundDescriptor);
        if (roundDescriptor.isLast()) {
            FileObject yaml = fileManager.createResource("api.yaml");
            SwaggerUtils.write(Yaml.pretty(), yaml, swagger);
            FileObject json = fileManager.createResource("api.json");
            SwaggerUtils.write(Json.pretty(), json, swagger);
        }
        return swagger;
    }
}
