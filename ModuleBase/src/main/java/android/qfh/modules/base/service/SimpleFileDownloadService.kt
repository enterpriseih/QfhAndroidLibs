package android.qfh.modules.base.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.qfh.libs.fileDownloader.downloader.DownloadStatus
import android.qfh.libs.fileDownloader.downloader.FileDownloadManager
import android.qfh.modules.base.R
import android.qfh.modules.base.notification.channel.ChannelUtils
import android.qfh.modules.base.service.SimpleFileDownloadService.Companion.KEY_DST_PATH
import android.qfh.modules.base.service.SimpleFileDownloadService.Companion.KEY_TEMP_PATH
import android.qfh.modules.base.service.SimpleFileDownloadService.Companion.KEY_URL
import android.qfh.modules.base.start.ModuleBaseInitializer
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

/**
 * 简单的文件下载服务，提供基于文件名称的下载及简单校验，可以同时下载多个文件
 * 通过 [startService] 启动服务时携带必须的下载参数，包括远程文件下载路径、本地写入临时文件的路径，下载完成后的重命名路径，
 * 分别对应 [KEY_URL]、[KEY_TEMP_PATH]、[KEY_DST_PATH]
 * 任务执行过程中，会自动显示对应的进度通知；UI 层如若监控进度，可以通过 [registerDownloadCallable] 方法注册监听器，而对于
 * 未启动的任务，可以通过检查对应的临时写入文件路径和下载完成后的重命名路径进行判断：任务是已经执行完毕还是未开始
 *
 * @suppress 注意，如果本地写入临时文件不为空，则自动启用断点下载
 */
