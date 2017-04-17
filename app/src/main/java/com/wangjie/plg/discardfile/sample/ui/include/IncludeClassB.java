package com.wangjie.plg.discardfile.sample.ui.include;

import com.wangjie.plg.discardfile.api.annotation.Discard;
import com.wangjie.plg.discardfile.sample.constants.ApplyConstants;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/12/17.
 */
public class IncludeClassB {
    @Discard(apply = ApplyConstants.Publish._TRUE, srcCode = "{return true;}")
    public boolean onIncludeMethodB() {
        System.out.println("onIncludeMethodB...");
        return false;
    }
}
