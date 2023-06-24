package android.qfh.modules.base.systemManager

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.qfh.modules.base.start.ModuleBaseInitializer
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.io.RandomAccessFile


object DeviceInfoUtil {
    /**
     * 获取内存信息
     * totalMem:内核可访问的总内存。这基本上是设备的 RAM 大小，不包括内核以下的固定分配，如 DMA 缓冲区、基带 CPU 的 RAM 等。
     * threshold:可用性内存的阈值，在该阈值下，我们认为内存较低并开始终止后台服务和其他非外来进程。
     * lowMemory:如果系统认为自己当前处于内存不足状态，则设置为 true。
     */
    fun getAvailMemory(): ActivityManager.MemoryInfo {
        val am =
            ModuleBaseInitializer.appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return mi
    }

    /**
     * 获取内存总共空间
     * @suppress 该方法获取到的总内存值和 [getAvailMemory] 方法获取到的是一样的
     */
    private fun getTotalMemory(): Long {
        val str1 = "/proc/meminfo" // 系统内存信息文件
        val str2: String
        val arrayOfString: Array<String>
        var initialMemory: Long = 0
        try {
            val localFileReader = FileReader(str1)
            val localBufferedReader = BufferedReader(
                localFileReader, 8192
            )
            str2 = localBufferedReader.readLine() // 读取 mem info 第一行，系统总内存大小
            arrayOfString =
                str2.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            initialMemory = arrayOfString[1].toInt() * 1024L // 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return initialMemory
    }

    /**
     * 获取手机 SD卡存储信息
     * 分别返回块大小、可用的块、总的块
     */
    fun getExternalMemorySize(): Triple<Long, Long, Long> {
        return getPathSize(Environment.getExternalStorageDirectory().absolutePath)
    }

    private fun getPathSize(path: String): Triple<Long, Long, Long> {
        val stat = StatFs(path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        val totalBlocks = stat.blockCountLong
        return Triple(blockSize, availableBlocks, totalBlocks)
    }

    /**
     * 获取 cpu 使用率
     * @return 取值为 0 到 1
     * @suppress 高版本上无效，会报无权限访问对应的虚拟文件
     */
    fun getCpuUsed(): Float {
        try {
            val reader = RandomAccessFile("/proc/stat", "r")
            var load = reader.readLine()
            var toks = load.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val idle1 = toks[5].toLong()
            val cpu1 =
                toks[2].toLong() + toks[3].toLong() + toks[4].toLong() + toks[6].toLong() + toks[7].toLong() + toks[8].toLong()
            try {
                Thread.sleep(360)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            reader.seek(0)
            load = reader.readLine()
            reader.close()
            toks = load.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val idle2 = toks[5].toLong()
            val cpu2 =
                toks[2].toLong() + toks[3].toLong() + toks[4].toLong() + toks[6].toLong() + toks[7].toLong() + toks[8].toLong()
            return (cpu2 - cpu1).toFloat() / (cpu2 + idle2 - (cpu1 + idle1))
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return 0f
    }

    /**
     * 仅适用于简单场景，不同的应用获取到的是不一致的
     */
    fun getAndroidId(context: Context): String? {
        return Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

}
