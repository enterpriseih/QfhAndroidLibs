package android.qfh.modules.base.util.updateApk

/**
 * 定义了远程配置文件所提供的必要属性
 * @suppress 如果自定义解析规则需要实现该接口
 */
interface RemoteConfigInterface {
    fun getRemoteVersionCode(): Int
    fun getRemoteVersionName(): String
    fun getRemoteApkUrl(): String
}