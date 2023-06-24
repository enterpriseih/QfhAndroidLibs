package android.qfh.libs.utils

import java.io.File

/**
 * 对当前文件进行删除，如果是文件夹，则会递归删除当前文件夹下的所有文件（包含自身）
 *
 * @param deleteFail 删除失败的文件(不包含文件夹，因为如果某些文件被过滤掉，其父级文件夹是无法删除的)
 * @param fileFilter 文件删除过滤规则,默认返回 true ；返回 true表示此文件需要删除
 * @param dirFilter 文件夹删除过滤删除,默认返回 true;返回 true 表示此文件夹需要被删除,否则直接跳过该文件夹
 * @suppress 文件夹删除规则记得考虑当前根文件夹,否则得不到预期效果
 */
fun File.deleteSelf(
    deleteFail: MutableList<File>? = null,
    fileFilter: (File) -> Boolean = { true },
    dirFilter: (File) -> Boolean = { true }
) {
    if (!exists()) return
    if (isDirectory) {
        if (dirFilter(this)) {
            val listFiles = this.listFiles()
            listFiles?.forEach {
                it.deleteSelf(deleteFail, fileFilter, dirFilter)
            }
            delete()
        }
    } else {
        if (fileFilter(this)) {
            if (!delete()) {
                deleteFail?.add(this)
            }
        }
    }
}

/**
 * 根据指定规则递归扫描文件夹
 * @param onReceiveFile 当扫描到文件时被回调（不会回调文件夹）
 * @param fileFilter 文件过滤器,默认返回 true，表示该文件需要被扫描；如果返回 false，[onReceiveFile]则接收不到该文件的回调
 * @param dirFilter 文件夹过滤器，,默认返回 true，表示该文件嘉需要被扫描，如果返回 false，该文件夹将被跳过。
 */
fun File.searchChildFileWithFilter(
    onReceiveFile: (File) -> Unit,
    fileFilter: (File) -> Boolean = { true },
    dirFilter: (File) -> Boolean = { true }
) {
    if (isDirectory) {
        if (dirFilter(this)) {
            val listFiles = this.listFiles()
            listFiles?.forEach {
                it.searchChildFileWithFilter(onReceiveFile, fileFilter, dirFilter)
            }
        }
    } else {
        if (fileFilter(this)) {
            onReceiveFile(this)
        }

    }
}

/**
 * 文件或者文件夹拷贝
 * 目标文件总是会被先删除然后重新创建
 * 如果源文件为文件，则为文件拷贝，否则为文件夹递归拷贝
 * @suppress 如果目标文件已存在，会进行删除重建
 * @param fileFilter 文件过滤器,默认为 true；如果为 false，复制时将跳过此文件
 * @param dirFilter 文件夹过滤器,默认为 true；如果为 false，复制时将跳过此文件夹
 */
@Suppress("MemberVisibilityCanBePrivate")
fun copyFileToFile(
    src: File, dst: File,
    fileFilter: (File) -> Boolean = { true },
    dirFilter: (File) -> Boolean = { true }
) {
    if (src.isDirectory) {
        if (dirFilter(src)) {
            if (dst.exists() && dst.isDirectory) {
                dst.deleteSelf()
            }
            dst.mkdir()
            val arrayOfFiles = src.listFiles() ?: return
            arrayOfFiles.forEach {
                val dstFile = File(dst, it.name)
                copyFileToFile(it, dstFile, fileFilter, dirFilter)
            }
        }
    } else {
        if (fileFilter(src)) {
            src.copyTo(dst, true)
        }

    }
}

