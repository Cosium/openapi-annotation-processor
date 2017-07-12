package com.cosium.openapi.annotation_processor.documentator;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface DocumentatorFactory {

    /**
     * @return A new documentator
     */
    Documentator build(IDocumentatorOptions options);

}
