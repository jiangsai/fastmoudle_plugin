package com.fastmoudle.base

import org.gradle.api.Project

class Env {

    private static Project rootProject

    private static boolean mDebug

    private static Property rootProperties

    static Project getRootProject() {
        return rootProject
    }

    static void rootProject(Project project) {
        rootProject = project.rootProject
    }

    static boolean getDebug() {
        return mDebug
    }

    static void debug(boolean debug) {
        mDebug = debug
    }

    static Property getProperties() {
        return rootProperties
    }

    static void properties(Property property) {
        rootProperties = property
    }
}
