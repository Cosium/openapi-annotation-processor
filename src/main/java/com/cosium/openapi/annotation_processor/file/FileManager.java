package com.cosium.openapi.annotation_processor.file;

import javax.tools.FileObject;
import java.nio.file.NoSuchFileException;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface FileManager {

    FileObject getResource(CharSequence relativeName) throws NoSuchFileException;

    FileObject getResource(CharSequence pkg, CharSequence relativeName) throws NoSuchFileException;

    FileObject createResource(CharSequence relativeName);

    FileObject createResource(CharSequence pkg, CharSequence relativeName);

}
