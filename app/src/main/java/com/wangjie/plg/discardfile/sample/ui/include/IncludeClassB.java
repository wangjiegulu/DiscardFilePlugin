package com.wangjie.plg.discardfile.sample.ui.include;

import com.wangjie.plg.discardfile.api.annotation.Discard;

import static com.wangjie.plg.discardfile.sample.constants.Constants.IS_PUBLISH;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/12/17.
 */
public class IncludeClassB {
    @Discard(applyParam = IS_PUBLISH, srcCode = "{return true;}")
    public boolean onIncludeMethodB() {
        System.out.println("onIncludeMethodB...");
        return false;
    }
}
