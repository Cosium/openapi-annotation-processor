package com.cosium.openapi.annotation_processor;

import com.cosium.openapi.annotation_processor.model.ParsedPath;

import java.util.List;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface Documentator {

    /**
     * Generate documentation for the given specifications
     * @param specifications The specifications to generate documentation for
     */
    void document(List<ParsedPath> specifications);

}
