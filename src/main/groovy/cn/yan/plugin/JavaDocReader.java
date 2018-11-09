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

package cn.yan.plugin;

import com.sun.javadoc.*;
import com.sun.tools.javadoc.Main;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * javadoc customer Doclet
 *
 * https://docs.oracle.com/javase/7/docs/technotes/guides/javadoc/doclet/overview.html
 * https://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html
 *
 * @author yan
 */
public class JavaDocReader {
    private static RootDoc root;

    public static class CustomerDoclet {
        public static boolean start(RootDoc root) {
            JavaDocReader.root = root;
            return true;
        }
    }

    public static RootDoc process(String[] extraArges) {
        List<String> argsOrderList = new ArrayList<>();
        argsOrderList.add("-doclet");
        argsOrderList.add(CustomerDoclet.class.getName());
        argsOrderList.addAll(Arrays.asList(extraArges));
        String[] args = argsOrderList.toArray(new String[argsOrderList.size()]);
        System.out.println(args);
        Main.execute(args);
        return root;
    }

    public static void process(List<String> sourcePaths, List<String> javapackages,
                               List<String> excludePackages, String outputDir) throws Exception {
        String paths = list2formatString(sourcePaths, ";");
        String includes = list2formatString(javapackages, ":");
        String excludes = list2formatString(excludePackages, ":");

        List<String> argsOrderList = new ArrayList<>();
        argsOrderList.add("-doclet");
        argsOrderList.add(CustomerDoclet.class.getName());

        if (paths != null && paths.length() > 0) {
            argsOrderList.add("-sourcepath");
            argsOrderList.add(paths);
        }

        argsOrderList.add("-encoding");
        argsOrderList.add("utf-8");
        argsOrderList.add("-verbose");

        if (includes != null && includes.length() > 0) {
            argsOrderList.add("-subpackages");
            argsOrderList.add(includes);
        }

        if (excludes != null && excludes.length() > 0) {
            argsOrderList.add("-exclude");
            argsOrderList.add(excludes);
        }

        String[] args = argsOrderList.toArray(new String[argsOrderList.size()]);
        System.out.println(Arrays.toString(args));

        Main.execute(args);

        File file = new File(outputDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file, new Date().toString() + ".txt");
        FileOutputStream outputStream = new FileOutputStream(file);

        ClassDoc[] classes = root.classes();
        if (classes != null) {
            for (int i = 0; i < classes.length; ++i) {
                if (classes[i].containingClass() == null && classes[i].isPublic()) {
                    Tag[] authorTags = classes[i].tags("author");
                    if (authorTags == null || authorTags.length == 0) {
                        String filename = classes[i].position().file().getAbsolutePath();
                        outputStream.write((filename+"\r\n").getBytes());
                    }
                }
            }
        }
        root = null;
        outputStream.flush();
        outputStream.close();
    }

    private static String list2formatString(List<String> srcs, String div) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int index=0; index<srcs.size(); index++) {
            if (index > 0) {
                stringBuilder.append(div);
            }
            stringBuilder.append(srcs.get(index));
        }
        return stringBuilder.toString();
    }
}
