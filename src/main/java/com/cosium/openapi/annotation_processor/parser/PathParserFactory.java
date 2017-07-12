package com.cosium.openapi.annotation_processor.parser;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Builds a path parser against a supported annotation
 *
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface PathParserFactory {

    /**
     * @return The supported annotation qualified name
     */
    String getSupportedAnnotation();

    /**
     * @return A new parser
     */
    PathParser build(Types typeUtils, Elements elementUtils);

}
