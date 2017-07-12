package com.cosium.openapi.annotation_processor;

import javax.annotation.processing.Filer;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
public interface DocumentatorFactory {

    /**
     * @return A new documentator
     */
    Documentator build(Filer filer);

}
