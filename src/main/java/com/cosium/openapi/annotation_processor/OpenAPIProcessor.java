package com.cosium.openapi.annotation_processor;

import com.cosium.openapi.annotation_processor.documentator.DefaultSpecificationGenerator;
import com.cosium.openapi.annotation_processor.documentator.SpecificationGenerator;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import com.cosium.openapi.annotation_processor.option.IOptions;
import com.cosium.openapi.annotation_processor.option.OptionsBuilder;
import com.cosium.openapi.annotation_processor.parser.PathParser;
import com.cosium.openapi.annotation_processor.parser.PathParserFactory;
import com.cosium.openapi.annotation_processor.parser.spring.SpringParserFactory;
import com.google.auto.service.AutoService;
import io.swagger.models.Swagger;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
@AutoService(Processor.class)
public class OpenAPIProcessor extends AbstractProcessor {

    private final List<PathParserFactory> parserFactories = new ArrayList<>();
    private final OptionsBuilder optionsBuilder = new OptionsBuilder();

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    private SpecificationGenerator specificationGenerator;

    public OpenAPIProcessor() {
        parserFactories.add(new SpringParserFactory());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return parserFactories
                .stream()
                .map(PathParserFactory::getSupportedAnnotation)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getSupportedOptions() {
        return optionsBuilder.getSupportedOptions();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();

        IOptions options = optionsBuilder.build(processingEnv.getOptions());
        this.specificationGenerator = new DefaultSpecificationGenerator(options.specificationGenerator());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        AtomicReference<Element> currentAnnotatedElement = new AtomicReference<>();
        doProcess(roundEnv, currentAnnotatedElement);
        return true;
    }

    private void doProcess(RoundEnvironment roundEnv, AtomicReference<Element> currentAnnotatedElement) {
        List<ParsedPath> parsedPaths = parserFactories
                .stream()
                .map(ParserHolder::new)
                .flatMap(parserHolder -> {
                    TypeElement annotation = elementUtils.getTypeElement(parserHolder.getSupportedAnnotation());
                    PathParser pathParser = parserHolder.getPathParser();
                    return roundEnv
                            .getElementsAnnotatedWith(annotation)
                            .stream()
                            .peek(currentAnnotatedElement::set)
                            .map(pathParser::parse)
                            .flatMap(Collection::stream);
                })
                .collect(Collectors.toList());

        Swagger swagger = specificationGenerator.generate(parsedPaths);


    }

    private class ParserHolder {
        private final String supportedAnnotation;
        private final PathParser pathParser;

        private ParserHolder(PathParserFactory pathParserFactory) {
            requireNonNull(pathParserFactory);
            this.supportedAnnotation = pathParserFactory.getSupportedAnnotation();
            this.pathParser = pathParserFactory.build(typeUtils, elementUtils);
        }

        private PathParser getPathParser() {
            return pathParser;
        }

        private String getSupportedAnnotation() {
            return supportedAnnotation;
        }
    }
}
