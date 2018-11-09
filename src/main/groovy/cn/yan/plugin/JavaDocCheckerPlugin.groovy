/**
 * MIT License
 *
 * Copyright (c) 2018 yanbo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cn.yan.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.AndroidSourceSet
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet

/**
 * 插件入口
 *
 * @author yan
 */
class JavaDocCheckerPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create(CheckerExtension.NAME, CheckerExtension)
        project.tasks.create("javaDocChecker", JavaDocCheckerTask)

        JavaPluginConvention java = null
        BaseExtension android = null
        if (project.plugins.hasPlugin(AppPlugin)) {
            android = project.extensions.getByType(AppExtension)
        } else if(project.plugins.hasPlugin(LibraryPlugin)) {
            android = project.extensions.getByType(LibraryExtension)
        } else if (project.plugins.hasPlugin(JavaPlugin)) {
            java = project.convention.getPlugin(JavaPluginConvention)
        }

        if (java == null && android == null) {
            throw new GradleException("it's a not support plugin type!")
        }

        project.afterEvaluate {
            afterEvaluateInner(project, java, android)
        }
    }

    private void afterEvaluateInner(Project project, JavaPluginConvention java, BaseExtension android) {
        if (java != null) {
            processJava(project, java)
        } else if (android != null) {
            processAndroid(project, android)
        }
    }

    private void processJava(Project project, JavaPluginConvention java) {
        List<String> sources = new ArrayList<>()
        SourceSet mainSourceSet = java.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        mainSourceSet.allJava.srcDirs.each {
            sources.add(it.absolutePath)
        }

        assignedTask(project, sources)
    }

    private void processAndroid(Project project, BaseExtension android) {
        List<String> sources = new ArrayList<>()
        AndroidSourceSet mainSourceSet = android.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        mainSourceSet.java.srcDirs.each {
            sources.add(it.absolutePath)
        }

        assignedTask(project, sources)
    }

    private void assignedTask(Project project, List<String> sources) {
        def checker = project[CheckerExtension.NAME]
        if (checker == null) {
            return
        }

        project.getTasksByName("javaDocChecker", false).each {
            it.configure {
                includePackages = checker.includePackages == null ? [] : checker.includePackages
                excludePackages = checker.excludePackages == null ? [] : checker.excludePackages
                sourcePaths = sources
                outputDir = checker.outputDirectory
            }
        }
    }
}
