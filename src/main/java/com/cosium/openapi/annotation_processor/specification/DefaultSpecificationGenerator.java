package com.cosium.openapi.annotation_processor.specification;

import com.cosium.openapi.annotation_processor.FileManager;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

import javax.tools.FileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultSpecificationGenerator implements SpecificationGenerator {

    private final ISpecificationGeneratorOptions options;
    private final FileManager fileManager;

    private final AtomicReference<Swagger> cache = new AtomicReference<>();

    public DefaultSpecificationGenerator(ISpecificationGeneratorOptions options, FileManager fileManager) {
        requireNonNull(options);
        requireNonNull(fileManager);
        this.options = options;
        this.fileManager = fileManager;
    }

    @Override
    public Swagger generate(List<ParsedPath> parsedPaths, boolean lastRound) {
        Swagger swagger = cache.updateAndGet(spec -> ofNullable(spec).orElseGet(Swagger::new));
        swagger.basePath(options.basePath());
        swagger.produces(options.produces());
        parsedPaths.forEach(parsedPath -> swagger.path(parsedPath.getPathTemplate(), parsedPath.getPath()));

        if (lastRound) {
            FileObject yaml = fileManager.createResource("api.yaml");
            write(Yaml.pretty(), yaml, swagger);
            FileObject json = fileManager.createResource("api.json");
            write(Json.pretty(), json, swagger);
        }

        return swagger;
    }

    private void write(ObjectWriter objectWriter, FileObject fileObject, Swagger swagger) {
        try (Writer writer = fileObject.openWriter()) {
            objectWriter.writeValue(writer, swagger);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
