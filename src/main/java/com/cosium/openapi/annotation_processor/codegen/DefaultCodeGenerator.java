package com.cosium.openapi.annotation_processor.codegen;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultCodeGenerator implements CodeGenerator {

    private final ICodeGeneratorOptions options;

    public DefaultCodeGenerator(ICodeGeneratorOptions options) {
        requireNonNull(options);
        this.options = options;
    }



}
