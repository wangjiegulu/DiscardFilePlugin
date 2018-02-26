# DiscardFilePlugin

An android gradle plugin for discard classes or methods in compile time.

> [中文版本](README-zh.md)

## 1.1 Scenes can be used

In the actual product, we always add some debug tools to our app in `debug` model, such as `DebugPanelActivity`(Debug panel tools page, provide some features like "switch server"). And we need to discard and clear related classes and methods in production environment mode, or modify `boolean isProductionEnvironment()` method to let it always return `ture`. **In order to avoid exposing debugging codes in production app via decompile and other trick.**

## 1.2 `@Discard` annotation

### 1.2.1 Target

- `ElementType.METHOD`: Discard codes in method. Codes of the method will be clean in compile time.

- `ElementType.TYPE`: Discard codes in class. In fact, it will be discarded all methods in the class.

### 1.2.2 Parameters

#### 1.2.2.1 `apply`

`apply` parameters specification：`key==exceptValue`

That means to discard will take effect only when `key==exceptValue`. And then it will discard classes or methods in compile time. So it can configuration different in any classes or methods:

```java
@Discard(apply = "test1==true")
public void testMethod_1() {
    System.out.println("testMethod_1...");
}

@Discard(apply = "test2==true")
public void testMethod_2() {
    System.out.println("testMethod_2...");
}
```

When you build with `gradle assembleDebug -Ptest1=true -Ptest2=false` command, `testMethod_1()` method will be discarded, but `testMethod_2()` not. After building succeed:

```java
@Discard(apply = "test1==true")
public void testMethod_1() {
}

@Discard(apply = "test2==true")
public void testMethod_2() {
    System.out.println("testMethod_2...");
}
```

#### 1.2.2.2 `srcCode`

It can be used to replace method body, if it not set, default as follows:

- Return type is `void`: Method body will be `{}` after discard.
- Return type is a primitive type: Method body will be returning default value, for example `{ return 0; }`.
- Return type is object: Method body will be `{ return null; }` after discard.

You can also fill in the specific method body code as follows:

```java
@Discard(srcCode = "{super.onCreate($1); System.out.println(\"this: \" + $0);}")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameEt = (EditText) findViewById(R.id.activity_main_username_et);
        passwordEt = (EditText) findViewById(R.id.activity_main_password_et);
        setTestAccount();
    }
```

Decompile the code that after discard as follows:

```java
@Discard(
        srcCode = "{super.onCreate($1); System.out.println(\"this: \" + $0);}"
    )
    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        System.out.println("this: " + this);
    }
```

`$0` represents current object `this`, parameters of the method are `$1, $2, $3...`, [Detailed documentation reference here](http://jboss-javassist.github.io/javassist/tutorial/tutorial2.html#alter)

#### 1.2.2.3 `makeClassNames`

Special class names here, it will make classes that not in the class path when discard. **Not generally used, can be omitted.**

#### 1.2.2.4 `enable`

It represents that if discard for this class or method is enabled state, default is `true`, A typical scene is, add a `@Discard` in class to discard all methods in this class, but need a method does not. You can then use `@Discard (enable = false)` to exclude the method from the `discard` scope.

## 1.3 How to use

### Gradle([Check newest version](http://search.maven.org/#search%7Cga%7C1%7Cdiscardfile)):

`build.gradle` in Project:

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.2.2'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        classpath 'com.github.wangjiegulu:discardfile:x.x.x'
    }
}
```

`build.gradle` in `app` or `library`:

```groovy
apply plugin: 'com.github.wangjiegulu.plg.discardfile'

dependencies {
	compile 'com.github.wangjiegulu:discardfile-api:x.x.x'
}
```

### 1.3.1. `build.gradle`

```groovy
// use plugin
apply plugin: 'com.github.wangjiegulu.plg.discardfile'

// Configure the class packages that need to be modified
discard {
    includePackagePath 'com.wangjie.plg.discardfile.sample.ui', 'com.wangjie.plg.discardfile.sample.include'
    excludePackagePath 'com.wangjie.plg.discardfile.sample.exclude'/*, 'com.wangjie.plg.discardfile.sample.ui.MainActivity'*/
}
```

### 1.3.2. Use `@Discard` annotation

Create custom 'apply' configuration(`publish` and` disable` two 'apply' configuration):

```java
public class ApplyConstants {
    public static class Publish {
        private static final String PUBLISH = "publish";
        public static final String _TRUE = PUBLISH + "==true";
    }
    public static class DISABLE {
        private static final String DISABLE = "disable";
        public static final String _TRUE = DISABLE + "==true";
    }
}
```

Add the `@Discard` annotation to the class that needs to be discarded.` `Apply = ApplyConstants.Publish._TRUE` means that Discard is just executed if` publish = true`.

```java
@Discard(apply = ApplyConstants.Publish._TRUE)
public class IncludeClassC {

    /**
     * This method will be discarded because of IncludeClass added a `@Discard` annotation
     */
    public void onIncludeMethodC() {
        System.out.println("onIncludeMethodC...");
    }

    /**
     * Replace method body as: {System.out.println("onIncludeMethodC_2... injected!");}
     */
    @Discard(apply = ApplyConstants.Publish._TRUE, srcCode = "{System.out.println(\"onIncludeMethodC_2... injected!\");}")
    public void onIncludeMethodC_2() {
        System.out.println("onIncludeMethodC_2...");
    }

    /**
     * Replace method implements and always return true
     */
    @Discard(apply = ApplyConstants.Publish._TRUE, srcCode = "{return true;}")
    public boolean onIncludeMethodC_3() {
        System.out.println("onIncludeMethodC_3...");
        return false;
    }

    /**
     * This method will be discarded because of IncludeClass added a `@Discard` annotation
     */
    public int onIncludeMethodC_4() {
        System.out.println("onIncludeMethodC_4...");
        return 100;
    }

    /**
     * This method is not discarded due to the use of the `@Discard` annotation to explicitly disable discard
     */
    @Discard(apply = ApplyConstants.Publish._TRUE, enable = false)
    public String onIncludeMethodC_5() {
        System.out.println("onIncludeMethodC_5...");
        return "hello world";
    }

    /**
     * Replace method implements and always return "hello world
     */
    @Discard(apply = ApplyConstants.Publish._TRUE, srcCode = "{return \"hello world injected!\";}")
    public String onIncludeMethodC_6() {
        System.out.println("onIncludeMethodC_6...");
        return "hello world";
    }
}
```

### 1.3.3. build

Build use following command:

```
gradle clean assembleFullDebug -Ppublish=true -Pdisable=true
```

When build is completed, the `class` file for that class will be automatically transformed as follows:

```
build/intermediates/transforms/discardFile/.../IncludeClassC.class
```

```java
@Discard(
    apply = "publish==true"
)
public class IncludeClassC {
    public IncludeClassC() {
    }

    public void onIncludeMethodC() {
        Object var10000 = null;
    }

    @Discard(
        apply = "publish==true",
        srcCode = "{System.out.println(\"onIncludeMethodC_2... injected!\");}"
    )
    public void onIncludeMethodC_2() {
        System.out.println("onIncludeMethodC_2... injected!");
    }

    @Discard(
        apply = "publish==true",
        srcCode = "{return true;}"
    )
    public boolean onIncludeMethodC_3() {
        return true;
    }

    public int onIncludeMethodC_4() {
        return 0;
    }

    @Discard(
        apply = "publish==true",
        enable = false
    )
    public String onIncludeMethodC_5() {
        System.out.println("onIncludeMethodC_5...");
        return "hello world";
    }

    @Discard(
        apply = "publish==true",
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




