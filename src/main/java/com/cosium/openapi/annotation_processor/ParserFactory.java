package com.cosium.openapi.annotation_processor;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Builds a parser against a supported annotation
 *
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface ParserFactory {

    /**
     * @return The supported annotation qualified name
     */
    String getSupportedAnnotation();

    /**
     * @return A new parser
     */
    Parser build(Types typeUtils, Elements elementUtils);

}
