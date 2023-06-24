@file:Suppress("unused")

package android.qfh.libs.utils.media

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.qfh.libs.utils.mimetype.MimeTypeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream

/**
 * SD 卡目录写入工具类
 * 场景为从 Android 11 开始适配分区存储
 */
object StorageWriteUtil {
    /**
     * 向 SD 卡 Download 目录下写入文件
     * @param fileName 带后缀的完整文件名；注意，该方法不会对该文件是否已存在做验证，需要自行先判断
     * @param onWriteData 写入文件数据时回调，执行于 IO 线程，不需要手动切换线程
     * @param checkWriteSDPermission 需要 SD 卡写入权限时回调，获取权限后需要把请求结果通过布尔值回调;此方法仅在低版本上会回调
     * @suppress 建议写入的文件为非音视图类型的普通文档，上述三种有专门的 URI 进行写入
     * @throws UrlInsertException
     * @throws OutputSteamOpenFailException
     * @throws DisAllowWriteException
     * @throws MkdirFailException
     */
    suspend fun writeDownloadDir(
        context: Context,
        fileName: String,
        onWriteData: (OutputStream) -> Unit,
        checkWriteSDPermission: ((Boolean) -> Unit) -> Unit
    ) {
        // Android 11 开始适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            withContext(Dispatchers.IO){
                val contentResolver = context.contentResolver
                val url = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    ContentValues().apply {
                        put(MediaStore.DownloadColumns.DISPLAY_NAME, fileName)
                        put(
                            MediaStore.Downloads.RELATIVE_PATH,
                            Environment.DIRECTORY_DOWNLOADS
                        )
                        put(MediaStore.DownloadColumns.IS_PENDING, 1)
                    }
                ) ?: throw UrlInsertException()
                (contentResolver.openOutputStream(url) ?: throw OutputSteamOpenFailException()).use {
                    onWriteData(it)
                }
                contentResolver.update(url, ContentValues().apply {
                    put(MediaStore.DownloadColumns.IS_PENDING, 0)
                }, null, null)
            }
        } else {
            callbackFlow {
                checkWriteSDPermission {
                    trySend(it)
                    close()
                }
                awaitClose { }
            }.collect {
                if (it) {
                    withContext(Dispatchers.IO){
                        val dir = File(
                            Environment.getExternalStorageDirectory(),
                            Environment.DIRECTORY_DOWNLOADS
                        ).apply {
                            if (!(isDirectory && exists())) {
                                if (!mkdirs()) {
                                    throw MkdirFailException()
                                }
                            }
                        }
                        File(dir, fileName).apply {
                            if (!exists()) {
                                createNewFile()
                            }
                        }.outputStream().use { ops ->
                            onWriteData(ops)
                        }
                    }
                } else {
                    throw DisAllowWriteException()
                }
            }
        }
    }

    /**
     * 向 SD 卡写入媒体文件，写入目录为对应文件类型的标准目录
     * @param fileName 带后缀的完整文件名；注意，该方法不会对该文件是否已存在做验证；会自动根据文件后缀名进行不同的表数据插入
     * @param onWriteData 写入文件数据时回调，执行于 IO 线程，不需要手动切换线程
     * @param checkWriteSDPermission 需要 SD 卡写入权限时回调，获取权限后需要把请求结果通过布尔值回调;此方法仅在低版本上会回调
     * @throws UnKnowMimeTypeException
     * @throws UrlInsertException
     * @throws OutputSteamOpenFailException
     */
    suspend fun writeMediaFile(
        context: Context,
        fileName: String,
        onWriteData: suspend (OutputStream) -> Unit,
        checkWriteSDPermission: ((Boolean) -> Unit) -> Unit
    ) {
        val mimetype = MimeTypeUtil.getMimeType(fileName) ?: throw UnKnowMimeTypeException()
        val tableUrl = when {
            MimeTypeUtil.isAudioFile(mimetype) -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            MimeTypeUtil.isVideoFile(mimetype) -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            MimeTypeUtil.isImageFile(mimetype) -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            else -> throw UnKnowMimeTypeException()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            callbackFlow {
                checkWriteSDPermission {
                    trySend(it)
                    close()
                }
                awaitClose { }
            }.collect {
                if (!it) {
                    throw DisAllowWriteException()
                }
            }
        }
        withContext(Dispatchers.IO){
            val contentResolver = context.contentResolver
            val url = contentResolver.insert(tableUrl,
                ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimetype)
                    put(
                        MediaStore.MediaColumns.DATA,
                        File(
                            File(
                                Environment.getExternalStorageDirectory(), when {
                                    MimeTypeUtil.isAudioFile(mimetype) -> Environment.DIRECTORY_MUSIC
                                    MimeTypeUtil.isVideoFile(mimetype) -> Environment.DIRECTORY_MOVIES
                                    MimeTypeUtil.isImageFile(mimetype) -> Environment.DIRECTORY_PICTURES
                                    else -> throw UnKnowMimeTypeException()
                                }
                            ),
                            fileName
                        ).absolutePath
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.DownloadColumns.IS_PENDING, 1)
                    }
                }
            ) ?: throw UrlInsertException()
            (contentResolver.openOutputStream(url) ?: throw OutputSteamOpenFailException()).use {
                onWriteData(it)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentResolver.update(url, ContentValues().apply {
                    put(MediaStore.DownloadColumns.IS_PENDING, 0)
                }, null, null)
            }
        }
    }


    /**
     * 插入媒体记录获取 url 时失败
     */
    class UrlInsertException : Exception()

    /**
     * 文件输出流打开失败
     */
    class OutputSteamOpenFailException : Exception()

    /**
     * 文件夹创建失败
     */
    class MkdirFailException : Exception()

    /**
     * 写入权限获取失败
     */
    class DisAllowWriteException : Exception()

    /**
     * 未知的文件后缀名
     */
    class UnKnowMimeTypeException : Exception()
}