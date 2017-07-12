package com.cosium.openapi.annotation_processor;

import com.cosium.openapi.annotation_processor.model.ParsedPath;

import javax.lang.model.element.Element;
import java.util.List;

/**
 * Parses paths from an annotated element
 *
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface PathParser {

    /**
     * @param annotatedElement The element from which to parse the paths.
     *                         This element is annoted with {@link PathParserFactory#getSupportedAnnotation()}.
     * @return A list of parsed paths
     */
    List<ParsedPath> parse(Element annotatedElement);

}
