package com.wangjie.plg.discardfile

import com.android.annotations.NonNull
import com.android.annotations.Nullable
import com.wangjie.plg.discardfile.api.annotation.Discard
import com.wangjie.plg.discardfile.api.constant.DiscardConstant
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

public class DiscardInject {
    public static ClassPool pool = ClassPool.getDefault();

    public static void applyInject(Project project, String dirPath) {
        pool.appendClassPath(dirPath)
        File dir = new File(dirPath)
        if (!dir.isDirectory()) {
            return
        }

        System.setProperty(DiscardConstant.APPLY_PARAM_DEFAULT,
                String.valueOf(dirPath.endsWith("/release"))
        )

        def discardFile = project['discard']
        println "[DiscardFilePlugin] -> Configuration: " + discardFile.includePackagePath + ", " + discardFile.excludePackagePath

        dir.eachFileRecurse { File file ->
            String filePath = file.absolutePath
            if (filePath.endsWith(".class")
                    && !filePath.contains("R\$")
                    && !filePath.contains("R.class")
                    && !filePath.contains("BuildConfig.class")
            ) {
                String classNamePath = filePath.replace(dirPath, "")
                if (null != classNamePath && classNamePath.length() > 0) {
                    if (classNamePath.startsWith("/")) {
                        classNamePath = classNamePath.substring(1)
                    }
//                    println "classNamePath: " + classNamePath
                    if (isValidateFile(discardFile, classNamePath)) {
                        String className = classNamePath.replaceAll("/", ".")
                        if (className.endsWith(".class")) {
                            className = className.substring(0, className.length() - 6)
                        }
                        CtClass ctClass = pool.get(className)
                        if (ctClass.isFrozen()) {
                            ctClass.defrost()
                        }

                        /*
                         * If Add @Discard annotation at Class, discard all declared methods
                         */
                        Discard classDiscard = ctClass.getAnnotation(Discard.class);
                        if (null != classDiscard) {
                            if (!isApplyDiscard(project, classDiscard, "Discard class : " + ctClass.getName())) {
                                println("[DiscardFilePlugin] -> [NOT APPLIED]Discard class : " + className)
                            } else {
                                discardClass(ctClass, classDiscard);
                                ctClass.writeFile(dirPath)
                                println("[DiscardFilePlugin] -> [APPLIED]Discard class : " + className)
                            }
                        } else {
                            /*
                             * Get all of methods that has @Discard annotations
                             */
                            CtMethod[] ctMethods = ctClass.getDeclaredMethods();
                            int ctMethodsLength = ctMethods.length;
                            for (int i = 0; i < ctMethodsLength; i++) {
                                CtMethod ctMethod = ctMethods[i];

                                Discard methodDiscard = ctMethod.getAnnotation(Discard.class)
                                if (null != methodDiscard) {
                                    if (!isApplyDiscard(project, methodDiscard, "Discard method : " + ctMethod.getLongName())) {
                                        println("[DiscardFilePlugin] -> [NOT APPLIED]Discard method : " + ctMethod.getLongName())
                                        continue
                                    }
                                    println("[DiscardFilePlugin] -> [APPLIED]Discard method : " + ctMethod.getLongName())
                                    discardMethod(ctMethod, methodDiscard)
                                    ctClass.writeFile(dirPath)
                                }
                            }
                        }
                        ctClass.detach()

                    }
                }
            }

        }


    }

    private static void discardClass(CtClass ctClass, @NonNull Discard discard) {
        tryMakeClasses(discard)
        CtMethod[] ctMethods = ctClass.getDeclaredMethods();
        int ctMethodsLength = ctMethods.length;
        for (int i = 0; i < ctMethodsLength; i++) {
            CtMethod ctMethod = ctMethods[i];
            discardMethod(ctMethod, ctMethod.getAnnotation(Discard.class))
        }
    }

    private static void discardMethod(CtMethod ctMethod, @Nullable Discard discard) {
        String srcCode = null;
        if (null != discard) {
            // Cancel this discard if it is disabled.
            if (!discard.enable()) {
                return;
            }
            tryMakeClasses(discard)
            srcCode = discard.srcCode()
        }

        if (null == srcCode || srcCode.length() < 2) {
            srcCode = getDefaultSrcCode(ctMethod)
        }

        ctMethod.setBody(srcCode)

    }

    private static boolean isApplyDiscard(Project project, Discard discard, String tag) {
        String applyParam = discard.applyParam()
        String applyParamExpectValue = discard.applyParamValue()
        String applyParamValue = getParameter(project, applyParam);

        if (null == applyParamValue || applyParamExpectValue != applyParamValue) {
            println("[DiscardFilePlugin] -> [NOT APPLIED PARAM]" + tag + ", applyParam: " + applyParam + ", applyParamExpectValue: " + applyParamExpectValue + ", applyParamValue: " + applyParamValue)
            return false;
        }
        return true;
    }

    private static void tryMakeClasses(Discard discard) {
        String[] paramMakeClassNames = discard.makeClassNames();
        if (null != paramMakeClassNames) {
            int paramMakeClassLen = paramMakeClassNames.length;
            for (int j = 0; j < paramMakeClassLen; j++) {
                if (null == pool.find(paramMakeClassNames[j])) {
                    pool.makeClass(paramMakeClassNames[j])
                }
            }
        }
    }

    private static boolean isValidateFile(DiscardFile discardFile, String classNamePath) {
        int excludeLength = discardFile.excludePackagePath.length;
        for (int i = 0; i < excludeLength; i++) {
            if (classNamePath.contains(discardFile.excludePackagePath[i].replaceAll("\\.", "/"))) {
                return false
            }
        }

        int includeLength = discardFile.includePackagePath.length;
        if (0 == includeLength) {
            return true;
        }
        boolean isInclude = false;
        for (int i = 0; i < includeLength; i++) {
            if (classNamePath.contains(discardFile.includePackagePath[i].replaceAll("\\.", "/"))) {
                isInclude = true;
                break
            }
        }
        return isInclude;
    }

    private static String getDefaultSrcCode(CtMethod ctMethod) {
        CtClass returnType = ctMethod.getReturnType()
        if (CtClass.voidType == returnType) {
            return "{}"
        } else if (CtClass.booleanType == returnType) {
            return "{ return false; }"
        } else if (CtClass.byteType == returnType) {
            return "{ return 0; }"
        } else if (CtClass.charType == returnType) {
            return "{ return 0; }"
        } else if (CtClass.doubleType == returnType) {
            return "{ return 0; }"
        } else if (CtClass.floatType == returnType) {
            return "{ return 0F; }"
        } else if (CtClass.intType == returnType) {
            return "{ return 0; }"
        } else if (CtClass.longType == returnType) {
            return "{ return 0L; }"
        } else if (CtClass.shortType == returnType) {
            return "{ return 0; }"
        } else {
            return "{ return null; }"
        }
    }

    private static String getParameter(Project project, String key) {
        // -D
        String value = System.getProperty(key)
        if (null != value && value.length() > 0) {
            return value
        }
        // -P
        if (project.hasProperty(key)) {
            return project.property(key)
        }
        return null
    }

}