class SimpleFileDownloadService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val url = intent.getStringExtra(KEY_URL)
        val tempFilePath = intent.getStringExtra(KEY_TEMP_PATH)
        val dstFilePath = intent.getStringExtra(KEY_DST_PATH)
        if (!url.isNullOrEmpty() && !tempFilePath.isNullOrEmpty() && !dstFilePath.isNullOrEmpty()) {
            downloadFile(url, tempFilePath, dstFilePath)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // key 为临时文件路径，value 为对应的下载任务
    private val jobMap = mutableMapOf<String, Job>()

    /**
     * 启动文件下载
     * @param url 下载文件路径
     * @param tempFilePath 本地写入的文件路径，表示下载中状态的文件
     * @param dstFilePath 下载完毕后的重命名文件
     *
     * @suppress 如果该任务已经启动，则该次调用被跳过
     */
    private fun downloadFile(url: String, tempFilePath: String, dstFilePath: String) {
        if (jobMap[tempFilePath] != null) {
            return
        }
        val downloadJop = ModuleBaseInitializer.mainScope.launch(start = CoroutineStart.LAZY) {
            var mTotalLength = 0L
            val jobWrapper = FileDownloadManager.downloadFile(
                url = url,
                alreadyExistFile = File(tempFilePath)
            )
            jobWrapper.result.collect {
                when (it) {
                    is DownloadStatus.DownloadStart -> {
                        mTotalLength = it.totalLength
                    }
                    is DownloadStatus.DownloadError -> {
                        downloadOnError(tempFilePath, dstFilePath, it.e)
                    }
                    is DownloadStatus.DownloadProgress -> {
                        downloadOnProgress(tempFilePath, it.currentLength, mTotalLength, it.speed)
                    }
                    is DownloadStatus.DownloadSuccess -> {
                        if (tempFilePath != dstFilePath) {
                            File(tempFilePath).renameTo(File(dstFilePath))
                        }
                        downloadOnSuccess(tempFilePath, dstFilePath)
                    }
                    is DownloadStatus.DownloadCancel -> {
                        // 暂未提供取消下载功能
                    }
                }
            }
        }
        jobMap[tempFilePath] = downloadJop
        downloadJop.start()
        onJobStart(tempFilePath, dstFilePath)
    }

    // 批量通知的 id 管理
    private val mNotificationIdManager = NotificationIdManager()

    // key 为 tempPath，value 的 pair：first 为 notificationId,second 为 对应的 notificationBuilder
    private val notificationIdMap = mutableMapOf<String, Pair<Int, NotificationCompat.Builder>>()

    // key 为 tempPath，value 为对应的 UI 层回调监听
    private val callableMap = mutableMapOf<String, MutableList<ProgressCallable>>()


    private fun downloadOnProgress(
        tempFilePath: String,
        currentLength: Long,
        totalLength: Long,
        speed: Long
    ) {

        callableMap[tempFilePath]?.forEach {
            it.onProgress(currentLength, totalLength, speed)
        }

        val pair = notificationIdMap[tempFilePath] ?: return
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this@SimpleFileDownloadService)
            .notify(
                pair.first, pair.second.setProgress(
                    100, (currentLength.toFloat() / totalLength * 100).toInt(), false
                ).build()
            )
    }

    private fun downloadOnError(tempFilePath: String, dstFilePath: String, e: Exception) {

        callableMap[tempFilePath]?.forEach {
            it.onError(e)
        }

        onJobEnd(tempFilePath)

        NotificationCompat.Builder(this, ChannelUtils.ID_CHANNEL_COMMON_FOREGROUND_SERVICE)
            .setContentTitle("${dstFilePath.substringAfterLast("/")} 下载失败")
            .setContentText(e.message)
            .setSmallIcon(R.mipmap.base_ic_launcher)
            .setAutoCancel(true)
            .build().apply {
                if (ActivityCompat.checkSelfPermission(
                        this@SimpleFileDownloadService,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                NotificationManagerCompat.from(this@SimpleFileDownloadService)
                    .notify(mNotificationIdManager.getNotificationId(), this)
            }
    }


    private fun downloadOnSuccess(tempPath: String, dstFilePath: String) {

        callableMap[tempPath]?.forEach {
            it.onSuccess(dstFilePath)
        }

        onJobEnd(tempPath)

        NotificationCompat.Builder(this, ChannelUtils.ID_CHANNEL_COMMON_FOREGROUND_SERVICE)
            .setContentText("下载成功：${dstFilePath.substringAfterLast("/")}")
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.base_ic_launcher)
            .build().apply {
                if (ActivityCompat.checkSelfPermission(
                        this@SimpleFileDownloadService,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                NotificationManagerCompat.from(this@SimpleFileDownloadService)
                    .notify(mNotificationIdManager.getNotificationId(), this)
            }

    }


    private fun onJobStart(tempPath: String, fileName: String) {

        ChannelUtils.checkCommonForegroundServiceNotificationChannel(this)
        val notificationBuilder =
            NotificationCompat.Builder(this, ChannelUtils.ID_CHANNEL_COMMON_FOREGROUND_SERVICE)
                .setSmallIcon(R.mipmap.base_ic_launcher)
                .setContentTitle("正在下载:${fileName.substringAfterLast("/")}")
                .setProgress(100, 0, false)
        val notificationId = mNotificationIdManager.getNotificationId()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this).notify(notificationId, notificationBuilder.build())
        notificationIdMap[tempPath] = notificationId to notificationBuilder

    }

    private fun onJobEnd(tempPath: String) {
        jobMap.remove(tempPath)
        callableMap.remove(tempPath)

        val progressId = notificationIdMap[tempPath]?.first
        if (progressId != null) {
            NotificationManagerCompat.from(this).cancel(progressId)
            notificationIdMap.remove(tempPath)
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@SimpleFileDownloadService
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }

    /**
     * 注册对应下载任务的监听
     * @param lifecycleOwner 生命周期持有者，当其销毁时该注册器会自动取消
     * @param tempPath 对应 [downloadFile] 里面的 tempFile 参数
     * @param progressCallable 进度回调
     */
    fun registerDownloadCallable(
        lifecycleOwner: LifecycleOwner,
        tempPath: String,
        progressCallable: ProgressCallable,
    ) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    callableMap[tempPath]?.remove(progressCallable)
                    if (callableMap[tempPath].isNullOrEmpty()) {
                        callableMap.remove(tempPath)
                    }
                }
            }
        })
        val list = callableMap[tempPath]
        if(list!=null){
            list.add(progressCallable)
        }else{
            callableMap[tempPath]= mutableListOf<ProgressCallable>().apply {
                add(progressCallable)
            }
        }
    }

    interface ProgressCallable {
        fun onError(e: Exception)
        fun onProgress(currentLength: Long, totalLength: Long, speed: Long)
        fun onSuccess(dstFilePath: String)
    }

    private class NotificationIdManager {
        private val notificationIds = mutableListOf<Int>()
        fun getNotificationId(): Int {
            var start = 3000
            while (notificationIds.contains(start)) {
                start++
            }
            notificationIds.add(start)
            return start
        }
    }


    companion object {
        const val KEY_URL = "key_url"
        const val KEY_TEMP_PATH = "key_temp_path"
        const val KEY_DST_PATH = "key_dst_path"
    }

}