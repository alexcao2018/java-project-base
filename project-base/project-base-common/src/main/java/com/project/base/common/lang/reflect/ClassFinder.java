package com.project.base.common.lang.reflect;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghc on 2017-03-30.
 */
public class ClassFinder {

    private static final char PKG_SEPARATOR = '.';

    private static final char DIR_SEPARATOR = '/';

    private static final String CLASS_FILE_SUFFIX = ".class";

    private static final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";

    public static List<Class<?>> find(String scannedPackage) {
        String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
        }
        File scannedDir = new File(scannedUrl.getFile());
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File file : scannedDir.listFiles()) {
            classes.addAll(find(file, scannedPackage,null));
        }
        return classes;
    }

    public static List<Class<?>> find(String directory, String scannedPackage, ClassLoader classLoader) {
        File scannedDir = new File(directory);
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (scannedDir.listFiles() == null)
            return classes;
        for (File file : scannedDir.listFiles()) {
            classes.addAll(find(file, scannedPackage, classLoader));
        }
        return classes;
    }

    public static List<Class<?>> find(String directory, String scannedPackage) {
        return find(directory, scannedPackage, null);
    }

    private static List<Class<?>> find(File file, String scannedPackage, ClassLoader classLoader) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String resource = StringUtils.EMPTY;

        if (StringUtils.isNotEmpty(scannedPackage)) {
            resource = scannedPackage + PKG_SEPARATOR + file.getName();
        } else {
            resource = file.getName();
        }
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource, classLoader));
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX) && !file.getName().contains("$")) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                System.out.println(className);
                if (classLoader == null)
                    classes.add(Class.forName(className));
                else
                    classes.add(classLoader.loadClass(className));
            } catch (ClassNotFoundException ignore) {
                System.out.println(ignore);
            }
        }
        return classes;
    }


}
