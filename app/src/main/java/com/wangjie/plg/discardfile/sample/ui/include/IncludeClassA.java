package com.wangjie.plg.discardfile.sample.ui.include;

import com.wangjie.plg.discardfile.api.annotation.Discard;

/**
 * gradle assembleFullDebug -DisDiscardA=true
 *
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/12/17.
 */
public class IncludeClassA {
    @Discard(applyParam = "isDiscardA")
    public void onIncludeMethodA() {
        System.out.println("onIncludeMethodA...");
    }
}
