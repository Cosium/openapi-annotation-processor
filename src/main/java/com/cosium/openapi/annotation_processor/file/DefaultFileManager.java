package com.cosium.openapi.annotation_processor.file;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Collection;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultFileManager implements FileManager {

    private final String basePackage;
    private final Filer filer;
    private final Collection<Element> originatingElements;

    public DefaultFileManager(String basePackage, Filer filer, Collection<Element> originatingElements) {
        requireNonNull(basePackage);
        requireNonNull(filer);
        requireNonNull(originatingElements);

        this.basePackage = basePackage;
        this.filer = filer;
        this.originatingElements = Collections.unmodifiableCollection(originatingElements);
    }

    @Override
    public FileObject getResource(CharSequence relativeName) throws NoSuchFileException {
        return getResource(null, relativeName);
    }

    @Override
    public FileObject getResource(CharSequence pkg, CharSequence relativeName) throws NoSuchFileException {
        try {
            String packageExtension = StringUtils.isNotBlank(pkg) ? "." + pkg : StringUtils.EMPTY;
            return filer.getResource(
                    StandardLocation.CLASS_OUTPUT,
                    basePackage + packageExtension,
                    relativeName
            );
        } catch (NoSuchFileException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileObject createResource(CharSequence relativeName) {
        return createResource(null, relativeName);
    }

    @Override
    public FileObject createResource(CharSequence pkg, CharSequence relativeName) {
        try {
            String packageExtension = StringUtils.isNotBlank(pkg) ? "." + pkg : StringUtils.EMPTY;
            return filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    basePackage + packageExtension,
                    relativeName,
                    originatingElements.toArray(new Element[originatingElements.size()])
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
