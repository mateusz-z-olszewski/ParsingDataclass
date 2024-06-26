package dev.olszewski.parsingdataclass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be applied to a constructor or a static method to annotate
 * that instances should be generated using that executable.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Parses {
    /**
     * Provides a regular expression for the parsing factory.
     * @return regex pattern string.
     */
    String value();

}
