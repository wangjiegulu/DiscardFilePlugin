package com.wangjie.plg.discardfile

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project

public class DiscardFileTransform extends Transform {
    Project project;

    DiscardFileTransform(Project project) {
        this.project = project
    }

    // 设置我们自定义的Transform对应的Task名称
    // 类似：TransformClassesWithPreDexForXXX
    @Override
    String getName() {
        return "discardFile"
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    // 这样确保其他类型的文件不会传入
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
//        HashSet<QualifiedContent.ContentType> contentTypes = new HashSet<>();
//        contentTypes.add(TransformManager.CONTENT_CLASS)
//        contentTypes.add(TransformManager.CONTENT_RESOURCES)
//        return contentTypes;
        return TransformManager.CONTENT_CLASS;
    }

    // 指定Transform的作用范围
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        long start = System.currentTimeMillis();

        println("+-----------------------------------------------------------------------------+");
        println("|                      Discard File Transform START                           |");
        println("+-----------------------------------------------------------------------------+");

        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历

        transformInvocation.inputs.each { TransformInput input ->

            // 对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput ->
                // jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）

                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }

                //生成输出路径
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)

                createFile(dest);

                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)

                DiscardInject.pool.appendClassPath(jarInput.file.getAbsolutePath())
            }

            // 对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                // 文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等

//                println(">>>>>>>>>>>>>>>> dir name: " + directoryInput.file)
                // 进行class注入
                DiscardInject.applyInject(project, directoryInput.file.getAbsolutePath())

                // 获取output目录
                def dest = transformInvocation.outputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY)

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }


        }

        println("[DiscardFilePlugin] -> Discard file transform takes: " + (System.currentTimeMillis() - start) + "ms")
        println("+-----------------------------------------------------------------------------+");
        println("|                       Discard File Transform END                            |");
        println("+-----------------------------------------------------------------------------+");
    }


    private static void createFile(File file) {
        if (file.isDirectory()) {
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            if (file.exists()) {
                return;
            }
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.createNewFile();
        }
    }

}