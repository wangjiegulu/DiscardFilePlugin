package com.wangjie.plg.discardfile.sample.dep;

import com.wangjie.plg.discardfile.api.annotation.Discard;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/17/17.
 */
public class DepClassA {
    @Discard
    public void depClassA_1() {
        System.out.println("depClassA_1...");
    }
}
