package com.wangjie.plg.discardfile.sample.ui.include;

import com.wangjie.plg.discardfile.api.annotation.Discard;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/12/17.
 */
public class IncludeClassB {
    @Discard(srcCode = "{return true;}")
    public boolean onIncludeMethodB(){
        System.out.println("onIncludeMethodB...");
        return false;
    }
}
