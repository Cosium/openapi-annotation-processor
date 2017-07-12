package com.cosium.openapi.annotation_processor.option;


import com.cosium.openapi.annotation_processor.codegen.ICodeGeneratorOptions;
import com.cosium.openapi.annotation_processor.documentator.ISpecificationGeneratorOptions;
import org.immutables.value.Value;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
@Value.Immutable
public interface IOptions {

    ISpecificationGeneratorOptions specificationGenerator();

    ICodeGeneratorOptions codeGenerator();

}
