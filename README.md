# 短信过滤 MVP

一个纯本地运行的 Android 短信过滤演示项目，使用 Kotlin 和 Jetpack Compose。项目按功能边界拆分为 `:app` 与 `:filter-engine`，方便后续分别扩展交互、存储和过滤策略。

## 当前功能

- 手动粘贴短信或选择内置样例，分类为正常、营销、疑似诈骗。
- 显示命中的关键词和匹配原因；疑似诈骗规则优先于营销规则。
- 在界面编辑关键词，每行一个；规则通过 Android `SharedPreferences` 存储在本机。
- 分类引擎不依赖 Android，可单独测试和迭代。
- 不申请短信权限，不读取或上传短信内容。

## 模块结构

- `:app`：Compose 界面、Activity 和本地规则存储。
- `:filter-engine`：纯 Kotlin 领域模型、规则匹配引擎及单元测试。

## 构建与测试

需要 JDK 17 或更高版本，以及 Android SDK Platform 35。安装 Android Studio/SDK 后，在项目根目录运行：

```powershell
.\gradlew.bat :filter-engine:test
.\gradlew.bat :app:assembleDebug
```

## 当前范围与后续系统短信集成

本 MVP 只对用户输入的文本进行演示分类；它**不是**默认短信应用，不会访问收件箱、收发短信或在系统层拦截短信。

后续若要处理真实短信，需要实现 Android 默认短信应用所需的完整组件和交互（短信接收/发送、会话与数据库、默认应用角色申请等），并遵循 Android 版本对应的 `RoleManager.ROLE_SMS` 默认短信角色及受限短信权限要求（例如 `RECEIVE_SMS`、`READ_SMS`、`SEND_SMS`）；仅声明权限并不足以获得默认短信处理能力。集成前还需核对目标 Android 版本和 Google Play 的短信权限政策。
