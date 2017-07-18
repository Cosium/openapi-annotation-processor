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
     * @return True if each language code should be written in a separate folder named as the language
     */
    boolean oneGenerationFolderPerLanguage();

    /**
     * @return Languages for which generation should occur
     */
    List<String> languages();

}
