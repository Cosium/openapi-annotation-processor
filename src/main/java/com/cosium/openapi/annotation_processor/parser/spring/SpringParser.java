package com.cosium.openapi.annotation_processor.parser.spring;

import com.cosium.openapi.annotation_processor.model.ParsedPath;
import com.cosium.openapi.annotation_processor.parser.PathParser;
import com.cosium.openapi.annotation_processor.parser.utils.AnnotationUtils;
import com.cosium.openapi.annotation_processor.parser.utils.PropertyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
	private final AnnotationUtils annotationUtils;

	SpringParser(PropertyUtils propertyUtils, AnnotationUtils annotationUtils) {
		requireNonNull(propertyUtils);
		requireNonNull(annotationUtils);
		this.propertyUtils = propertyUtils;
		this.annotationUtils = annotationUtils;
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
				.filter(this::isNotHiddenOperation)
				.forEach(executableElement -> {
					Operation operation = buildOperation(executableElement);
					RequestMapping requestMapping = executableElement.getAnnotation(RequestMapping.class);
					addOperation(path, operation, requestMapping);
				});

		return path;
	}

	private boolean isNotHiddenOperation(ExecutableElement executableElement) {
		return !ofNullable(executableElement.getAnnotation(ApiOperation.class))
				.map(ApiOperation::hidden)
				.orElse(false);
	}

	private void addOperation(Path path, Operation operation, RequestMapping requestMapping) {
		RequestMethod[] methods = requestMapping.method();
		if (methods.length == 0) {
			methods = new RequestMethod[]{RequestMethod.GET};
		}
		Stream.of(methods)
				.forEach(requestMethod -> path.set(requestMethod.name().toLowerCase(), operation));
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
		Property returnProperty = ofNullable(executableElement.getAnnotation(ApiOperation.class))
				.map(apiOperation -> annotationUtils.extractType(apiOperation, ApiOperation::response))
				.filter(returnType -> returnType.getKind() != TypeKind.VOID)
				.map(propertyUtils::toProperty)
				.orElseGet(() -> propertyUtils.toProperty(executableElement.getReturnType()));
		okResponse.schema(returnProperty);
		operation.response(200, okResponse);

		ofNullable(executableElement.getAnnotation(Deprecated.class))
				.ifPresent(deprecated -> operation.deprecated(true));

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
		RequestParam requestParam = variableElement.getAnnotation(RequestParam.class);
		if (requestParam != null) {
			return Optional.of(buildQueryParameter(variableElement, requestParam));
		}
		return Optional.empty();
	}

	private BodyParameter buildBodyParameter(VariableElement variableElement) {
		BodyParameter bodyParameter = new BodyParameter();
		bodyParameter.setName(variableElement.getSimpleName().toString());
		bodyParameter.setRequired(true);
		bodyParameter.setSchema(propertyUtils.buildModel(variableElement.asType()));
		return bodyParameter;
	}

	private QueryParameter buildQueryParameter(VariableElement variableElement, RequestParam requestParam) {
		Property property = ofNullable(propertyUtils.toSimpleProperty(variableElement))
				.filter(prop -> !(prop instanceof ObjectProperty))
				.orElseGet(StringProperty::new);
		return new QueryParameter()
				.name(requestParam.value())
				.required(requestParam.required())
				.property(property);
	}

	private PathParameter buildPathParameter(VariableElement variableElement, PathVariable pathVariable) {
		Property property = ofNullable(propertyUtils.toSimpleProperty(variableElement))
				.filter(prop -> !(prop instanceof ObjectProperty))
				.orElseGet(StringProperty::new);
		return new PathParameter()
				.name(pathVariable.value())
				.required(pathVariable.required())
				.property(property);
	}

	private Set<String> getPathTemplates(RequestMapping requestMapping) {
		if (requestMapping.value().length > 0) {
			return new HashSet<>(Arrays.asList(requestMapping.value()));
		} else if (requestMapping.path().length > 0) {
			return new HashSet<>(Arrays.asList(requestMapping.path()));
		} else {
			return Collections.singleton(StringUtils.EMPTY);
		}
	}

}
