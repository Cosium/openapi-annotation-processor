package com.cosium.openapi.annotation_processor;

import com.cosium.openapi.annotation_processor.documentator.openapi_20.OpenAPI20DocumentatorFactory;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import com.cosium.openapi.annotation_processor.parser.spring.SpringParserFactory;
import com.google.auto.service.AutoService;

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
    private final List<DocumentatorFactory> documentatorFactories = new ArrayList<>();

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    public OpenAPIProcessor() {
        parserFactories.add(new SpringParserFactory());
        documentatorFactories.add(new OpenAPI20DocumentatorFactory());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return parserFactories
                .stream()
                .map(PathParserFactory::getSupportedAnnotation)
                .collect(Collectors.toSet());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        AtomicReference<Element> currentAnnotatedElement = new AtomicReference<>();

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

        documentatorFactories
                .stream()
                .map(documentatorFactory -> documentatorFactory.build(filer))
                .forEach(documentator -> documentator.document(parsedPaths));

        return true;
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
