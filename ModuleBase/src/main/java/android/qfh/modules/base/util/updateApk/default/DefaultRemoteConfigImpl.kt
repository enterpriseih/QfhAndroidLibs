package android.qfh.modules.base.util.updateApk.default

import android.qfh.modules.base.util.updateApk.RemoteConfigInterface

class DefaultRemoteConfigImpl(
    private val versionCode: Int?,
    private val versionName: String?,
    private val apkUrl: String?,
    val apkSize: String?,
    val updateInfo: List<String>?
) : RemoteConfigInterface {
    override fun getRemoteVersionCode(): Int {
        return versionCode ?: 0
    }

    override fun getRemoteVersionName(): String {
        return versionName ?: ""
    }

    override fun getRemoteApkUrl(): String {
        return apkUrl ?: ""
    }
}