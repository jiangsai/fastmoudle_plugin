package com.fastmoudle.module

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import com.fastmoudle.utils.Logger


class FastModuleTransform extends Transform{

    Project project

    FastModuleTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return Constant.FAST_MODULE
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        System.out.println("transform开始")
        Collection<TransformInput> transformInputs = transformInvocation.getInputs()
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider()
        def moduleProxyClassList = []
        transformInputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                System.out.println("gbgb====="+directoryInput+"====")
                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        //形如 X$$****$$Proxy.class 的类，是我们要找的目标class
                        System.out.println("jyt====="+file+"====")
                        if (ScanUtil.isTargetProxyClass(file)) {
                            Logger.logd("scan dir jar : " + file.getAbsolutePath())
                            moduleProxyClassList.add(file.name)
                        }
                    }
                }

                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            //System.out.println("transform ===> sum="+moduleProxyClassList.size())
            System.out.println("jyt=====jarInputs开始====")
            input.jarInputs.each { JarInput jarInput ->
//                println "\njarInput = ${jarInput}"

                def jarName = jarInput.name
                System.out.println("jyt=====jarInputs"+jarName+"====")
                def md5 = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
                    //处理jar包里的代码
                    File src = jarInput.file
                    if (ScanUtil.shouldProcessPreDexJar(src.absolutePath)) {
                        List<String> list = ScanUtil.scanJar(src, dest)
                        if (list != null) {
                            moduleProxyClassList.addAll(list)
                        }
                    }
                }
                FileUtils.copyFile(jarInput.file, dest)
            }
        }


        if (null != ScanUtil.FILE_CONTAINS_INIT_CLASS) {
            new FastModuleInjector(moduleProxyClassList).execute()
        }

    }
}