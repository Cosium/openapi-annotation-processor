package com.cosium.openapi.annotation_processor.documentator.openapi_20;

import com.cosium.openapi.annotation_processor.documentator.Documentator;
import com.cosium.openapi.annotation_processor.documentator.IDocumentatorOptions;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import io.swagger.models.Swagger;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
class OpenAPI20Documentator implements Documentator {

    private final IDocumentatorOptions options;

    OpenAPI20Documentator(IDocumentatorOptions options) {
        requireNonNull(options);
        this.options = options;
    }

    @Override
    public void document(List<ParsedPath> parsedPaths) {
        Swagger swagger = new Swagger();
        swagger.basePath(options.basePath());
        swagger.produces(options.produces());

    }
}
