package com.wangjie.plg.discardfile.sample.exclude;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/12/17.
 */
public class ExcludeClassB {
    public boolean onExcludeMethodB(){
        System.out.println("onExcludeMethodB...");
        return false;
    }
}
