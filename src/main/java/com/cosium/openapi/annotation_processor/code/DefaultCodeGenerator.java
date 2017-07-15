package com.cosium.openapi.annotation_processor.code;

import com.cosium.openapi.annotation_processor.FileManager;
import com.cosium.openapi.annotation_processor.loader.ServiceLoader;
import io.swagger.codegen.ClientOptInput;
import io.swagger.codegen.ClientOpts;
import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.DefaultGenerator;
import io.swagger.models.Swagger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.FileObject;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class DefaultCodeGenerator implements CodeGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCodeGenerator.class);

    private final ICodeGeneratorOptions options;
    private final ServiceLoader serviceLoader;
    private final FileManager fileManager;

    public DefaultCodeGenerator(ICodeGeneratorOptions options, ServiceLoader serviceLoader, FileManager fileManager) {
        requireNonNull(options);
        requireNonNull(serviceLoader);
        requireNonNull(fileManager);
        this.options = options;
        this.serviceLoader = serviceLoader;
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
            LOG.debug("Using output dir {}", mainPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CodegenConfig codegenConfig = serviceLoader
                .load(CodegenConfig.class)
                .stream()
                .filter(config -> config.getName().equals(language))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find codegen configuration for language " + language));

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
                .forEach(file -> writeFile(language, mainPath, file.toPath()));
    }

    private void writeFile(String packageRoot, Path mainPath, Path file) {
        LOG.debug("Writing file {} relatively to {}", file, mainPath);
        Path relativePath = mainPath.relativize(file).getParent();

        String packageName = ofNullable(relativePath)
                .map(path -> StringUtils.replace(path.toString(), File.pathSeparator, "."))
                .map(s -> packageRoot + "." + s)
                .orElse(packageRoot);
        packageName = StringUtils.replace(packageName, "-", "_");

        LOG.debug("Computed package name '{}'", packageName);

        FileObject fileObject = fileManager.createResource(packageName, file.getFileName().toString());
        try (Writer writer = fileObject.openWriter(); Reader reader = Files.newBufferedReader(file)) {
            IOUtils.copy(reader, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
