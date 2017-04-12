package com.wangjie.plg.discardfile.api.annotation;

import com.wangjie.plg.discardfile.api.constant.DiscardConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Discard {
    /**
     * 是否启用Discard的条件参数，默认是"isRelease"
     */
    String applyParam() default DiscardConstant.APPLY_PARAM_DEFAULT;

    /**
     * 默认方法体代码块
     */
    String srcCode() default DiscardConstant.SRC_CODE_DEFAULT;

    /**
     * 需要make的类名
     */
    String[] makeClassNames() default {};
}
