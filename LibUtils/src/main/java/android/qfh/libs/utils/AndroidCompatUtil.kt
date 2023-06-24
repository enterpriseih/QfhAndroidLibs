@file:Suppress("DEPRECATION", "unused")

package android.qfh.libs.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * Android版本兼容
 */
@Suppress("SpellCheckingInspection")
object AndroidCompatUtil {
    /**
     * 根据文件对象获取其对外 URI
     * 兼容 Android 7.0
     * @param authority 7.0 以上此字段有效,表示映射路径的内容提供者的 authority 字段;
     * 如果不提供,则取 manifest.xml 内定义的第一个文件内容提供者的 authority
     */
    fun getFileUri(context: Context, file: File, authority: String? = null): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val application = context.applicationContext
            val authorityRaw = authority ?: application.packageManager.getProviderInfo(
                ComponentName(
                    application.packageName,
                    FileProvider::class.java.name
                ), PackageManager.GET_META_DATA
            ).authority
            FileProvider.getUriForFile(context, authorityRaw, file)
        } else {
            Uri.fromFile(file)
        }
    }

}