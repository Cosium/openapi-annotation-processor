package com.cosium.openapi.annotation_processor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.models.Path;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class ParsedPath {

    private final String pathTemplate;
    private final Path path;

    @JsonCreator
    public ParsedPath(@JsonProperty("pathTemplate") String pathTemplate, @JsonProperty("path") Path path) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pathTemplate", pathTemplate)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParsedPath that = (ParsedPath) o;

        return pathTemplate.equals(that.pathTemplate);
    }

    @Override
    public int hashCode() {
        return pathTemplate.hashCode();
    }
}
