package com.wangjie.plg.discardfile.sample.exclude;

import com.wangjie.plg.discardfile.api.annotation.Discard;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/13/17.
 */
@Discard(applyParamValue = "false")
public class ExcludeClassC {
    public void onExcludeMethodC_1() {
        System.out.println("onExcludeMethodC_1...");
    }


}
