package com.cosium.openapi.annotation_processor.parser.utils;


import io.swagger.models.properties.*;

import javax.lang.model.type.TypeMirror;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class PropertyUtils {

    public PropertyUtils() {
    }

    public Property from(TypeMirror typeMirror) {
        Property property;
        switch (typeMirror.getKind()) {
            case BOOLEAN:
                property = new BooleanProperty();
                break;
            case SHORT:
            case INT:
            case BYTE:
            case CHAR:
                property = new IntegerProperty();
                break;
            case LONG:
                property = new LongProperty();
                break;
            case FLOAT:
                property = new FloatProperty();
                break;
            case DOUBLE:
                property = new DoubleProperty();
                break;
            case ARRAY:
                property = new ArrayProperty();
                break;
            default:
                property = new ObjectProperty();
                break;
        }

        return property;
    }

}
