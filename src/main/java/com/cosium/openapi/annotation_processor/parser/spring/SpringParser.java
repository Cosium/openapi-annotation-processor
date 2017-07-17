package com.cosium.openapi.annotation_processor.parser.spring;

import com.cosium.openapi.annotation_processor.model.ParsedPath;
import com.cosium.openapi.annotation_processor.parser.PathParser;
import com.cosium.openapi.annotation_processor.parser.utils.PropertyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.element.*;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
class SpringParser implements PathParser {

    private static final Logger LOG = LoggerFactory.getLogger(SpringParser.class);

    private final PropertyUtils propertyUtils;

    SpringParser(PropertyUtils propertyUtils) {
        requireNonNull(propertyUtils);
        this.propertyUtils = propertyUtils;
    }

    @Override
    public List<ParsedPath> parse(Element annotatedElement) {
        if (annotatedElement.getKind() != ElementKind.CLASS) {
            return emptyList();
        }
        if (annotatedElement.getModifiers().contains(Modifier.ABSTRACT)) {
            return emptyList();
        }

        Map<String, List<ExecutableElement>> methodsByPathTemplate = new HashMap<>();

        annotatedElement
                .getEnclosedElements()
                .stream()
                .filter(o -> o.getKind() == ElementKind.METHOD)
                .filter(o -> nonNull(o.getAnnotation(RequestMapping.class)))
                .map(ExecutableElement.class::cast)
                .forEach(
                        executableElement -> getPathTemplates(executableElement.getAnnotation(RequestMapping.class))
                                .stream()
                                .peek(pathTemplate -> LOG.debug("Extracted path template '{}'", pathTemplate))
                                .forEach(pathTemplate -> {
                                    methodsByPathTemplate.computeIfAbsent(pathTemplate, pt -> new ArrayList<>());
                                    methodsByPathTemplate.get(pathTemplate).add(executableElement);
                                })
                );

        LOG.debug("Found methods by template {}", methodsByPathTemplate);

        Set<String> basePathTemplates = getPathTemplates(annotatedElement.getAnnotation(RequestMapping.class));
        LOG.debug("Found base path templates {}", basePathTemplates);
        List<ParsedPath> parsedPaths = new ArrayList<>();
        methodsByPathTemplate
                .forEach((pathTemplate, executableElements) -> {
                    Path path = buildPath(executableElements);
                    basePathTemplates.forEach(basePathTemplate -> parsedPaths.add(new ParsedPath(basePathTemplate + pathTemplate, path)));
                });
        return parsedPaths;
    }

    private Path buildPath(List<ExecutableElement> executableElements) {
        Path path = new Path();
        executableElements
                .stream()
                .peek(executableElement -> LOG.debug("Building path for {}", executableElement))
                .forEach(executableElement -> {
                    Operation operation = buildOperation(executableElement);
                    RequestMapping requestMapping = executableElement.getAnnotation(RequestMapping.class);
                    addOperation(path, operation, requestMapping);
                });

        return path;
    }

    private void addOperation(Path path, Operation operation, RequestMapping requestMapping) {
        Stream.of(requestMapping.method()).forEach(requestMethod -> path.set(requestMethod.name().toLowerCase(), operation));
    }

    private Operation buildOperation(ExecutableElement executableElement) {
        Operation operation = new Operation();
        String operationId = ofNullable(executableElement.getAnnotation(ApiOperation.class))
                .map(ApiOperation::nickname)
                .filter(StringUtils::isNotBlank)
                .orElse(executableElement.getSimpleName().toString());
        operation.setOperationId(operationId);

        Element controller = executableElement.getEnclosingElement();
        Optional<Api> apiAnnotation = ofNullable(controller.getAnnotation(Api.class));
        apiAnnotation.ifPresent(annotation -> Stream.of(annotation.tags())
                .filter(StringUtils::isNotBlank)
                .forEach(operation::addTag));

        executableElement.getParameters()
                .stream()
                .map(this::buildParameter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(operation::addParameter);

        Response okResponse = new Response();
        okResponse.schema(propertyUtils.toProperty(executableElement.getReturnType()));
        operation.response(200, okResponse);

        return operation;
    }

    private Optional<Parameter> buildParameter(VariableElement variableElement) {
        if (variableElement.getAnnotation(RequestBody.class) != null) {
            return Optional.of(buildBodyParameter(variableElement));
        }
        PathVariable pathVariable = variableElement.getAnnotation(PathVariable.class);
        if (pathVariable != null) {
            return Optional.of(buildPathParameter(variableElement, pathVariable));
        }
        return Optional.empty();
    }

    private BodyParameter buildBodyParameter(VariableElement variableElement) {
        BodyParameter bodyParameter = new BodyParameter();
        bodyParameter.setName(variableElement.getSimpleName().toString());
        bodyParameter.setRequired(true);
        bodyParameter.setSchema(new ModelImpl().type(ModelImpl.OBJECT));
        return bodyParameter;
    }

    private PathParameter buildPathParameter(VariableElement variableElement, PathVariable pathVariable) {
        Property property = ofNullable(propertyUtils.toSimpleProperty(variableElement))
                .filter(prop -> !(prop instanceof ObjectProperty))
                .orElseGet(StringProperty::new);
        return new PathParameter()
                .name(pathVariable.value())
                .required(true)
                .property(property);
    }

    private Set<String> getPathTemplates(RequestMapping requestMapping) {
        if (requestMapping.value().length > 0) {
            return new HashSet<>(Arrays.asList(requestMapping.value()));
        } else {
            return new HashSet<>(Arrays.asList(requestMapping.path()));
        }
    }

}
