package com.cosium.openapi.annotation_processor.code;

import com.cosium.openapi.annotation_processor.file.FileManager;
import com.cosium.openapi.annotation_processor.loader.ServiceLoader;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17/08/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class CodeGeneratorFactory {

    private final ICodeGeneratorOptions options;
    private final ServiceLoader serviceLoader;

    public CodeGeneratorFactory(ICodeGeneratorOptions options, ServiceLoader serviceLoader) {
        requireNonNull(options);
        requireNonNull(serviceLoader);
        this.options = options;
        this.serviceLoader = serviceLoader;
    }

    public CodeGenerator build(FileManager fileManager) {
        return new DefaultCodeGenerator(
                options,
                serviceLoader,
                fileManager
        );
    }

}
