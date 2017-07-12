package com.cosium.openapi.annotation_processor.parser.spring;

import com.cosium.openapi.annotation_processor.Parser;
import com.cosium.openapi.annotation_processor.specification.Specification_;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
class SpringParser implements Parser {

    private final Types typeUtils;
    private final Elements elementUtils;

    SpringParser(Types typeUtils, Elements elementUtils) {
        requireNonNull(typeUtils);
        requireNonNull(elementUtils);

        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    @Override
    public Specification_ parse(Element annotatedElement) {
        RequestMapping requestMapping = annotatedElement.getAnnotation(RequestMapping.class);

        return null;
    }
}
