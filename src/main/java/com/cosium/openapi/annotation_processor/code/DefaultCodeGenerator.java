package com.cosium.openapi.annotation_processor.code;

import com.cosium.openapi.annotation_processor.FileManager;
import io.swagger.models.Swagger;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultCodeGenerator implements CodeGenerator {

    private final ICodeGeneratorOptions options;
    private final FileManager fileManager;

    public DefaultCodeGenerator(ICodeGeneratorOptions options, FileManager fileManager) {
        requireNonNull(options);
        requireNonNull(fileManager);
        this.options = options;
        this.fileManager = fileManager;
    }

    @Override
    public void generate(Swagger swagger) {

    }
}
