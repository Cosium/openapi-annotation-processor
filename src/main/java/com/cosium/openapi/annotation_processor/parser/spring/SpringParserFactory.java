package com.cosium.openapi.annotation_processor.parser.spring;

import com.cosium.openapi.annotation_processor.Parser;
import com.cosium.openapi.annotation_processor.ParserFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class SpringParserFactory implements ParserFactory {

    @Override
    public String getSupportedAnnotation() {
        return RequestMapping.class.getCanonicalName();
    }

    @Override
    public Parser build(Types typeUtils, Elements elementUtils) {
        return new SpringParser(typeUtils, elementUtils);
    }
}
