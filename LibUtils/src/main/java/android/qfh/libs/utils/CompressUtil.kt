package android.qfh.libs.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * 文件压缩解压缩
 */
object CompressUtil {
    private val systemSeparator = File.separator

    const val TYPE_ZIP = "zip"

    /**
     * 压缩文件或者文件夹
     * @param srcFilePath 被压缩的文件或者文件夹,被压缩文件必须存在
     * @param dstFilePath 被压缩后的压缩文件路径,如果文件已存在,文件会被删除重建;
     * 文件后缀建议根据压缩方式取名,如 *.zip
     * @param type 压缩算法 当前可使用算法如下:zip压缩[TYPE_ZIP]
     */
    fun compress(srcFilePath: String, dstFilePath: String, type: String = TYPE_ZIP) {
        if (type == TYPE_ZIP) {
            zip(srcFilePath, dstFilePath)
        }
    }

    /**
     * 解压缩文件
     * @param compressFilePath 解压缩文件的路径
     * @param dstDirPath 解压到的文件夹,文件夹需要已存在，程序会自动在该文件夹下创建解压目录
     * @param type 压缩算法 当前可使用算法如下:zip压缩[TYPE_ZIP]
     * @suppress 如果压缩文件不是 utf-8 编码则会抛出异常；
     */
    fun unCompress(compressFilePath: String, dstDirPath: String, type: String = TYPE_ZIP) {
        if (type == TYPE_ZIP) {
            unzip(compressFilePath, dstDirPath)
        }
    }

    //==================zip============================
    private fun zip(src: String, dst: String) {
        //提供了一个数据项压缩成一个ZIP归档输出流
        var out: ZipOutputStream? = null
        try {
            val fileOrDirectory = File(src) //压缩文件路径
            out = ZipOutputStream(FileOutputStream(File(dst)))
            //如果此文件是一个文件，否则为false。
            if (fileOrDirectory.isFile) {
                zipFileOrDirectory(out, fileOrDirectory, "")
            } else {
                //返回一个文件或空阵列。
                fileOrDirectory.listFiles()?.forEach {
                    zipFileOrDirectory(out, it, "")
                }
            }
        } finally {
            //关闭输出流
            out?.close()
        }
    }

    private fun unzip(zipFilePath: String?, outputDirectory: String) {
        var zipFile: ZipFile? = null
        try {
            zipFile = ZipFile(zipFilePath)
            val e: Enumeration<out ZipEntry> = zipFile.entries()
            var zipEntry: ZipEntry?
            while (e.hasMoreElements()) {
                zipEntry = e.nextElement()
                val entryName = zipEntry.name
                var `in`: InputStream? = null
                var out: FileOutputStream? = null
                try {
                    if (zipEntry.isDirectory) {
                        var name = zipEntry.name
                        name = name.substring(0, name.length - 1)
                        val f = File(
                            outputDirectory + systemSeparator
                                    + name
                        )
                        f.mkdirs()
                    } else {
                        var index = entryName.lastIndexOf(systemSeparator)
                        if (index != -1) {
                            val df = File(
                                outputDirectory + systemSeparator
                                        + entryName.substring(0, index)
                            )
                            df.mkdirs()
                        }
                        index = entryName.lastIndexOf("/")
                        if (index != -1) {
                            val df = File(
                                outputDirectory + systemSeparator
                                        + entryName.substring(0, index)
                            )
                            df.mkdirs()
                        }
                        val f = File(
                            outputDirectory + systemSeparator
                                    + zipEntry.name
                        )
                        `in` = zipFile.getInputStream(zipEntry)
                        out = FileOutputStream(f)
                        var c: Int
                        val by = ByteArray(1024)
                        while (`in`.read(by).also { c = it } != -1) {
                            out.write(by, 0, c)
                        }
                        out.flush()
                    }
                } finally {
                    `in`?.close()
                    out?.close()
                }
            }
        } finally {
            zipFile?.close()
        }
    }

    private fun zipFileOrDirectory(
        out: ZipOutputStream,
        fileOrDirectory: File,
        curPath: String
    ) {
        //从文件中读取字节的输入流
        var `in`: FileInputStream? = null
        try {
            //如果此文件是一个目录，否则返回false。
            if (!fileOrDirectory.isDirectory) {
                // 压缩文件
                val buffer = ByteArray(4096)
                var bytesRead: Int
                `in` = FileInputStream(fileOrDirectory)
                //实例代表一个条目内的ZIP归档
                val entry = ZipEntry(
                    curPath
                            + fileOrDirectory.name
                )
                //条目的信息写入底层流
                out.putNextEntry(entry)
                while (`in`.read(buffer).also { bytesRead = it } != -1) {
                    out.write(buffer, 0, bytesRead)
                }
                out.closeEntry()
            } else {
                // 压缩目录
                fileOrDirectory.listFiles()?.forEach {
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(
                        out, it, curPath
                                + fileOrDirectory.name + systemSeparator
                    )
                }
            }
        } finally {
            `in`?.close()
        }
    }
    //==================zip============================
}