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
    public ClassPool pool/* = ClassPool.getDefault()*/;

    DiscardInject(ClassPool pool) {
        this.pool = pool
    }

    public void applyInject(Project project, DiscardFileExtension discardFileExtension, String dirPath) {
        pool.appendClassPath(dirPath)
        File dir = new File(dirPath)
        if (!dir.isDirectory()) {
            return
        }

        println "[DiscardFilePlugin] inject dirPath: " + dirPath
        System.setProperty(DiscardConstant.APPLY_PARAM_DEFAULT,
                String.valueOf(
                        dirPath.endsWith("/release")
                                ||
                                dirPath.contains("/release/")
                )
        )

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
                    if (isValidateFile(discardFileExtension, classNamePath)) {
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
//                                ctClass.writeFile(dirPath)
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
//                                    ctClass.writeFile(dirPath)
                                }
                            }


                        }
                        ctClass.writeFile(dirPath)
                        ctClass.detach()

                    }
                }
            }

        }


    }

    private void discardClass(CtClass ctClass, @NonNull Discard discard) {
        tryMakeClasses(discard)

        // discard methods
        CtMethod[] ctMethods = ctClass.getDeclaredMethods();
        int ctMethodsLength = ctMethods.length;
        for (int i = 0; i < ctMethodsLength; i++) {
            CtMethod ctMethod = ctMethods[i];
            discardMethod(ctMethod, ctMethod.getAnnotation(Discard.class))
        }
    }


    private void discardMethod(CtMethod ctMethod, @Nullable Discard discard) {
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
            srcCode = "{ return " + getDefaultTypeValue(ctMethod.getReturnType()) + "; }"
        }
        ctMethod.setBody(srcCode)
    }

    private boolean isApplyDiscard(Project project, Discard discard, String tag) {
        String applyParam = discard.applyParam()
        String applyParamExpectValue = discard.applyParamValue()
        String applyParamValue = getParameter(project, applyParam);

        if (null == applyParamValue || applyParamExpectValue != applyParamValue) {
            println("[DiscardFilePlugin] -> [NOT APPLIED PARAM]" + tag + ", applyParam: " + applyParam + ", applyParamExpectValue: " + applyParamExpectValue + ", applyParamValue: " + applyParamValue)
            return false;
        }
        return true;
    }

    private void tryMakeClasses(Discard discard) {
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

    private boolean isValidateFile(DiscardFileExtension discardFile, String classNamePath) {
        // 优先检查exclude，如果包含在exclude中，则不通过
        if (null != discardFile.excludePackagePath && discardFile.excludePackagePath.length > 0) {
            int excludeLength = discardFile.excludePackagePath.length;
            for (int i = 0; i < excludeLength; i++) {
                if (classNamePath.contains(discardFile.excludePackagePath[i].replaceAll("\\.", "/"))) {
                    return false
                }
            }
        }

        // 如果没有设置include，则默认包含所有包
        if (null == discardFile.includePackagePath || discardFile.includePackagePath.length <= 0) {
            return true;
        }

        // 如果明确了include，则检查是否在include中
        boolean isInclude = false;
        int includeLength = discardFile.includePackagePath.length;
        for (int i = 0; i < includeLength; i++) {
            if (classNamePath.contains(discardFile.includePackagePath[i].replaceAll("\\.", "/"))) {
                isInclude = true;
                break
            }
        }
        return isInclude;
    }

    private String getDefaultTypeValue(CtClass type) {
        if (CtClass.booleanType == type) {
            return "false"
        } else if (CtClass.byteType == type) {
            return "0"
        } else if (CtClass.charType == type) {
            return "0"
        } else if (CtClass.doubleType == type) {
            return "0"
        } else if (CtClass.floatType == type) {
            return "0F"
        } else if (CtClass.intType == type) {
            return "0"
        } else if (CtClass.longType == type) {
            return "0L"
        } else if (CtClass.shortType == type) {
            return "0"
        } else {
            return "null"
        }
    }

    private String getParameter(Project project, String key) {
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

    public void clear() {
        pool = null
    }


}