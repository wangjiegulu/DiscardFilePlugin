package com.wangjie.plg.discardfile

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

public class DiscardInject {
    private static ClassPool pool = ClassPool.getDefault();

    public static void applyInject(Project project, String dirPath) {
        pool.appendClassPath(dirPath)
        File dir = new File(dirPath)
        if (!dir.isDirectory()) {
            return
        }
        def discardFile = project['discard']
        println ">>>>>>>>>>>>" + discardFile.includePackagePath + ", " + discardFile.excludePackagePath

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
                    println "classNamePath: " + classNamePath
                    if (isValidateFile(discardFile, classNamePath)) {
                        String className = classNamePath.replaceAll("/", ".").replace(".class", "")
                        println "[validateFile]className: " + className

                        CtClass c = pool.get(className)
                        if(c.isFrozen()){
                            c.defrost()
                        }
                        CtMethod[] ctMethods = c.getMethods();
                        int ctMethodsLength = ctMethods.length;
//                        pool.makeClass("android.os.Bundle")
                        for(int i = 0; i < ctMethodsLength; i++){
//                            ctMethods[i].hasAnnotation()
                            if(ctMethods[i].returnType == CtClass.voidType){
                                ctMethods[i].setBody("{}")
                            }else if(ctMethods[i].returnType == CtClass.booleanType){
                                ctMethods[i].setBody("{return true;}")
                            }



                        }

                        c.writeFile(dirPath)
                        c.detach()
                    }
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


}