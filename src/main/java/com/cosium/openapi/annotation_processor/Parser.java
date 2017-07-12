package com.cosium.openapi.annotation_processor;

import com.cosium.openapi.annotation_processor.specification.Specification_;

import javax.lang.model.element.Element;

/**
 * Parses a specification from an annotated element
 *
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface Parser {

    /**
     * @param annotatedElement The element from which to parse the specification
     * @return The specification that was parsed from annotatedElement
     */
    Specification_ parse(Element annotatedElement);

}
