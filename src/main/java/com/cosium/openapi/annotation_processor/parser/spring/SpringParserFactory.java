package com.cosium.openapi.annotation_processor.parser.spring;

import com.cosium.openapi.annotation_processor.parser.PathParser;
import com.cosium.openapi.annotation_processor.parser.PathParserFactory;
import com.cosium.openapi.annotation_processor.parser.utils.PropertyUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class SpringParserFactory implements PathParserFactory {

    @Override
    public String getSupportedAnnotation() {
        return RequestMapping.class.getCanonicalName();
    }

    @Override
    public PathParser build(Types typeUtils, Elements elementUtils) {
        return new SpringParser(new PropertyUtils(typeUtils, elementUtils));
    }
}
