package com.project.base.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.project.base.common.net.NetworkTool;
import com.project.base.common.lang.reflect.ClassFinder;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.List;

/*

            <plugin>
                <groupId>com.project.base</groupId>
                <artifactId>project-base-plugin-dubbo</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <goals>
                            <goal>dubbo-resolve</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <dubboPort>此处填写端口号！！</dubboPort>
                </configuration>
            </plugin>

 */


@Mojo(name = "dubbo-resolve", requiresDependencyResolution = ResolutionScope.COMPILE)
public class DubboMojo extends AbstractMojo {

    @Parameter(property = "outputDirectory", defaultValue = "${project.build.outputDirectory}", readonly = true, required = true)
    private File outputDirectory;

    @Parameter(property = "buildName", defaultValue = "${project.build.finalName}", readonly = true, required = true)
    private String buildName;

    @Parameter(property = "project", defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter
    private String dubboPort;

    @Parameter
    private String dubboHost;

    @Parameter
    private String location;


    public void execute() throws MojoExecutionException {

        try {
            List<String> compileClasspathElements = project.getCompileClasspathElements();
            URL[] runtimeUrls = new URL[compileClasspathElements.size()];
            for (int i = 0; i < compileClasspathElements.size(); i++) {
                String element = compileClasspathElements.get(i);
                runtimeUrls[i] = new File(element).toURI().toURL();
            }

            URLClassLoader urlClassLoader = new URLClassLoader(runtimeUrls, Thread.currentThread().getContextClassLoader());
            List<Class<?>> clazzCollection = ClassFinder.find(outputDirectory.getAbsolutePath(), StringUtils.EMPTY, urlClassLoader);

            String userHome = System.getProperty("user.home");
            PrintWriter writer = null;
            try {
                String hostAddress = dubboHost;
                if (StringUtils.isBlank(hostAddress))
                    hostAddress = NetworkTool.getLocalAddress().getHostAddress();

                writer = new PrintWriter(userHome + "/dubbo-resolve.properties", "UTF-8");
                for (Class<?> clazz : clazzCollection) {
                    Class<?> clazzInterface = clazz.getInterfaces()[0];
                    writer.write(MessageFormat.format("{0}=dubbo://{1}:{2}?default.timeout=1200000", clazzInterface.getName(), hostAddress, dubboPort));
                    writer.write(System.getProperty("line.separator"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
