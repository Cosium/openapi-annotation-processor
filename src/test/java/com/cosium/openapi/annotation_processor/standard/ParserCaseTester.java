package com.cosium.openapi.annotation_processor.standard;

import com.cosium.openapi.annotation_processor.OpenAPIProcessor;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Created on 13/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class ParserCaseTester {

    private static final Logger LOG = LoggerFactory.getLogger(ParserCaseTester.class);

    private static final JavaFileManager.Location LOCATION = StandardLocation.CLASS_OUTPUT;

    private final String parserName;
    private final String caseName;

    private final Path inputPath;
    private final Path expectedCodePath;
    private final Path expectedSpecificationPath;

    public ParserCaseTester(String parserName, Path path) {
        requireNonNull(parserName);
        requireNonNull(path);

        this.parserName = parserName;
        this.caseName = path.getFileName().toString();

        this.inputPath = path.resolve("input");
        Path expected = path.resolve("expected");
        this.expectedCodePath = expected.resolve("code");
        this.expectedSpecificationPath = expected.resolve("specification");
    }

    public void test() {
        try {
            doTest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void doTest() throws Exception {
        LOG.info("Running test '{}' for parser '{}'", caseName, parserName);

        List<JavaFileObject> inputs = subFiles(inputPath);
        LOG.debug("Collected {} inputs", inputs.size());

        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(subFiles(inputPath))
                .processedWith(new OpenAPIProcessor())
                .compilesWithoutError();
    }

    private List<JavaFileObject> subFiles(Path path){
        return PathUtils
                .subFiles(path)
                .stream()
                .map(Path::toUri)
                .map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(JavaFileObjects::forResource)
                .collect(Collectors.toList());
    }

}
