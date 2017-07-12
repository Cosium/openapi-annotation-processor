package com.cosium.openapi.annotation_processor;

import javax.tools.FileObject;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface FileManager {

    FileObject createResource(CharSequence relativeName);

    FileObject createResource(CharSequence pkg, CharSequence relativeName);

}
