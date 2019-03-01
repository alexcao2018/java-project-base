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

import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.System.out;

/*

            <plugin>
                <groupId>com.project.base</groupId>
                <artifactId>project-base-plugin</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <goals>
                            <goal>apollo-config</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

 */


@Mojo(name = "apollo-config", requiresDependencyResolution = ResolutionScope.COMPILE)
public class ApolloMojo extends AbstractMojo {

    @Parameter(property = "outputDirectory", defaultValue = "${project.build.outputDirectory}", readonly = true, required = true)
    private File outputDirectory;

    @Parameter(property = "buildName", defaultValue = "${project.build.finalName}", readonly = true, required = true)
    private String buildName;

    @Parameter(property = "project", defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "sourceDirectory", defaultValue = "${project.build.sourceDirectory}", readonly = true, required = true)
    private String sourceDirectory;

    @Parameter(property = "sDirectory", defaultValue = "${project.build.directory}", readonly = true, required = true)
    private String sDirectory;

    private Map<String, String> profileMap = new HashMap<>();
    private String applicationYmlName = "application.yml";
    private String applicationPropertyName = "application-apollo.properties";


    public ApolloMojo() {
        profileMap.put("dev", "/resources/deploy/dev/");
        profileMap.put("k8s", "/resources/deploy/k8s/");
        profileMap.put("pro", "/resources/deploy/pro/");
    }

    public void execute() throws MojoExecutionException {

        /* 生成对应的properties 文件
        ----------------------------------------------------------------
         */
        for (String profile : profileMap.keySet()) {
            String profileDirectorySuffix = profileMap.get(profile).replace("/", File.separator);
            String profileDirectory = sourceDirectory.substring(0, sourceDirectory.lastIndexOf(File.separator)) + profileDirectorySuffix;
            String applicationYmlPath = profileDirectory + applicationYmlName;
            String applicationPropertyPath = profileDirectory + applicationPropertyName;
            Yaml yaml = new Yaml();
            InputStream in = null;
            try {
                in = Files.newInputStream(Paths.get(applicationYmlPath));
                TreeMap<String, Map<String, Object>> config = yaml.loadAs(in, TreeMap.class);
                Path file = Paths.get(applicationPropertyPath);
                String propertyContent = toProperties(config);
                byte[] bytes = propertyContent.getBytes(Charset.forName("UTF-8"));
                Files.write(file, bytes);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /* 将target 目录中的application.yml 文件替换为 apollo 配置
        ----------------------------------------------------------------
         */
        InputStream resourceAsStream = ApolloMojo.class.getClassLoader().getResourceAsStream("application.yml");
        try {
            String apolloYaml = IOUtils.toString(resourceAsStream, "UTF-8");
            apolloYaml = apolloYaml.replace("${id}",project.getParent().getArtifact().getArtifactId());
            Path file = Paths.get(outputDirectory.getAbsolutePath() + File.separator + applicationYmlName);
            byte[] bytes = apolloYaml.getBytes(Charset.forName("UTF-8"));
            Files.write(file, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static String toProperties(TreeMap<String, Map<String, Object>> config) {
        StringBuilder sb = new StringBuilder();
        for (String key : config.keySet()) {
            sb.append(toString(key, config.get(key)));
        }
        return sb.toString();
    }

    private static String toString(String key, Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (String mapKey : map.keySet()) {
            if (map.get(mapKey) instanceof Map) {
                sb.append(toString(String.format("%s.%s", key, mapKey), (Map<String, Object>) map.get(mapKey)));
            } else {
                sb.append(String.format("%s.%s=%s%n", key, mapKey, map.get(mapKey).toString()));
            }
        }
        return sb.toString();
    }

}

