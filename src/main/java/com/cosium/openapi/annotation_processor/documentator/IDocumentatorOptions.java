package com.cosium.openapi.annotation_processor.documentator;

import org.immutables.value.Value;

import java.util.List;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
@Value.Immutable
public interface IDocumentatorOptions {

    String basePath();

    List<String> produces();

    List<String> consumes();

}
