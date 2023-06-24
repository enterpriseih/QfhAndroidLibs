package android.qfh.modules.base.network.requestbody

import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Okio
import java.io.InputStream

/**
 * 文件上传进度监听的 [RequestBody]
 * @param mediaType 文件的数据类型
 * @param inputStream 文件流
 * @param coroutineScope 上传进度信息回调所在的协程
 * @param callback 文件上传进度回调
 * @suppress 文件上传完成要以 http 接口为准，[callback] 可能最后一部分进度信息还未回调就
 * 已经结束
 */
class ProgressRequestBody(
    private val mediaType: MediaType?,
    private val inputStream: InputStream,
    private val coroutineScope: CoroutineScope,
    private val callback: ProgressCallback,
) : RequestBody() {

    private var lastProgressByteCount = 0L
    private var sumCount = 0L
    // 回调间隔
    private val delayTime = 1000L
    private val timerJob: Job = coroutineScope.launch {
        while (true) {
            delay(delayTime)
            callback.onProgress(sumCount, sumCount - lastProgressByteCount)
            lastProgressByteCount = sumCount
        }
    }

    override fun contentType(): MediaType? {
        return mediaType
    }

    override fun contentLength(): Long {
        return inputStream.available().toLong()
    }

    override fun writeTo(sink: BufferedSink) {
        Okio.source(inputStream).use {
            Okio.buffer(object : ForwardingSink(sink) {
                override fun write(source: Buffer, byteCount: Long) {
                    super.write(source, byteCount)
                    sumCount += byteCount
                }
            }).run {
                writeAll(it)
                // 此步骤必须，否则调用 close 时会因为部分数据没有写入报失败
                flush()
                timerJob.cancel()
            }
        }
    }

    interface ProgressCallback {
        /**
         * @param progress 当前已经传输的字节长度
         * @param speed 下载速度，当前每[delayTime]毫秒传输的数据量，默认为 1 S
         * @suppress 回调线程取决于 [ProgressRequestBody.coroutineScope] 所在的线程
         */
        fun onProgress(progress: Long, speed: Long)
    }
}