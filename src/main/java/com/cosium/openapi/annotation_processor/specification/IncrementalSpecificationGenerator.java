package com.cosium.openapi.annotation_processor.specification;

import com.cosium.openapi.annotation_processor.RoundDescriptor;
import com.cosium.openapi.annotation_processor.file.FileManager;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import io.swagger.models.Swagger;
import io.swagger.util.Json;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Created on 18/08/17.
 *
 * @author Reda.Housni-Alaoui
 */
class IncrementalSpecificationGenerator implements SpecificationGenerator {

    private static final String PARSED_PATHS_CACHE_FILENAME = "parsed-paths.cache.json";

    private final SpecificationGenerator delegate;
    private final FileManager fileManager;

    public IncrementalSpecificationGenerator(SpecificationGenerator delegate, FileManager fileManager) {
        requireNonNull(delegate);
        requireNonNull(fileManager);
        this.delegate = delegate;
        this.fileManager = fileManager;
    }

    @Override
    public Swagger generate(List<ParsedPath> parsedPaths, RoundDescriptor roundDescriptor) {
        if (roundDescriptor.isFirst()) {
            List<ParsedPath> cachedPaths = readFromFileCache();
            parsedPaths = Stream
                    .concat(parsedPaths.stream(), cachedPaths.stream())
                    .distinct()
                    .collect(Collectors.toList());
        }
        Swagger swagger = delegate.generate(parsedPaths, roundDescriptor);
        if (roundDescriptor.isLast()) {
            writeToFileCache(SwaggerUtils.toParsedPaths(swagger));
        }
        return swagger;
    }

    private List<ParsedPath> readFromFileCache() {
        try {
            return SwaggerUtils.read(Json.mapper(), fileManager.getResource(PARSED_PATHS_CACHE_FILENAME));
        } catch (FileNotFoundException | NoSuchFileException e) {
            return Collections.emptyList();
        }
    }

    private void writeToFileCache(List<ParsedPath> parsedPaths) {
        SwaggerUtils.write(Json.pretty(), fileManager.createResource(PARSED_PATHS_CACHE_FILENAME), parsedPaths);
    }

}
