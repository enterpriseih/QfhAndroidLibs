package android.qfh.modules.base.util.updateApk

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.qfh.libs.fileDownloader.downloader.DownloadWithoutProgress
import android.qfh.libs.fileDownloader.downloader.FileDownloadManager
import android.qfh.libs.utils.AndroidCompatUtil
import android.qfh.modules.base.service.SimpleFileDownloadService
import android.qfh.modules.base.start.ModuleBaseInitializer
import android.qfh.modules.base.util.updateApk.default.DefaultUpdateInterfaceFactory
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.launch
import java.io.File

/**
 * 应用升级管理类，封装了版本检测、后台服务下载、安装等系列流程
 * @param configUrl 远程配置文件的全路径地址
 * @param updateCallable 升级过程中的信息回调
 * @param updateInterfaceFactory 升级流程的功能接口，用于自定义扩展，默认流程可以使用 [DefaultUpdateInterfaceFactory]
 *
 * 调用 [checkUpdateApp] 进行应用更新检查
 */
class AppUpdateManager(
    private val configUrl: String,
    private val updateCallable: UpdateCallable,
    private val updateInterfaceFactory: UpdateInterfaceFactory
) {
    /**
     * 检查是否有更新版本
     * @param lifecycleOwner 访问远程配置文件过程的生命周期 scope
     * @suppress 新版本应用下载过程由后台服务负责，不受该生命周期限制
     */
    fun checkUpdateApp(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycleScope.launch {
            val configResultWrapper = FileDownloadManager.downloadFile(configUrl).result
            configResultWrapper.collect(
                DownloadWithoutProgress(
                    onError = updateCallable::onConfigFileDownloadException,
                    onSuccess = {
                        checkVersion(it, lifecycleOwner)
                    })
            )
        }
    }

    private fun checkVersion(downloadConfigFile: File, lifecycleOwner: LifecycleOwner) {

        val configObj: RemoteConfigInterface
        try {
            val configParser = updateInterfaceFactory.createConfigParser()
            configObj = configParser.parserConfigFile(downloadConfigFile)
        } catch (e: Exception) {
            updateCallable.onConfigFileParserException(e)
            return
        }

        val context = ModuleBaseInitializer.appContext

        @Suppress("DEPRECATION")
        val packageInfo =
            context.packageManager.getPackageInfo(context.packageName, 0)

        @Suppress("DEPRECATION")
        val localVersionCode = packageInfo.versionCode
        val localVersionName = packageInfo.versionName

        if (configObj.getRemoteVersionCode() <= localVersionCode) {
            updateCallable.onGetConfigResult(false)
            return
        }

        val localDstApkFileName =
            "${packageInfo.packageName}-${configObj.getRemoteVersionCode()}.apk"
        val localDstApkFile = File(getDownloadDir(), localDstApkFileName)
        if (localDstApkFile.exists()) {
            updateCallable.onGetConfigResult(true, localDstApkFile)
        } else {
            val tempDownloadFile = File(getDownloadDir(), "$localDstApkFileName.downloading")
            // 清除无效文件
            if (!tempDownloadFile.exists()) {
                getDownloadDir().listFiles()?.forEach {
                    it.deleteRecursively()
                }
            }
            updateCallable.onGetConfigResult(
                true,
                null,
                object : NewAppDownloadLauncher(configObj, localVersionName) {
                    override fun download() {
                        bindServiceAndGetCallback(lifecycleOwner, tempDownloadFile.absolutePath)
                        val intent = Intent(context, SimpleFileDownloadService::class.java).apply {
                            putExtra(SimpleFileDownloadService.KEY_URL, configObj.getRemoteApkUrl())
                            putExtra(
                                SimpleFileDownloadService.KEY_TEMP_PATH,
                                tempDownloadFile.absolutePath
                            )
                            putExtra(
                                SimpleFileDownloadService.KEY_DST_PATH,
                                localDstApkFile.absolutePath
                            )
                        }
                        context.startService(intent)
                    }
                })
        }
    }

    private fun bindServiceAndGetCallback(lifecycleOwner: LifecycleOwner, tempPath: String) {
        val context = ModuleBaseInitializer.appContext
        val coon = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder) {
                val downloadService =
                    (service as SimpleFileDownloadService.LocalBinder).getService()
                downloadService.registerDownloadCallable(
                    lifecycleOwner,
                    tempPath,
                    object : SimpleFileDownloadService.ProgressCallable {
                        override fun onError(e: Exception) {
                            updateCallable.onDownloadResult(null, e)
                        }

                        override fun onProgress(
                            currentLength: Long,
                            totalLength: Long,
                            speed: Long
                        ) {
                            updateCallable.onDownloadProgress(currentLength, totalLength, speed)
                        }

                        override fun onSuccess(dstFilePath: String) {
                            updateCallable.onDownloadResult(File(dstFilePath), null)
                        }

                    })
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                updateCallable.onDownloadResult(null, Exception("下载服务意外断开"))
            }
        }
        context.bindService(
            Intent(context, SimpleFileDownloadService::class.java),
            coon,
            Context.BIND_AUTO_CREATE
        )
    }

    interface UpdateCallable {
        /**
         * 配置文件获取失败
         */
        fun onConfigFileDownloadException(e: Exception)

        /**
         * 配置文件解析失败
         */
        fun onConfigFileParserException(e: Exception)

        /**
         * 配置文件解析成功
         * @param hasNewVersion 是否有新版本；如果该值为 false，后续字段将被忽略
         * @param alreadyDownloadFile 已经下载完毕的 apk 包；如果该字段不为空，后续字段将被忽略
         * @param newAppDownloadLauncher 需要下载新的 app 时的可选操作选项
         */
        fun onGetConfigResult(
            hasNewVersion: Boolean,
            alreadyDownloadFile: File? = null,
            newAppDownloadLauncher: NewAppDownloadLauncher? = null
        )

        /**
         * 下载进度回调，回调间隔为 1 s
         * @param currentLength 当前已下载进度
         * @param totalLength 文件的总大小
         * @param speed 文件下载速度，单位为 字节/每秒
         *
         * @suppress 该方法可选实现，默认服务会在通知栏显示对应进度
         */
        fun onDownloadProgress(currentLength: Long, totalLength: Long, speed: Long)

        /**
         * 下载 apk 文件的结果
         * @param file 如果不为空，则下载成功
         * @param exception 如果不为空，表示下载失败
         */
        fun onDownloadResult(file: File?, exception: Exception?)
    }

    companion object {
        private fun getDownloadDir(): File {
            val downloadDir =
                ModuleBaseInitializer.appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
            if ((!downloadDir.exists()) || (!downloadDir.isDirectory)) {
                downloadDir.mkdirs()
            }
            return downloadDir
        }

        fun installApk(activity: FragmentActivity, apkFile: File) {
            val url = AndroidCompatUtil.getFileUri(
                ModuleBaseInitializer.appContext,
                apkFile, "${activity.application.packageName}.fileprovider"
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(url, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionX.init(activity)
                    .permissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                    .onExplainRequestReason { scope, deniedList ->
                        val message = "需要授予安装权限才能正常升级"
                        scope.showRequestReasonDialog(deniedList, message, "确定", "取消")
                    }
                    .request { allGranted, _, _ ->
                        if (allGranted) {
                            activity.startActivity(intent)
                        }
                    }
            } else {
                activity.startActivity(intent)
            }
        }
    }
}