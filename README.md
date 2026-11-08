# 喝水啦 (DrinkWater)

原生 Android 喝水提醒 App，无广告、无需联网。

- 每天 08:00–22:00，按整点和半点提醒（08:00、08:30 … 21:30）
- 一键开关；关掉 App 仍可收到通知；手机重启后自动恢复提醒
- 使用 AlarmManager，拥有精确闹钟权限时尽量准点；未授权时降级为可能延迟的提醒
- Android 8.0+；Android 13+ 需要通知权限，Android 12+ 建议允许“闹钟和提醒”
- 系统深度待机、省电限制或厂商后台管控可能导致通知延迟，不保证绝对准时

## 下载 APK（GitHub Actions）

打开仓库的 **Actions** → **Build DrinkWater APK** → 最新的成功运行 → **Artifacts** → **DrinkWater-debug-apk**。
下载 ZIP、解压，安装 **app-debug.apk**。首次启动 App，授予通知、准时闹钟权限。

## 本地编译

用 Android Studio 打开此目录，执行 **gradle :app:assembleDebug**（需 Android SDK 35、JDK 17 和 Gradle 8.10.2）。
APK 位于 **app/build/outputs/apk/debug/app-debug.apk**。

对 vivo / iQOO 手机，建议在系统设置里允许该 App 的通知、自启动及后台活动，并关闭该 App 的电池限制。
