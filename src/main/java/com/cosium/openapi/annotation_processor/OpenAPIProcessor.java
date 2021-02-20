package com.cosium.openapi.annotation_processor;

import static java.util.Objects.requireNonNull;

import com.cosium.logging.annotation_processor.AbstractLoggingProcessor;
import com.cosium.openapi.annotation_processor.code.CodeGenerator;
import com.cosium.openapi.annotation_processor.code.CodeGeneratorFactory;
import com.cosium.openapi.annotation_processor.file.FileManager;
import com.cosium.openapi.annotation_processor.file.FileManagerFactory;
import com.cosium.openapi.annotation_processor.loader.DefaultServiceLoader;
import com.cosium.openapi.annotation_processor.loader.ServiceLoader;
import com.cosium.openapi.annotation_processor.model.ParsedPath;
import com.cosium.openapi.annotation_processor.option.IOptions;
import com.cosium.openapi.annotation_processor.option.OptionsBuilder;
import com.cosium.openapi.annotation_processor.parser.PathParser;
import com.cosium.openapi.annotation_processor.parser.PathParserFactory;
import com.cosium.openapi.annotation_processor.parser.spring.SpringParserFactory;
import com.cosium.openapi.annotation_processor.specification.SpecificationGenerator;
import com.cosium.openapi.annotation_processor.specification.SpecificationGeneratorFactory;
import com.google.auto.service.AutoService;
import io.swagger.models.Swagger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
@AutoService(Processor.class)
public class OpenAPIProcessor extends AbstractLoggingProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAPIProcessor.class);

    private final AtomicInteger roundNumber = new AtomicInteger();
    private final List<PathParserFactory> parserFactories = new ArrayList<>();
    private final OptionsBuilder optionsBuilder = new OptionsBuilder();
    private final ServiceLoader serviceLoader = new DefaultServiceLoader();

    private Types typeUtils;
    private Elements elementUtils;
    private Messager messager;

    private List<ParserHolder> pathParsers;
    private FileManagerFactory fileManagerFactory;
    private SpecificationGeneratorFactory specificationGeneratorFactory;
    private CodeGeneratorFactory codeGeneratorFactory;

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

        this.pathParsers = parserFactories
                .stream()
                .map(ParserHolder::new)
                .collect(Collectors.toList());
        IOptions options = optionsBuilder.build(processingEnv.getOptions());
        this.fileManagerFactory = new FileManagerFactory(processingEnv.getFiler(), options.baseGenerationPackage());
        this.specificationGeneratorFactory = new SpecificationGeneratorFactory(options.specificationGenerator());
        this.codeGeneratorFactory = new CodeGeneratorFactory(options.codeGenerator(), serviceLoader);
    }

    @Override
    protected boolean doProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        boolean lastRound = roundEnv.processingOver();
        if (lastRound) {
            LOG.debug("Processing last round");
        } else {
            LOG.debug("Processing");
        }
        AtomicReference<Element> currentAnnotatedElement = new AtomicReference<>();
        try {
            doProcess(roundEnv, currentAnnotatedElement, new RoundDescriptor(roundNumber.incrementAndGet(), lastRound));
        } catch (Throwable e) {
            LOG.error(e.getMessage(), e);
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage(), currentAnnotatedElement.get());
            if(!(e instanceof Exception)){
                throw e;
            }
        }
        return true;
    }

    private void doProcess(RoundEnvironment roundEnv, AtomicReference<Element> currentAnnotatedElement, RoundDescriptor roundDescriptor) {
        Collection<Element> originatingElements = new HashSet<>();
        List<ParsedPath> parsedPaths = pathParsers
                .stream()
                .flatMap(parserHolder -> {
                    TypeElement annotation = elementUtils.getTypeElement(parserHolder.getSupportedAnnotation());
                    PathParser pathParser = parserHolder.getPathParser();
                    return roundEnv
                            .getElementsAnnotatedWith(annotation)
                            .stream()
                            .peek(currentAnnotatedElement::set)
                            .peek(originatingElements::add)
                            .peek(o -> LOG.debug("Parsing paths from {}", o))
                            .map(pathParser::parse)
                            .flatMap(Collection::stream);
                })
                .collect(Collectors.toList());
        currentAnnotatedElement.set(null);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Generating specification from {} parsed paths", parsedPaths.size());
        }

        FileManager specificationFileManager = fileManagerFactory.build("specification", originatingElements);
        SpecificationGenerator specificationGenerator = specificationGeneratorFactory.build(specificationFileManager);
        Swagger specification = specificationGenerator.generate(parsedPaths, roundDescriptor);
        if (roundDescriptor.isLast()) {
            FileManager codeGeneratorFileManager = fileManagerFactory.build("code", originatingElements);
            CodeGenerator codeGenerator = codeGeneratorFactory.build(codeGeneratorFileManager);
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

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}
