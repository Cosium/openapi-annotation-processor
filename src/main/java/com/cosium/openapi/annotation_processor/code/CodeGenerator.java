package com.cosium.openapi.annotation_processor.code;

import io.swagger.models.Swagger;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface CodeGenerator {

    /**
     * Generates code from specification
     * @param swagger The specification to generate code from
     */
    void generate(Swagger swagger);

}
