package com.cosium.openapi.annotation_processor.parser.spring;

import com.cosium.openapi.annotation_processor.model.ParsedPath;
import com.cosium.openapi.annotation_processor.parser.PathParser;
import com.cosium.openapi.annotation_processor.parser.utils.PropertyUtils;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.lang.model.element.*;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
class SpringParser implements PathParser {

    private final PropertyUtils propertyUtils;

    SpringParser() {
        this.propertyUtils = new PropertyUtils();
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
                        executableElement -> getPathTemplate(executableElement.getAnnotation(RequestMapping.class))
                                .forEach(
                                        pathTemplate -> methodsByPathTemplate.getOrDefault(pathTemplate, new ArrayList<>()).add(executableElement)
                                )
                );

        Set<String> basePathTemplates = getPathTemplate(annotatedElement.getAnnotation(RequestMapping.class));
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
        operation.setOperationId(executableElement.getSimpleName().toString());

        executableElement.getParameters()
                .stream()
                .map(this::buildParameter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(operation::addParameter);

        Response okResponse = new Response();
        okResponse.schema(propertyUtils.from(executableElement.getReturnType()));
        operation.response(200, okResponse);

        return operation;
    }

    private Optional<Parameter> buildParameter(VariableElement variableElement) {
        if (variableElement.getAnnotation(RequestBody.class) != null) {
            return Optional.of(buildBodyParameter(variableElement));
        }
        PathVariable pathVariable = variableElement.getAnnotation(PathVariable.class);
        if (pathVariable != null) {
            return Optional.of(buildPathParameter(pathVariable.value()));
        }
        return Optional.empty();
    }

    private BodyParameter buildBodyParameter(VariableElement variableElement) {
        BodyParameter bodyParameter = new BodyParameter();
        bodyParameter.setName(variableElement.getSimpleName().toString());
        bodyParameter.setRequired(true);
        bodyParameter.setSchema(new ModelImpl());
        return bodyParameter;
    }

    private PathParameter buildPathParameter(String template) {
        PathParameter pathParameter = new PathParameter();
        pathParameter.setName(template);
        return pathParameter;
    }

    private Set<String> getPathTemplate(RequestMapping requestMapping) {
        if (requestMapping.value().length > 0) {
            return new HashSet<>(Arrays.asList(requestMapping.value()));
        } else {
            return new HashSet<>(Arrays.asList(requestMapping.path()));
        }
    }

}
