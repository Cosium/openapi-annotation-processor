package com.cosium.openapi.annotation_processor.code;

import org.immutables.value.Value;

import java.util.List;

/**
 * Created on 12/07/17.
 *
 * @author Reda.Housni-Alaoui
 */
@Value.Immutable
public interface ICodeGeneratorOptions {

    /**
     * @return Languages for which generation should occur
     */
    List<String> languages();

}
