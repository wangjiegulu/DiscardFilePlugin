package com.wangjie.plg.discardfile

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
        println "[DiscardFilePlugin] -> discardFile: " + discardFile.includePackagePath + ", " + discardFile.excludePackagePath

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
                        String className = classNamePath.replaceAll("/", ".").replace(".class", "")
//                        println "[DiscardFilePlugin] -> className: " + className

                        CtClass c = pool.get(className)
                        if (c.isFrozen()) {
                            c.defrost()
                        }
                        CtMethod[] ctMethods = c.getMethods();
                        int ctMethodsLength = ctMethods.length;
                        for (int i = 0; i < ctMethodsLength; i++) {
                            CtMethod ctMethod = ctMethods[i];

                            if (ctMethod.hasAnnotation(Discard.class)) {
                                Discard discard = ctMethod.getAnnotation(Discard.class)

                                String applyParam = discard.applyParam()
                                String applyParamValue = System.getProperty(applyParam);
                                println("[DiscardFilePlugin] -> applyParam: " + applyParam + ", applyParamValue: " + applyParamValue)
                                if (null == applyParamValue || !"true".equalsIgnoreCase(applyParamValue)) {
                                    println("[DiscardFilePlugin] -> [NOT APPLY]Discard method : " + className + "::" + ctMethod.getName() + "()")
                                    continue
                                }
                                makeClasses(discard)

                                String srcCode = discard.srcCode();
                                if (null == srcCode || srcCode.length() < 2) {
                                    srcCode = getDefaultSrcCode(ctMethod)
                                }

                                println("[DiscardFilePlugin] -> Discard method : " + className + "::" + ctMethod.getName() + "()")

                                ctMethod.setBody(srcCode)
                                c.writeFile(dirPath)
                                c.detach()
                            }
                        }


                    }
                }
            }

        }


    }

    private static void makeClasses(Discard discard) {
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

}