package com.wangjie.plg.discardfile.sample.exclude;

import com.wangjie.plg.discardfile.api.annotation.Discard;
import com.wangjie.plg.discardfile.sample.constants.ApplyConstants;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/13/17.
 */
@Discard(apply = ApplyConstants.Publish._TRUE)
public class ExcludeClassC {
    public void onExcludeMethodC_1() {
        System.out.println("onExcludeMethodC_1...");
    }


}
