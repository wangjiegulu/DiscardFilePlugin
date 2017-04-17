package com.wangjie.plg.discardfile.api.annotation;

import com.wangjie.plg.discardfile.api.constant.DiscardConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Discard {
    /**
     * 是否启用Discard的条件参数，格式：key==exceptValue
     */
    String apply();

    /**
     * Method body for replace.
     *
     * Only for method.
     */
    String srcCode() default DiscardConstant.SRC_CODE_DEFAULT;

    /**
     * Class names of need to make.
     */
    String[] makeClassNames() default {};

    /**
     * If false, Discard will not work.
     */
    boolean enable() default true;
}
