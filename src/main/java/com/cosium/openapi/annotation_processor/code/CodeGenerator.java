package com.cosium.openapi.annotation_processor.code;

import io.swagger.models.Swagger;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface CodeGenerator {

    void generate(Swagger swagger);

}
