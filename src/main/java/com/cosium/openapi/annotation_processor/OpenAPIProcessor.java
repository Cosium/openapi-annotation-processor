package com.cosium.openapi.annotation_processor;

import com.cosium.openapi.annotation_processor.code.CodeGenerator;
import com.cosium.openapi.annotation_processor.code.DefaultCodeGenerator;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import com.cosium.openapi.annotation_processor.option.IOptions;
import com.cosium.openapi.annotation_processor.option.OptionsBuilder;
import com.cosium.openapi.annotation_processor.parser.PathParser;
import com.cosium.openapi.annotation_processor.parser.PathParserFactory;
import com.cosium.openapi.annotation_processor.parser.spring.SpringParserFactory;
import com.cosium.openapi.annotation_processor.specification.DefaultSpecificationGenerator;
import com.cosium.openapi.annotation_processor.specification.SpecificationGenerator;
import com.google.auto.service.AutoService;
import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
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

    private static final Logger LOG = LoggerFactory.getLogger(OpenAPIProcessor.class);

    private final List<PathParserFactory> parserFactories = new ArrayList<>();
    private final OptionsBuilder optionsBuilder = new OptionsBuilder();

    private Types typeUtils;
    private Elements elementUtils;
    private Messager messager;

    private SpecificationGenerator specificationGenerator;
    private CodeGenerator codeGenerator;

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
        this.messager = processingEnv.getMessager();

        Filer filer = processingEnv.getFiler();
        IOptions options = optionsBuilder.build(processingEnv.getOptions());
        this.specificationGenerator = new DefaultSpecificationGenerator(
                options.specificationGenerator(),
                new DefaultFileManager(options.baseGenerationPackage() + ".generated.specification", filer)
        );
        this.codeGenerator = new DefaultCodeGenerator(
                options.codeGenerator(),
                new DefaultFileManager(options.baseGenerationPackage() + ".generated.code", filer)
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean lastRound = roundEnv.processingOver();
        if (lastRound) {
            LOG.debug("Processing last round");
        } else {
            LOG.debug("Processing");
        }
        AtomicReference<Element> currentAnnotatedElement = new AtomicReference<>();
        try {
            doProcess(roundEnv, currentAnnotatedElement, lastRound);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), currentAnnotatedElement.get());
        }
        return true;
    }

    private void doProcess(RoundEnvironment roundEnv, AtomicReference<Element> currentAnnotatedElement, boolean lastRound) {
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
                            .peek(o -> LOG.debug("Parsing paths from {}", o))
                            .map(pathParser::parse)
                            .flatMap(Collection::stream);
                })
                .collect(Collectors.toList());
        currentAnnotatedElement.set(null);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Generating specification from {} parsed paths", parsedPaths.size());
        }
        Swagger specification = specificationGenerator.generate(parsedPaths, lastRound);
        if (lastRound) {
            LOG.debug("Generating code");
            codeGenerator.generate(specification);
        }
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
