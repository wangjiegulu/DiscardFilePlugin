package com.wangjie.plg.discardfile.sample.ui.include;

import com.wangjie.plg.discardfile.api.annotation.Discard;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/13/17.
 */
@Discard(applyParamValue = "false")
public class IncludeClassC {
    public void onIncludeMethodC(){
        System.out.println("onIncludeMethodC...");
    }


    @Discard(srcCode = "{System.out.println(\"onIncludeMethodC_2... injected!\");}")
    public void onIncludeMethodC_2() {
        System.out.println("onIncludeMethodC_2...");
    }

    @Discard(srcCode = "{return true;}")
    public boolean onIncludeMethodC_3() {
        System.out.println("onIncludeMethodC_3...");
        return false;
    }

    public int onIncludeMethodC_4() {
        System.out.println("onIncludeMethodC_4...");
        return 100;
    }

    @Discard(enable = false)
    public String onIncludeMethodC_5() {
        System.out.println("onIncludeMethodC_5...");
        return "hello world";
    }

    @Discard(srcCode = "{return \"hello world injected!\";}")
    public String onIncludeMethodC_6() {
        System.out.println("onIncludeMethodC_6...");
        return "hello world";
    }
}
