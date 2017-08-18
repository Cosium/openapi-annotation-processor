package com.cosium.openapi.annotation_processor.model;

import io.swagger.models.Path;
import io.swagger.util.Json;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created on 18/08/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class ParsedPathTest {

    @Test
    public void canBeSerializedAndDeserialized() throws Exception {
        ParsedPath parsedPath = new ParsedPath("foo", new Path());
        String json = Json.mapper().writeValueAsString(parsedPath);
        ParsedPath deserializedParsedPath = Json.mapper().readValue(json, ParsedPath.class);
        assertThat(deserializedParsedPath.getPathTemplate()).isEqualTo("foo");
        assertThat(deserializedParsedPath.getPath()).isNotNull();
    }

}