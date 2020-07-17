package com.project.base.common.lang;

import com.project.base.common.security.HashTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemTool {

    private static final Logger logger = LoggerFactory.getLogger(SystemTool.class);

    /**
     * 当前系统的回车换行符
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private SystemTool() {
    }

    /**
     * 获取当前操作系统名称.
     *
     * @return 操作系统名称 例如:windows xp,linux 等.
     */
    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * 获取MAC地址
     *
     * @return 所有网卡的MAC地址列表
     */
    public static List<String> getMacAddress() {
        List<String> macs = new ArrayList<>();
        // The Runtime.exec() method returns an instance of a subclass of Process
        Process myProc;

        // surround with a try catch block
        try {

            String currentLine = "";

            // the operating systems name as referenced by the System
            String osName = getOSName();

            // a regular expression used to match the area of text we want
            String macRegExp = "";

            if (osName.startsWith("windows")) { // Windows operating system will run this

                // the regular expression we will be matching for the Mac address on Windows
                macRegExp = "[\\da-zA-Z]{1,2}\\-[\\da-zA-Z]{1,2}\\-[\\da-zA-Z]" +
                        "{1,2}\\-[\\da-zA-Z]{1,2}\\-[\\da-zA-Z]{1,2}\\-[\\da-zA-Z]{1,2}";
                myProc = Runtime.getRuntime().exec("ipconfig /all");
            } else if (osName.startsWith("linux")) { // Linux operating system runs this

                // the regular expression we will be matching for the Mac address on Linux
                macRegExp = "[\\da-zA-Z]{1,2}\\:[\\da-zA-Z]{1,2}\\:[\\da-zA-Z]" +
                        "{1,2}\\:[\\da-zA-Z]{1,2}\\:[\\da-zA-Z]{1,2}\\:[\\da-zA-Z]{1,2}";
                myProc = Runtime.getRuntime().exec("/sbin/ifconfig -a");
            } else {
                throw new UnsupportedOperationException("不支持的操作系统");
            }

            // we'll wrap a buffer around the InputStream we get from the "myProc" Process
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(myProc.getInputStream()));
            // compile the macRegExp string into a Pattern
            Pattern macPattern = Pattern.compile(".*(" + macRegExp + ").*");

            // a Matcher object for matching the regular expression to the string
            Matcher macMtch = null;

            while ((currentLine = in.readLine()) != null) {

                // walk through each line and try to match the pattern
                macMtch = macPattern.matcher(currentLine);

                if (macMtch.matches()) {

                    // it matched so we split the line
                    String[] splitLine = currentLine.split(macRegExp);

                    for (int a = 0; a < splitLine.length; a++) {
                        // REPLACE ALL PORTIONS of the currentLine
                        // that do not match the expression
                        // with an empty string
                        currentLine = currentLine.replaceAll(splitLine[a].replaceAll("\\(", "\\\\(").replaceAll("\\)", "\\\\)"), "");
                    }

                    // mac address(es) returned in the StringBuffer
                    macs.add(currentLine);

                    // reset the matcher just in case we have more than one mac address
                    macMtch.reset();
                }
            }

            myProc.destroy();
        } catch (IOException e) {
            logger.error("读取mac异常", e);
        }
        return macs;
    }

    // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
    // 封装org.apache.commons.lang3.SystemUtils
    // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

    /**
     * <p>
     * 获取java home目录, 并以{@code File}返回
     * </p>
     *
     * @return 目录
     * @throws SecurityException 如果安全管理器存在并且它的 {@code checkPropertyAccess} 方法不允许访问特别的系统属性
     * @see System#getProperty(String)
     * @since 1.0.0
     */
    public static File getJavaHome() {
        return org.apache.commons.lang3.SystemUtils.getJavaHome();
    }

    /**
     * <p>
     * 获取IO临时目录, 并以{@code File}返回
     * </p>
     *
     * @return 目录
     * @throws SecurityException 如果安全管理器存在并且它的 {@code checkPropertyAccess} 方法不允许访问特别的系统属性
     * @see System#getProperty(String)
     * @since 1.0.0
     */
    public static File getJavaIoTmpDir() {
        return org.apache.commons.lang3.SystemUtils.getJavaIoTmpDir();
    }

    // -----------------------------------------------------------------------

    /**
     * <p>
     * 获取用户目录, 并以{@code File}返回
     * </p>
     *
     * @return 目录
     * @throws SecurityException 如果安全管理器存在并且它的 {@code checkPropertyAccess} 方法不允许访问特别的系统属性
     * @see System#getProperty(String)
     * @since 1.0.0
     */
    public static File getUserDir() {
        return org.apache.commons.lang3.SystemUtils.getUserDir();
    }

    /**
     * <p>
     * 获取用户home目录, 并以{@code File}返回
     * </p>
     *
     * @return 目录
     * @throws SecurityException 如果安全管理器存在并且它的 {@code checkPropertyAccess} 方法不允许访问特别的系统属性
     * @see System#getProperty(String)
     * @since 1.0.0
     */
    public static File getUserHome() {
        return org.apache.commons.lang3.SystemUtils.getUserHome();
    }


    /**
     * 是否调试模式
     *
     * @return true:调试模式，反之为false
     */
    private final static Pattern debugPattern = Pattern.compile("-Xdebug|jdwp");

    public static boolean isDebug() {
        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (debugPattern.matcher(arg).find()) {
                return true;
            }
        }
        return false;
    }
}