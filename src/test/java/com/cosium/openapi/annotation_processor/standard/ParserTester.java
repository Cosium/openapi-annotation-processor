package com.cosium.openapi.annotation_processor.standard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 13/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class ParserTester {

    private static final Logger LOG = LoggerFactory.getLogger(ParserTester.class);

    private final Path path;
    private final String parserName;

    public ParserTester(Path path) {
        requireNonNull(path);

        this.path = path;
        this.parserName = path.getFileName().toString();
    }

    public void test() throws Exception{
        LOG.info("Running tests for parser '{}'", parserName);
        PathUtils
                .subDirectories(path)
                .stream()
                .map(casePath -> new ParserCaseTester(parserName, casePath))
                .forEach(ParserCaseTester::test);
    }
}
