@file:Suppress("unused")

package android.qfh.libs.utils.media

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Document 操作帮助类
 */
object DocumentCompatUtil {

    /**
     * 调用文件存储器选择可写的文件夹
     * @param activity 在 activity 内调用时此字段不能为空
     * @param fragment 在 fragment 内调用时此字段不能为空
     * @param onChooseDir 选择到的 uri 回调
     */
    fun chooseDir(
        activity: FragmentActivity? = null,
        fragment: Fragment? = null,
        onChooseDir: (Uri) -> Unit
    ) {
        val operateHelperFragment = getOperateHelperFragment<OperateHelperFragment>(
            activity,
            fragment,
            TAG_MEDIA_AND_DOCUMENT
        )
        operateHelperFragment.chooseDir(onChooseDir)
    }

    /**
     * 调用存储框架让用户选择文件
     */
    fun chooseFile(
        activity: FragmentActivity? = null,
        fragment: Fragment? = null,
        onChooseFile: (Uri) -> Unit
    ) {
        val operateHelperFragment = getOperateHelperFragment<OperateHelperFragment>(
            activity,
            fragment,
            TAG_MEDIA_AND_DOCUMENT
        )
        operateHelperFragment.chooseFile(onChooseFile)
    }

    /**
     * 返回给定 DocumentFile 的 uri 对应的全路径
     * @param fileName 如果该 uri 不是目录而是文件，该字段表示文件的名称，因为 findDocumentPath 对文件 uri
     * 使用只会返回其父文件夹的路径，需要手动拼接
     * @sample [primary:音乐/flac/cd1]
     */
    fun getDocumentShowPath(context: Context, uri: Uri, fileName: String? = null): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val dirPath = DocumentsContract.findDocumentPath(
                context.contentResolver,
                uri
            )?.path?.lastOrNull()?.toString()
            if (fileName.isNullOrEmpty()) {
                dirPath ?: ""
            } else {
                "$dirPath/$fileName"
            }
        } else {
            uri.toString()
        }
    }

    /**
     * 文档拷贝
     * @param srcDocument 文件文档
     * @param dstDocument 目标文档
     */
    fun copyDocument(
        context: Context,
        srcDocument: DocumentFile,
        dstDocument:DocumentFile
    ) {
        val contentResolver = context.contentResolver
        contentResolver.openInputStream(srcDocument.uri)?.use { ips ->
            contentResolver.openOutputStream(dstDocument.uri)?.use { ops ->
                ips.copyTo(ops)
            }
        }
    }
}