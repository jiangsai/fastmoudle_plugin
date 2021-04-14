package com.fastmoudle.utils;


import com.fastmoudle.base.Env;

public class Logger {

    public static void log() {
        log("");
    }

    public static void log(Object log) {
        log(String.valueOf(log));
    }

    public static void logd() {
        logd("");
    }

    public static void logd(Object log) {
        if (Env.getDebug()) {
            log(log);
        }
    }

    private static void log(String log) {

        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        String className = null;
        String method = null;
        String name;
        for (StackTraceElement element : traces) {
            name = element.getClassName();
            if (name.contains("dalvik") || name.contains("java_lang")
                    || name.contains("java.lang") || name.contains("org.codehaus.groovy.runtime")
                    || name.contains("sun.reflect") || name.contains("groovy.")) {
                continue;
            }

            if (!name.contains(Logger.class.getName())) {
                className = element.getClassName();
                className = className.substring(className.lastIndexOf(".") + 1);
                method = element.getMethodName();
                break;
            }

        }
        System.out.println(" >>>>>>>>>>>>>> " + className + "." + method + "() " + log);
    }

}
