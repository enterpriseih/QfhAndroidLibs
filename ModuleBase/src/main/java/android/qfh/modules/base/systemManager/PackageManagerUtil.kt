package android.qfh.modules.base.systemManager

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

object PackageManagerUtil {

    /**
     * 获取指定应用包名的相关信息
     * @suppress 注意需要在清单文件里声明对应包名的 query 参数，否则高版本可能查询不到
     */
    fun getPackageInfo(context: Context, packageName: String): PackageInfo? {
        return try {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    /**
     * 该应用是否正在运行中
     */
    fun packageIsRunning(packageInfo: PackageInfo): Boolean {
        return (ApplicationInfo.FLAG_STOPPED and packageInfo.applicationInfo.flags) == 0
    }

    /**
     * 该应用是否为系统应用
     */
    fun packageIsSystemApp(packageInfo: PackageInfo): Boolean {
        return (ApplicationInfo.FLAG_SYSTEM and packageInfo.applicationInfo.flags) == 1
    }

}