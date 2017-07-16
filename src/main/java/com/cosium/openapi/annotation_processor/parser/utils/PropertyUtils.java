package com.cosium.openapi.annotation_processor.parser.utils;


import io.swagger.models.properties.*;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class PropertyUtils {

    private final Types typeUtils;
    private final Elements elementUtils;

    public PropertyUtils(Types typeUtils, Elements elementUtils) {
        requireNonNull(typeUtils);
        requireNonNull(elementUtils);

        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    public Property from(Element element) {
        return from(element.asType());
    }

    public Property from(TypeMirror type) {
        Property property;
        switch (type.getKind()) {
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

        if (property instanceof ObjectProperty) {
            if (isSubtype(type, String.class)) {
                property = new StringProperty();
            } else if (isSubtype(type, Date.class)) {
                property = new DateProperty();
            } else if (isSubtype(type, UUID.class)) {
                property = new UUIDProperty();
            } else if (isSubtype(type, File.class)) {
                property = new FileProperty();
            } else if (isSubtype(type, Collection.class)) {
                property = new ArrayProperty();
            } else if (isSubtype(type, Map.class)) {
                property = new MapProperty();
            } else if (isSubtype(type, Boolean.class)) {
                property = new BooleanProperty();
            } else if (isSubtype(type, Long.class)) {
                property = new LongProperty();
            } else if (isSubtype(type, Float.class)) {
                property = new FloatProperty();
            } else if (isSubtype(type, Double.class)) {
                property = new DoubleProperty();
            } else if (isSubtype(type, Short.class)
                    || isSubtype(type, Integer.class)
                    || isSubtype(type, Byte.class)
                    || isSubtype(type, Character.class)) {
                property = new IntegerProperty();
            }
        }

        if (property instanceof ArrayProperty) {
            ArrayProperty arrayProperty = (ArrayProperty) property;
            arrayProperty.setItems(new ObjectProperty());
        }

        return property;
    }

    private boolean isSubtype(TypeMirror t1, Class<?> t2) {
        return typeUtils.isSubtype(t1, elementUtils.getTypeElement(t2.getCanonicalName()).asType());
    }

}
