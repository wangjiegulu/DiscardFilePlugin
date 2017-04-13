# DiscardFilePlugin

An android gradle plugin for discard class or method in compile time.

用于在编译构建时期忽略清空类和方法的一个Android Gradle插件。

## 使用场景

在`debug`模式下加入`DebugPanelActivity`，调试面板工具页面，比如“切换服务器”等操作。我们需要在正式上线的release版本中清空相关类和方法，或者修改`boolean isProductionEnvironment()`方法，让它永远返回`true`.

## 使用方式

> 源码依赖，目前不支持远程依赖，稍后更新远程依赖的支持

### 1. 修改`build.gradle`

```groovy
// 使用插件
apply plugin: 'com.wangjie.plg.discardfile'

// 配置需要修改的类所属在那些包下
discard {
    includePackagePath 'com.wangjie.plg.discardfile.sample.ui', 'com.wangjie.plg.discardfile.sample.include'
    excludePackagePath 'com.wangjie.plg.discardfile.sample.exclude'/*, 'com.wangjie.plg.discardfile.sample.ui.MainActivity'*/
}
```

### 2. 设置`@Discard`注解

在需要清空的类上添加`@Discard`注解，`applyParamValue = "true"`表示只有在`Release`版本下，才会执行Discard。

```java
@Discard(applyParamValue = "false")
public class IncludeClassC {
    /**
     * 因为IncludeClassC类增加了`@Discard`注解，所以该方法也会被discard。
     */
    public void onIncludeMethodC() {
        System.out.println("onIncludeMethodC...");
    }

    /**
     * 替换该方法的实现为：{System.out.println("onIncludeMethodC_2... injected!");}
     */
    @Discard(srcCode = "{System.out.println(\"onIncludeMethodC_2... injected!\");}")
    public void onIncludeMethodC_2() {
        System.out.println("onIncludeMethodC_2...");
    }

    /**
     * 替换该方法永远返回true
     */
    @Discard(srcCode = "{return true;}")
    public boolean onIncludeMethodC_3() {
        System.out.println("onIncludeMethodC_3...");
        return false;
    }

    /**
     * 因为IncludeClassC类增加了`@Discard`注解，所以该方法也会被discard。
     */
    public int onIncludeMethodC_4() {
        System.out.println("onIncludeMethodC_4...");
        return 100;
    }

    /**
     * 由于使用了`@Discard`注解进行显式地声明禁用了本地的discard，所以该方法不会被discard
     */
    @Discard(enable = false)
    public String onIncludeMethodC_5() {
        System.out.println("onIncludeMethodC_5...");
        return "hello world";
    }

    /**
     * 替换该方法永远返回"hello world"字符串
     */
    @Discard(srcCode = "{return \"hello world injected!\";}")
    public String onIncludeMethodC_6() {
        System.out.println("onIncludeMethodC_6...");
        return "hello world";
    }
}
```

### 3. build运行

**在`Release`环境下**编译完成之后，该类的`class`文件将会根据配置的`@Discard`注解被自动修改成如下：

```
build/intermediates/classes/../release/.../IncludeClassC.class
```

```java
@Discard(
    applyParamValue = "false"
)
public class IncludeClassC {
    public IncludeClassC() {
    }

    public void onIncludeMethodC() {
    }

    @Discard(
        srcCode = "{System.out.println(\"onIncludeMethodC_2... injected!\");}"
    )
    public void onIncludeMethodC_2() {
        System.out.println("onIncludeMethodC_2... injected!");
    }

    @Discard(
        srcCode = "{return true;}"
    )
    public boolean onIncludeMethodC_3() {
        return true;
    }

    public int onIncludeMethodC_4() {
        return 0;
    }

    @Discard(
        enable = false
    )
    public String onIncludeMethodC_5() {
        System.out.println("onIncludeMethodC_5...");
        return "hello world";
    }

    @Discard(
        srcCode = "{return \"hello world injected!\";}"
    )
    public String onIncludeMethodC_6() {
        return "hello world injected!";
    }
}
```

License
=======


```
Copyright 2017 Wang Jie

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing blacklist and
limitations under the License.
```








