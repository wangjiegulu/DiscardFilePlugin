package com.wangjie.plg.discardfile

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class DiscardFilePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.out.println("========================");
        System.out.println("Discard file plugin!");
        System.out.println("========================");

        project.extensions.create("discard", DiscardFile);

        project.extensions.getByType(AppExtension).registerTransform(new DiscardFileTransform(project))

//        project.task('readExtension').doLast {
//            def discardFile = project['wj'];
//            println discardFile.disFile + ", " + discardFile.isClass
//
////            project.extensions.getByType(AppExtension).registerTransform(new DiscardFileTransform(project))
//        }


    }
}