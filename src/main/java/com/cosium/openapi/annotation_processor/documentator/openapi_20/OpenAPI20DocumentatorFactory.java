package com.cosium.openapi.annotation_processor.documentator.openapi_20;

import com.cosium.openapi.annotation_processor.documentator.Documentator;
import com.cosium.openapi.annotation_processor.documentator.DocumentatorFactory;
import com.cosium.openapi.annotation_processor.documentator.IDocumentatorOptions;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class OpenAPI20DocumentatorFactory implements DocumentatorFactory {
    @Override
    public Documentator build(IDocumentatorOptions options) {
        return new OpenAPI20Documentator(options);
    }
}
