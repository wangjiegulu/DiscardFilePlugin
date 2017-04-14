package com.wangjie.plg.discardfile

import com.android.build.gradle.AppExtension
//import com.android.build.gradle.AppPlugin
//import com.android.build.gradle.LibraryPlugin
import com.wangjie.plg.discardfile.api.constant.DiscardConstant
import javassist.ClassPool
import org.gradle.api.Plugin
import org.gradle.api.Project

public class DiscardFilePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
//        System.out.println("========================");
//        System.out.println("Discard file plugin!");
//        System.out.println("========================");

//        def hasApp = project.plugins.withType(AppPlugin)
//        def hasLib = project.plugins.withType(LibraryPlugin)
//        if (!hasApp && !hasLib) {
//            throw new IllegalStateException("'android' or 'android-library' plugin required.")
//        }

//        final def variants
//        if (hasApp) {
//            variants = project.android.applicationVariants
//        } else {
//            variants = project.android.libraryVariants
//        }

        ClassPool classPool = new ClassPool(null);
        classPool.appendSystemPath();

        project.extensions.create(DiscardConstant.EXTENSION_NAME, DiscardFileExtension);
        DiscardFileTransform discardFileTransform = new DiscardFileTransform(project, classPool);
        project.extensions.getByType(AppExtension).registerTransform(discardFileTransform)


        project.afterEvaluate {

            def bootClasspath = project.android.bootClasspath.join(File.pathSeparator)
            println("[DiscardFilePlugin] -> bootClasspath: " + bootClasspath + ", this: " + this)
            classPool.appendClassPath(bootClasspath)
//            variants.all { variant ->
                /*
                 * variant.name: panelRelease
                 * variant.baseName: panel-release
                 * variant.dirName: panel/release
                 */

//            }
        }

    }
}