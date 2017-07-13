package com.cosium.openapi.annotation_processor;

import com.cosium.openapi.annotation_processor.standard.ParserTester;
import com.cosium.openapi.annotation_processor.standard.PathUtils;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created on 13/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class StandardTest {

    private final Path basePath;

    public StandardTest() throws Exception{
        URL url = getClass().getResource("/standard");
        this.basePath = Paths.get(url.toURI());
    }

    @Test
    public void test() throws Exception{
        PathUtils
                .subDirectories(basePath)
                .stream()
                .map(ParserTester::new)
                .forEach(parserTester -> {
                    try {
                        parserTester.test();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
