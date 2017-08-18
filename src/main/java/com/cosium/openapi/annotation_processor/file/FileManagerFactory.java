package com.cosium.openapi.annotation_processor.file;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17/08/17.
 *
 * @author Reda.Housni-Alaoui
 */
public class FileManagerFactory {

    private final Filer filer;
    private final String baseGenerationPackage;

    public FileManagerFactory(Filer filer, String baseGenerationPackage) {
        requireNonNull(filer);
        requireNonNull(baseGenerationPackage);
        this.filer = filer;
        this.baseGenerationPackage = baseGenerationPackage;
    }

    public FileManager build(String packageName, Collection<Element> originatingElements) {
        return new DefaultFileManager(baseGenerationPackage + "." + packageName, filer, originatingElements);
    }

}
