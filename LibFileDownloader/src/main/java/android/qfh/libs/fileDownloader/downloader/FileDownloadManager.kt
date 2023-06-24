package android.qfh.libs.fileDownloader.downloader

import android.os.Environment
import android.qfh.libs.fileDownloader.FileDownloaderInit
import android.qfh.libs.fileDownloader.logD
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * 该类负责单个大文件的断点下载
 */
object FileDownloadManager {

    private interface FileDownloadService {
        @Streaming
        @GET
        suspend fun downloadFile(
            @Url url: String,
            @Header("range") range: String = "bytes=0-",
            @QueryMap map: Map<String, String>
        ): Response<ResponseBody>
    }

    private val mFileDownloadService by lazy {
        FileDownloaderInit.retrofit.create(FileDownloadService::class.java)
    }

    // 下载的文件所在的目录
    private val downloadTempDir by lazy {
        FileDownloaderInit.appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
            .apply {
                if (!exists()) {
                    mkdirs()
                }
            }
    }


    /**
     *
     * 下载指定 url 对应的文件
     *
     * @param url 完整的网络下载路径
     * @param alreadyExistFile 已下载的部分文件，如果该字段为空，则会自动使用 UUID 命名；否则将使用该文件作为写入路径，如果该文件已存在，则自动进行断点续传
     * @param queryMap 可携带的 query 参数对
     * @suppress [alreadyExistFile] 如果不为空且该文件已存在 请保证 [downloadFile] 方法内部有权限通过 File API 直接读取该文件。
     * @suppress 如果服务器不支持断点续传，将自动删除已下载文件原有的下载内容然后重新下载
     */
    fun downloadFile(
        url: String,
        alreadyExistFile: File? = null,
        queryMap: Map<String, String>? = null
    ): DownloadResultWrapper {
        val channel = Channel<DownloadStatus>()

        var isStartDownload = false
        var isEndDownload = false

        // 当前已下载的字节数
        var alreadyDownloadTotalLength = 0L
        //上次回调进度时的字节数
        var lastCallbackDownloadLength = 0L
        // 上次回调进度时的时间
        var lastCallbackTime = 0L

        val fileDownloadJob = FileDownloaderInit.downloadScope.launch(Dispatchers.IO) {
            try {
                val tempFile =
                    alreadyExistFile ?: File(downloadTempDir, UUID.randomUUID().toString())
                logD("request downloading……")
                val downloadFileResponse = mFileDownloadService.downloadFile(
                    url,
                    "bytes=${alreadyExistFile?.length() ?: 0}-", queryMap ?: emptyMap()
                )
                logD("get download result")
                if (!downloadFileResponse.isSuccessful) {
                    logD("get download result error:${downloadFileResponse.message()}")
                    channel.send(DownloadStatus.DownloadError(HttpException(downloadFileResponse)))
                    return@launch
                }
                val body = downloadFileResponse.body()
                if (body == null) {
                    logD("get download result error:response body empty")
                    channel.send(DownloadStatus.DownloadError(Exception("response body empty")))
                    return@launch
                }
                logD("start read byte steam……")
                body.byteStream().use { ips ->
                    // 是否支持并使用断点续传
                    var isSupportBreakPointDownload = false

                    val totalLength = if (alreadyExistFile?.exists() != true) {
                        body.contentLength()
                    } else {
                        val contentRange = downloadFileResponse.headers().get("Content-Range")
                        if (contentRange.isNullOrEmpty()) {
                            body.contentLength()
                        } else {
                            logD("断点续传:url:${url},Content-Range:${contentRange}")
                            isSupportBreakPointDownload = true
                            contentRange.substringAfterLast("/").toLong()
                        }
                    }
                    channel.send(DownloadStatus.DownloadStart(tempFile, totalLength))

                    alreadyDownloadTotalLength = alreadyExistFile?.length() ?: 0
                    lastCallbackDownloadLength = alreadyDownloadTotalLength
                    lastCallbackTime = System.currentTimeMillis()

                    val bufferSize = DEFAULT_BUFFER_SIZE
                    var readLength = 0
                    val buffer = ByteArray(bufferSize)

                    isStartDownload = true

                    FileOutputStream(tempFile, isSupportBreakPointDownload).use { ops ->
                        while (isActive && ips.read(buffer, 0, bufferSize)
                                .also { readLength = it } != -1
                        ) {
                            ops.write(buffer, 0, readLength)
                            alreadyDownloadTotalLength += readLength
                        }
                    }
                }
                if (isActive) {
                    channel.send(DownloadStatus.DownloadSuccess)
                    logD("download success")
                } else {
                    channel.send(DownloadStatus.DownloadCancel)
                    logD("download canceled")
                }
            } catch (e: Exception) {
                logD("catch error:${e.message}")
                channel.send(DownloadStatus.DownloadError(e))
            } finally {
                isEndDownload = true
            }
        }
        FileDownloaderInit.downloadScope.launch {
            val callbackProgress = DownloadStatus.DownloadProgress(0, 0)
            while (true) {
                // 回调间隔 1000 ms
                delay(1000)
                if (isEndDownload) {
                    channel.close()
                    break
                }
                if (isStartDownload) {

                    val currentTime = System.currentTimeMillis()
                    val timeLine = (currentTime - lastCallbackTime) / 1000f
                    lastCallbackTime = currentTime

                    val speed = (alreadyDownloadTotalLength - lastCallbackDownloadLength) / timeLine
                    lastCallbackDownloadLength = alreadyDownloadTotalLength

                    channel.send(callbackProgress.apply {
                        currentLength = lastCallbackDownloadLength
                        this.speed = speed.toLong()
                    })
                }
            }
            logD("callback channel close")
        }
        return DownloadResultWrapper(
            channel.receiveAsFlow()
                .shareIn(FileDownloaderInit.downloadScope, SharingStarted.Eagerly, 1),
            downloadJob = fileDownloadJob
        )
    }

    /**
     * 下载结果包装类
     * @param result 可以从此 flow 中监听文件下载进度及结果信息
     * @suppress 可以通过 [cancelDownload] 方法取消下载；
     * 注意，取消 [result] 所在的协程仅仅取消监听，后台仍然会下载，因此请使用 [cancelDownload] 取消后台下载协程
     */
    class DownloadResultWrapper(
        val result: Flow<DownloadStatus>,
        private val downloadJob: Job
    ) {
        fun cancelDownload() {
            if (downloadJob.isActive) {
                downloadJob.cancel()
            }
        }
    }
}