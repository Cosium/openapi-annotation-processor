package com.cosium.openapi.annotation_processor.specification;

import com.cosium.openapi.annotation_processor.model.ParsedPath;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.swagger.models.Swagger;

import javax.tools.FileObject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18/08/17.
 *
 * @author Reda.Housni-Alaoui
 */
abstract class SwaggerUtils {

    public static void write(ObjectWriter objectWriter, FileObject fileObject, Object object) {
        try (Writer writer = fileObject.openWriter()) {
            objectWriter.writeValue(writer, object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ParsedPath> read(ObjectMapper objectMapper, FileObject fileObject) throws FileNotFoundException {
        try (Reader reader = fileObject.openReader(false)) {
            return objectMapper.readValue(reader, objectMapper.getTypeFactory().constructCollectionType(List.class, ParsedPath.class));
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ParsedPath> toParsedPaths(Swagger swagger) {
        List<ParsedPath> parsedPaths = new ArrayList<>();
        if (swagger.getPaths() != null) {
            swagger.getPaths().forEach((pathTemplate, path) -> parsedPaths.add(new ParsedPath(pathTemplate, path)));
        }
        return parsedPaths;
    }

    public static void addParsedPaths(List<ParsedPath> parsedPaths, Swagger swagger) {
        parsedPaths.forEach(parsedPath -> swagger.path(parsedPath.getPathTemplate(), parsedPath.getPath()));
    }

}
