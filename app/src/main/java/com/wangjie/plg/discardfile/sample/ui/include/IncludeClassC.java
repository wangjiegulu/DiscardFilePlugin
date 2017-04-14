package com.wangjie.plg.discardfile.sample.ui.include;

import com.wangjie.plg.discardfile.api.annotation.Discard;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/13/17.
 */
@Discard(applyParamValue = "true") // apply param => `isRelease = true`
public class IncludeClassC {

    /**
     * 因为IncludeClassC类增加了`@Discard`注解，所以该方法也会被discard。
     */
    public void onIncludeMethodC() {
        System.out.println("onIncludeMethodC...");
    }

    /**
     * 替换该方法的实现为：{System.out.println("onIncludeMethodC_2... injected!");}
     */
    @Discard(srcCode = "{System.out.println(\"onIncludeMethodC_2... injected!\");}")
    public void onIncludeMethodC_2() {
        System.out.println("onIncludeMethodC_2...");
    }

    /**
     * 替换该方法永远返回true
     */
    @Discard(srcCode = "{return true;}")
    public boolean onIncludeMethodC_3() {
        System.out.println("onIncludeMethodC_3...");
        return false;
    }

    /**
     * 因为IncludeClassC类增加了`@Discard`注解，所以该方法也会被discard。
     */
    public int onIncludeMethodC_4() {
        System.out.println("onIncludeMethodC_4...");
        return 100;
    }

    /**
     * 由于使用了`@Discard`注解进行显式地声明禁用了本地的discard，所以该方法不会被discard
     */
    @Discard(enable = false)
    public String onIncludeMethodC_5() {
        System.out.println("onIncludeMethodC_5...");
        return "hello world";
    }

    /**
     * 替换该方法永远返回"hello world"字符串
     */
    @Discard(srcCode = "{return \"hello world injected!\";}")
    public String onIncludeMethodC_6() {
        System.out.println("onIncludeMethodC_6...");
        return "hello world";
    }
}
