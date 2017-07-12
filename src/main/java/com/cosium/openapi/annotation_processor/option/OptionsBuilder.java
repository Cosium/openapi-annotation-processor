package com.cosium.openapi.annotation_processor.option;

import com.cosium.openapi.annotation_processor.documentator.DocumentatorOptions;
import com.cosium.openapi.annotation_processor.documentator.IDocumentatorOptions;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class OptionsBuilder {

    private static final String BASE_PATH_OPTION = "basePath";
    private static final String PRODUCES_OPTION = "produces";
    private static final String CONSUMES_OPTION = "consumes";

    private static final String APPLICATION_JSON = "application/json";

    public Set<String> getSupportedOptions(){
        return new HashSet<>(Arrays.asList(BASE_PATH_OPTION, PRODUCES_OPTION, CONSUMES_OPTION));
    }

    public IOptions build(Map<String, String> options){
        return Options.builder()
                .documentator(buildDocumentatorOptions(options))
                .build();
    }

    private IDocumentatorOptions buildDocumentatorOptions(Map<String, String> options){
        DocumentatorOptions.BuildFinal documentatorOptionsBuilder = DocumentatorOptions
                .builder()
                .basePath(options.getOrDefault(BASE_PATH_OPTION, "/"));

        Stream.of(StringUtils.split(StringUtils.defaultIfBlank(options.get(CONSUMES_OPTION), APPLICATION_JSON), ","))
                .forEach(documentatorOptionsBuilder::addConsumes);
        Stream.of(StringUtils.split(StringUtils.defaultIfBlank(options.get(PRODUCES_OPTION), APPLICATION_JSON), ","))
                .forEach(documentatorOptionsBuilder::addProduces);
        return documentatorOptionsBuilder.build();
    }

}
