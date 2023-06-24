package android.qfh.libs.fileDownloader.downloader

import java.io.File

/**
 * 用于表示单个文件下载的进度回调
 */
sealed class DownloadStatus {
    /**
     * 请求链接成功但还未开始读取文件流时被回调，通知调用者下载已开始
     * @param totalLength 下载的文件总的文件长度（不一定是该次下载的长度，因为可能断点续传是部分下载）
     * @param file 下载对应的本地临时文件。默认该文件的名称为 UUID 生成的无规则字符串
     * 该文件位于 [Context.getExternalFilesDir(DIRECTORY_DOWNLOADS)]下，下载结束后，需要自行处理该文件的存删。
     */
    class DownloadStart(val file: File, val totalLength: Long) : DownloadStatus()

    /**
     * 下载过程中回调
     * @param currentLength 当前已下载的字节数
     * @param speed 文件的实时下载速度，每秒下载的字节数
     * @suppress 对于同一个下载请求，回调的多次该进度实例 [DownloadProgress] 都是同一个实例，不会重复新建实例
     */
    class DownloadProgress(var currentLength: Long = 0, var speed: Long) : DownloadStatus()

    /**
     * 下载成功时被回调
     */
    object DownloadSuccess : DownloadStatus()

    /**
     * 下载失败后的回调
     * 如果该方法被回调，[DownloadSuccess] 将不会被回调
     * @param e 异常信息
     * @suppress 下载失败后，需要手动处理临时下载文件
     */
    class DownloadError(val e: Exception) : DownloadStatus()

    /**
     * 下载取消
     */
    object DownloadCancel : DownloadStatus()
}
