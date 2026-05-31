# CheckJavaVersion - Java Class 版本检测工具

## 项目简介

在企业级 Java 项目开发中，经常需要引入第三方 Jar 包。不同版本的 Jar 包使用不同 JDK 编译，当项目中混入了用更高版本 JDK 编译的 class 文件时，会引发 `UnsupportedClassVersionError` 异常，导致应用启动失败。

**CheckJavaVersion** 是一款基于 Java Swing 的可视化工具，专门用于检测 Jar/Zip 包中所有 class 文件的 JDK 编译版本，帮助开发者快速定位版本冲突问题。

## 工作原理

1. **选择文件** — 通过文件选择器选取需要检测的 Jar 或 Zip 文件
2. **自动解压** — 将文件解压到系统临时目录
3. **递归扫描** — 遍历所有 `.class` 和嵌套的 `.jar` 文件
4. **版本识别** — 读取 class 文件字节码中的 `major_version` 字段，映射为对应的 JDK 版本号
5. **结果展示** — 以表格形式列出每个 class 文件及其编译版本，并按版本分组统计

## 支持版本

| 主版本号 | JDK 版本 |
|:---:|:---:|
| 45 | JDK 1.1 |
| 46 | JDK 1.2 |
| 47 | JDK 1.3 |
| 48 | JDK 1.4 |
| 49 | JDK 5 |
| 50 | JDK 6 |
| 51 | JDK 7 |
| 52 | JDK 8 |
| 53 | JDK 9 |
| 54 | JDK 10 |
| 55 | JDK 11 |
| 56 | JDK 12 |
| 57 | JDK 13 |
| 58 | JDK 14 |
| 59 | JDK 15 |
| 60 | JDK 16 |
| 61 | JDK 17 |
| 62 | JDK 18 |
| 63 | JDK 19 |
| 64 | JDK 20 |
| 65 | JDK 21 |
| 66 | JDK 22 |
| 67 | JDK 23 |
| 68 | JDK 24 |

## 适用场景

- **老应用升级** — 排查旧项目中引入的高版本 Jar，避免运行时版本冲突
- **依赖检查** — 在集成第三方库前，快速确认其编译版本是否符合项目要求
- **CI/CD 辅助** — 集成到构建流程中，自动检测制品中的 class 版本分布

## 技术栈

- Java Swing（GUI）
- 纯 JDK 实现，无第三方依赖
- 通过 class 文件 Magic Number（`0xCAFEBABE`）和主版本号识别 JDK 版本
