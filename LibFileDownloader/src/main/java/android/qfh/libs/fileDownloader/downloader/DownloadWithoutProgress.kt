package android.qfh.libs.fileDownloader.downloader

import kotlinx.coroutines.flow.FlowCollector
import java.io.File

class DownloadWithoutProgress(
    private val onError: (Exception) -> Unit,
    private val onSuccess: (File) -> Unit
) : FlowCollector<DownloadStatus> {
    private var downloadFile: File? = null
    override suspend fun emit(value: DownloadStatus) {
        when (value) {
            is DownloadStatus.DownloadStart -> {
                downloadFile = value.file
            }
            is DownloadStatus.DownloadError -> {
                downloadFile?.delete()
                onError(value.e)
            }
            is DownloadStatus.DownloadSuccess -> {
                onSuccess(downloadFile!!)
            }
            is DownloadStatus.DownloadProgress, DownloadStatus.DownloadCancel -> {

            }
        }
    }
}