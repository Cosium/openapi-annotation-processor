package com.cosium.openapi.annotation_processor.model;

import io.swagger.models.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class ParsedPath {

    private final String pathTemplate;
    private final Path path;

    public ParsedPath(String pathTemplate, Path path) {
        requireNonNull(pathTemplate);
        requireNonNull(path);

        this.pathTemplate = pathTemplate;
        this.path = path;
    }

    public String getPathTemplate() {
        return pathTemplate;
    }

    public Path getPath() {
        return path;
    }
}
