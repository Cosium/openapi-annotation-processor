package com.cosium.openapi.annotation_processor.option;


import com.cosium.openapi.annotation_processor.code.ICodeGeneratorOptions;
import com.cosium.openapi.annotation_processor.specification.ISpecificationGeneratorOptions;
import org.immutables.value.Value;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
@Value.Immutable
public interface IOptions {

    /**
     * @return The base package that will be used to generate all resources
     */
    String baseGenerationPackage();

    /**
     * @return The specification generator options
     */
    ISpecificationGeneratorOptions specificationGenerator();

    /**
     * @return The code generator options
     */
    ICodeGeneratorOptions codeGenerator();

}
