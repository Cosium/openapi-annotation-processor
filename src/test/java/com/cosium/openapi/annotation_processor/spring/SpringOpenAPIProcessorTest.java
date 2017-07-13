package com.cosium.openapi.annotation_processor.spring;

import com.cosium.openapi.annotation_processor.OpenAPIProcessor;
import com.google.common.truth.Truth;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.Test;

import java.util.Collections;

import static com.google.testing.compile.JavaFileObjects.forResource;

/**
 * Created on 13/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class SpringOpenAPIProcessorTest {

    private static final String RESOURCE_DIRECTORY = "com/cosium/openapi/annotation_processor/spring";

    private void test(String caseToTest){
        Truth.assert_()
                .about(JavaSourcesSubjectFactory.javaSources())
                .that(Collections.singletonList(
                        forResource(RESOURCE_DIRECTORY + "/" + caseToTest + "/Controller.java")
                ))
                .processedWith(new OpenAPIProcessor())
                .compilesWithoutError();
    }

    @Test
    public void testEmpty(){
        test("empty");
    }

}
