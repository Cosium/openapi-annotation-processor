package com.cosium.openapi.annotation_processor.option;


import com.cosium.openapi.annotation_processor.documentator.IDocumentatorOptions;
import org.immutables.value.Value;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
@Value.Immutable
public interface IOptions {

    IDocumentatorOptions documentator();

}
