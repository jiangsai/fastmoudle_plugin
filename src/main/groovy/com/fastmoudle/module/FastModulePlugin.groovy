package com.fastmoudle.module
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


import com.fastmoudle.utils.Logger

class FastModulePlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        Logger.log()
        System.out.println("FastModulePlugin开始")
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new FastModuleTransform(project))

    }
}