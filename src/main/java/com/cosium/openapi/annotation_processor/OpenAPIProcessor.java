package com.cosium.openapi.annotation_processor;

import com.cosium.openapi.annotation_processor.documentator.DocumentatorFactory;
import com.cosium.openapi.annotation_processor.documentator.DocumentatorOptions;
import com.cosium.openapi.annotation_processor.documentator.IDocumentatorOptions;
import com.cosium.openapi.annotation_processor.documentator.openapi_20.OpenAPI20DocumentatorFactory;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import com.cosium.openapi.annotation_processor.parser.PathParser;
import com.cosium.openapi.annotation_processor.parser.PathParserFactory;
import com.cosium.openapi.annotation_processor.parser.spring.SpringParserFactory;
import com.google.auto.service.AutoService;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
@AutoService(Processor.class)
public class OpenAPIProcessor extends AbstractProcessor {

    private static final String BASE_PATH_OPTION = "basePath";
    private static final String PRODUCES_OPTION = "produces";
    private static final String CONSUMES_OPTION = "consumes";

    private final List<PathParserFactory> parserFactories = new ArrayList<>();
    private final List<DocumentatorFactory> documentatorFactories = new ArrayList<>();

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    private IDocumentatorOptions documentatorOptions;

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
    public Set<String> getSupportedOptions() {
        return new HashSet<>(Arrays.asList(BASE_PATH_OPTION, PRODUCES_OPTION, CONSUMES_OPTION));
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();

        Map<String, String> options = processingEnv.getOptions();

        DocumentatorOptions.BuildFinal documentatorOptionsBuilder = DocumentatorOptions
                .builder()
                .basePath(options.getOrDefault(BASE_PATH_OPTION, "/"));

        Stream.of(StringUtils.split(options.get(CONSUMES_OPTION), ","))
                .forEach(documentatorOptionsBuilder::addConsumes);
        Stream.of(StringUtils.split(options.get(PRODUCES_OPTION), ","))
                .forEach(documentatorOptionsBuilder::addProduces);

        this.documentatorOptions = documentatorOptionsBuilder.build();
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

        documentatorFactories
                .stream()
                .map(documentatorFactory -> documentatorFactory.build(documentatorOptions))
                .forEach(documentator -> documentator.document(parsedPaths));
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
