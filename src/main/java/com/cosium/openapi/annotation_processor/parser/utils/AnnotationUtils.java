package com.cosium.openapi.annotation_processor.parser.utils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class AnnotationUtils {

    private final Types typeUtils;

    public AnnotationUtils(Types typeUtils) {
        requireNonNull(typeUtils);
        this.typeUtils = typeUtils;
    }

    public <A extends Annotation> TypeElement extractTypeElement(A annotation, Function<A, Class<?>> extractor) {
        try {
            extractor.apply(annotation);
            throw new RuntimeException("controllerAnnotation.value() didn't throw !");
        } catch (MirroredTypeException mte) {
            return (TypeElement) typeUtils.asElement(mte.getTypeMirror());
        }
    }

    public <A extends Annotation> TypeMirror extractType(A annotation, Function<A, Class<?>> extractor) {
        return extractTypeElement(annotation, extractor).asType();
    }
}
