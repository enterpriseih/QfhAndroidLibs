package android.qfh.libs.utils.mimetype

import java.net.URLConnection

object MimeTypeUtil {
    private const val imageMimeTypeStart = "image/"
    private const val audioMimeTypeStart = "audio/"
    private const val videoMimeTypeStart = "video/"


    //所有文件后缀名集合
    private val mFileNameMap by lazy {
        URLConnection.getFileNameMap()
    }

    // 根据文件后缀区分 mime type
    fun getMimeType(fileName: String): String? {
        return mFileNameMap.getContentTypeFor(fileName)
    }

    fun isImageFile(mimeType: String): Boolean {
        return mimeType.startsWith(imageMimeTypeStart)
    }

    fun isAudioFile(mimeType: String): Boolean {
        return mimeType.startsWith(audioMimeTypeStart)
    }

    fun isVideoFile(mimeType: String): Boolean {
        return mimeType.startsWith(videoMimeTypeStart)
    }
}