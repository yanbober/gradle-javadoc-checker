# gradle-javadoc-checker

一个检查 android、androidLibrary、java、javaLibrary 代码源文件 javadoc @author 的插件。

# 目录介绍

- src 下为插件代码。

- test-demo 下为测试代码。

# 原理介绍

利用 javadoc 命令工具的 tools.jar 进行自定义 doclet 操作，然后生成自定的 doc 进行解析判断。

具体参见 oracle 官方文档：

- (javadoc doclet)[https://docs.oracle.com/javase/7/docs/technotes/guides/javadoc/doclet/overview.html]

- (javadoc tools)[https://docs.oracle.com/javase/7/docs/technotes/tools/windows/javadoc.html]

# 使用介绍

插件默认使用操作如下：

```gradle
apply plugin: 'javadoc.checker'

buildscript {
    repositories {
        maven{ url './../repo/' }
    }

    dependencies {
        classpath 'cn.yan:gradle-javadoc-checker:1.0-SNAPSHOT'
    }
}

javadocChecker {
    includePackages = ["cn.demo"]
    outputDirectory = project.rootDir.absolutePath + File.separator + "report"
}

/*
//还可以自定义 task 依赖
task testChecker(type: JavaDocCheckerTask) {
    ......
}
*/
```

注意：默认使用只会识别 sourceSets main 的 java 文件。
