package com.cosium.openapi.annotation_processor;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
class DefaultFileManager implements FileManager {

    private final String basePackage;
    private final Filer filer;

    DefaultFileManager(String basePackage, Filer filer) {
        requireNonNull(basePackage);
        requireNonNull(filer);

        this.basePackage = basePackage;
        this.filer = filer;
    }

    @Override
    public FileObject createResource(CharSequence relativeName) {
        return createResource(StringUtils.EMPTY, relativeName);
    }

    @Override
    public FileObject createResource(CharSequence pkg, CharSequence relativeName) {
        try {
            return filer.createResource(StandardLocation.CLASS_OUTPUT, basePackage + "." + pkg, relativeName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
