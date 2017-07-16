package com.cosium.openapi.annotation_processor.option;

import com.cosium.openapi.annotation_processor.code.CodeGeneratorOptions;
import com.cosium.openapi.annotation_processor.code.ICodeGeneratorOptions;
import com.cosium.openapi.annotation_processor.specification.ISpecificationGeneratorOptions;
import com.cosium.openapi.annotation_processor.specification.SpecificationGeneratorOptions;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
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

    private static final String PREFIX = "com.cosium.openapi.";

    private static final String BASE_GENERATION_PACKAGE = PREFIX + "generation_package";

    private static final String SPECIFICATION_GENERATOR_PREFIX = PREFIX + "specification_generator.";

    private static final String SPECIFICATION_GENERATOR_TITLE_OPTION = SPECIFICATION_GENERATOR_PREFIX + "title";
    private static final String SPECIFICATION_GENERATOR_BASE_PATH_OPTION = SPECIFICATION_GENERATOR_PREFIX + "base_path";
    private static final String SPECIFICATION_GENERATOR_PRODUCES_OPTION = SPECIFICATION_GENERATOR_PREFIX + "produces";
    private static final String SPECIFICATION_GENERATOR_CONSUMES_OPTION = SPECIFICATION_GENERATOR_PREFIX + "consumes";

    private static final String CODE_GENERATOR_PREFIX = PREFIX + "code_generator.";

    private static final String CODE_GENERATOR_LANGUAGES = CODE_GENERATOR_PREFIX + "languages";

    private static final String APPLICATION_JSON = "application/json";

    public Set<String> getSupportedOptions() {
        return new LinkedHashSet<>(Arrays.asList(
                BASE_GENERATION_PACKAGE,
                SPECIFICATION_GENERATOR_TITLE_OPTION,
                SPECIFICATION_GENERATOR_BASE_PATH_OPTION,
                SPECIFICATION_GENERATOR_PRODUCES_OPTION,
                SPECIFICATION_GENERATOR_CONSUMES_OPTION,
                CODE_GENERATOR_LANGUAGES
        ));
    }

    public IOptions build(Map<String, String> options) {
        return Options
                .builder()
                .baseGenerationPackage(options.getOrDefault(BASE_GENERATION_PACKAGE, "com.cosium.openapi.generated"))
                .specificationGenerator(buildSpecificationGenerator(options))
                .codeGenerator(buildCodeGenerator(options))
                .build();
    }

    private ISpecificationGeneratorOptions buildSpecificationGenerator(Map<String, String> options) {
        SpecificationGeneratorOptions.BuildFinal documentatorOptionsBuilder = SpecificationGeneratorOptions
                .builder()
                .title(options.getOrDefault(SPECIFICATION_GENERATOR_TITLE_OPTION, StringUtils.EMPTY))
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
