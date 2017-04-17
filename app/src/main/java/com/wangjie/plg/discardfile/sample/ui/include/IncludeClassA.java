package com.wangjie.plg.discardfile.sample.ui.include;

import com.wangjie.plg.discardfile.api.annotation.Discard;
import com.wangjie.plg.discardfile.sample.constants.ApplyConstants;

/**
 * gradle assembleFullDebug -DisDiscardA=true
 *
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/12/17.
 */
public class IncludeClassA {
    @Discard(apply = ApplyConstants.DISABLE._TRUE)
    public void onIncludeMethodA() {
        System.out.println("onIncludeMethodA...");
    }
}
