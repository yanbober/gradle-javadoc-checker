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

import org.gradle.api.DefaultTask
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * javadoc 处理任务
 * @author yan
 */
class JavaDocCheckerTask extends DefaultTask {
    @Input
    List<String> includePackages

    @Input
    List<String> excludePackages

    @Input
    List<String> sourcePaths

    @OutputDirectory
    String outputDir

    @TaskAction
    void checker() {
        if (sourcePaths == null || sourcePaths.size() == 0) {
            throw new GradleScriptException("JavaDocCheckerTask sourcePaths params can't be null or empty!")
        }

        if (outputDir == null || outputDir.length() == 0) {
            throw new GradleScriptException("JavaDocCheckerTask outputDir params can't be null or empty!")
        }

        JavaDocReader.process(sourcePaths, includePackages, excludePackages, outputDir)
    }
}
