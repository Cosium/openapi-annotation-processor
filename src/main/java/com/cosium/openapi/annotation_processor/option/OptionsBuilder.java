package com.cosium.openapi.annotation_processor.option;

import com.cosium.openapi.annotation_processor.codegen.CodeGeneratorOptions;
import com.cosium.openapi.annotation_processor.codegen.ICodeGeneratorOptions;
import com.cosium.openapi.annotation_processor.documentator.ISpecificationGeneratorOptions;
import com.cosium.openapi.annotation_processor.documentator.SpecificationGeneratorOptions;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class OptionsBuilder {

    private static final String SPECIFICATION_GENERATOR_PREFIX = "documentation-generator_";

    private static final String SPECIFICATION_GENERATOR_BASE_PATH_OPTION = SPECIFICATION_GENERATOR_PREFIX + "basePath";
    private static final String SPECIFICATION_GENERATOR_PRODUCES_OPTION = SPECIFICATION_GENERATOR_PREFIX + "produces";
    private static final String SPECIFICATION_GENERATOR_CONSUMES_OPTION = SPECIFICATION_GENERATOR_PREFIX + "consumes";

    private static final String CODE_GENERATOR_PREFIX = "code-generator_";

    private static final String CODE_GENERATOR_LANGUAGES = CODE_GENERATOR_PREFIX + "languages";

    private static final String APPLICATION_JSON = "application/json";

    public Set<String> getSupportedOptions() {
        return new HashSet<>(Arrays.asList(
                SPECIFICATION_GENERATOR_BASE_PATH_OPTION,
                SPECIFICATION_GENERATOR_PRODUCES_OPTION,
                SPECIFICATION_GENERATOR_CONSUMES_OPTION,
                CODE_GENERATOR_LANGUAGES
        ));
    }

    public IOptions build(Map<String, String> options) {
        return Options
                .builder()
                .specificationGenerator(buildSpecificationGenerator(options))
                .codeGenerator(buildCodeGenerator(options))
                .build();
    }

    private ISpecificationGeneratorOptions buildSpecificationGenerator(Map<String, String> options) {
        SpecificationGeneratorOptions.BuildFinal documentatorOptionsBuilder = SpecificationGeneratorOptions
                .builder()
                .basePath(options.getOrDefault(SPECIFICATION_GENERATOR_BASE_PATH_OPTION, "/"));

        Stream.of(StringUtils
                .split(StringUtils.defaultIfBlank(options.get(SPECIFICATION_GENERATOR_CONSUMES_OPTION), APPLICATION_JSON), ","))
                .forEach(documentatorOptionsBuilder::addConsumes);
        Stream.of(StringUtils
                .split(StringUtils.defaultIfBlank(options.get(SPECIFICATION_GENERATOR_PRODUCES_OPTION), APPLICATION_JSON), ","))
                .forEach(documentatorOptionsBuilder::addProduces);
        return documentatorOptionsBuilder.build();
    }

    private ICodeGeneratorOptions buildCodeGenerator(Map<String, String> options) {
        CodeGeneratorOptions.Builder builder = CodeGeneratorOptions.builder();

        ofNullable(options.get(CODE_GENERATOR_LANGUAGES))
                .filter(StringUtils::isNotBlank)
                .map(s -> StringUtils.split(s, ","))
                .ifPresent(builder::addLanguages);

        return builder.build();
    }

}
