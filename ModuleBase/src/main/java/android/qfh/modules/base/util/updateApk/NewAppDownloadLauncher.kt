package android.qfh.modules.base.util.updateApk

/**
 * 携带了返回给调用端的版本相关信息
 * @param remoteConfigInterface 远程配置文件映射的本地对象，可以进行类型强转
 * @param localVersionName 本地应用的 versionName
 *
 * @suppress 如果需要下载新 apk 包，可以调用 [download] 方法
 */
abstract class NewAppDownloadLauncher(
    val remoteConfigInterface: RemoteConfigInterface,
    val localVersionName: String
) {
    abstract fun download()
}