package com.cosium.openapi.annotation_processor.code;

import com.cosium.openapi.annotation_processor.FileManager;
import io.swagger.codegen.*;
import io.swagger.models.Swagger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.tools.FileObject;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

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
        options.languages().forEach(language -> generate(swagger, language));
    }

    private void generate(Swagger swagger, String language) {
        Path mainPath;
        try {
            mainPath = Files.createTempDirectory(language);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CodegenConfig codegenConfig = CodegenConfigLoader.forName(language);
        codegenConfig.setOutputDir(mainPath.toString());

        ClientOptInput clientOptInput = new ClientOptInput();
        ClientOpts clientOpts = new ClientOpts();
        clientOptInput
                .opts(clientOpts)
                .config(codegenConfig)
                .swagger(swagger);


        new DefaultGenerator()
                .opts(clientOptInput)
                .generate()
                .forEach(file -> writeFile(mainPath, file.toPath()));
    }

    private void writeFile(Path mainPath, Path file) {
        Path relativePath = file.relativize(mainPath);
        String packageName = StringUtils.replace(relativePath.toString(), File.pathSeparator, ".");
        FileObject fileObject = fileManager.createResource(packageName, file.getFileName().toString());
        try (Writer writer = fileObject.openWriter(); Reader reader = Files.newBufferedReader(file)) {
            IOUtils.copy(reader, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
