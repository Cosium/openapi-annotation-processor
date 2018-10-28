package com.cosium.openapi.annotation_processor.parser.utils;


import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.DoubleProperty;
import io.swagger.models.properties.FileProperty;
import io.swagger.models.properties.FloatProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.LongProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import io.swagger.models.properties.UUIDProperty;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class PropertyUtils {

	private static final List<Class<? extends Property>> SIMPLE_PROPERTIES = Arrays.asList(
			BooleanProperty.class,
			IntegerProperty.class,
			LongProperty.class,
			FloatProperty.class,
			DoubleProperty.class,
			DateProperty.class,
			UUIDProperty.class,
			StringProperty.class
	);

	private final Types typeUtils;
	private final Elements elementUtils;

	public PropertyUtils(Types typeUtils, Elements elementUtils) {
		requireNonNull(typeUtils);
		requireNonNull(elementUtils);

		this.typeUtils = typeUtils;
		this.elementUtils = elementUtils;
	}

	public Model buildModel(TypeMirror type) {
		ModelImpl schema = new ModelImpl().type(ModelImpl.OBJECT);
		getPublicFields(type)
				.forEach(field -> addFieldToSchema(schema, field));
		return schema;
	}

	private void addFieldToSchema(ModelImpl schema, Element field) {
		Property property = toProperty(field.asType());
		property.setRequired(true);
		schema.property(field.getSimpleName().toString(), property);
	}

	private List<Element> getPublicFields(TypeMirror type) {
		Element element = typeUtils.asElement(type);
		return element
				.getEnclosedElements()
				.stream()
				.filter(o -> o.getModifiers().contains(Modifier.PUBLIC))
				.filter(o -> ElementKind.FIELD.equals(o.getKind()))
				.collect(Collectors.toList());
	}

	public Property toSimpleProperty(Element element) {
		return ofNullable(toProperty(element))
				.filter(property -> SIMPLE_PROPERTIES.contains(property.getClass()))
				.orElseGet(StringProperty::new);
	}

	public Property toProperty(Class<?> clazz) {
		return toProperty(elementUtils.getTypeElement(clazz.getCanonicalName()));
	}

	public Property toProperty(Element element) {
		return toProperty(element.asType());
	}

	public Property toProperty(TypeMirror type) {
		Property property;
		switch (type.getKind()) {
			case BOOLEAN:
				property = new BooleanProperty();
				break;
			case SHORT:
			case INT:
			case BYTE:
			case CHAR:
				property = new IntegerProperty();
				break;
			case LONG:
				property = new LongProperty();
				break;
			case FLOAT:
				property = new FloatProperty();
				break;
			case DOUBLE:
				property = new DoubleProperty();
				break;
			case ARRAY:
				property = new ArrayProperty();
				break;
			default:
				property = new ObjectProperty();
				break;
		}

		if (property instanceof ObjectProperty) {
			if (isSubtype(type, String.class)) {
				property = new StringProperty();
			} else if (isSubtype(type, Date.class)) {
				property = new DateProperty();
			} else if (isSubtype(type, UUID.class)) {
				property = new UUIDProperty();
			} else if (isSubtype(type, File.class)) {
				property = new FileProperty();
			} else if (isSubtype(type, Collection.class)) {
				property = new ArrayProperty();
			} else if (isSubtype(type, Map.class)) {
				property = new MapProperty();
			} else if (isSubtype(type, Boolean.class)) {
				property = new BooleanProperty();
			} else if (isSubtype(type, Long.class)) {
				property = new LongProperty();
			} else if (isSubtype(type, Float.class)) {
				property = new FloatProperty();
			} else if (isSubtype(type, Double.class)) {
				property = new DoubleProperty();
			} else if (isSubtype(type, Short.class)
					|| isSubtype(type, Integer.class)
					|| isSubtype(type, Byte.class)
					|| isSubtype(type, Character.class)) {
				property = new IntegerProperty();
			}
		}

		if (property instanceof ArrayProperty) {
			ArrayProperty arrayProperty = (ArrayProperty) property;
			arrayProperty.setItems(new ObjectProperty());
		} else if (property instanceof MapProperty) {
			MapProperty mapProperty = (MapProperty) property;
			mapProperty.setAdditionalProperties(new ObjectProperty());
		}

		return property;
	}

	private boolean isSubtype(TypeMirror t1, Class<?> t2) {
		return typeUtils.isSubtype(
				typeUtils.erasure(t1),
				typeUtils.erasure(elementUtils.getTypeElement(t2.getCanonicalName()).asType())
		);
	}


}
