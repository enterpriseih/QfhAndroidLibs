package android.qfh.libs.utils.encrypt

import androidx.annotation.StringDef
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * 信息摘要算法
 * 建议使用 SHA 算法而不是 MD5 ，更安全
 * 建议使用 [HmacUtil] 内相关方法，更加安全
 */
object MessageDigestUtil {

    const val ALGORITHM_MD5 = "MD5"
    const val ALGORITHM_SHA1 = "SHA1"
    const val ALGORITHM_SHA256 = "SHA-256"
    const val ALGORITHM_SHA512 = "SHA-512"

    @StringDef(ALGORITHM_MD5, ALGORITHM_SHA1, ALGORITHM_SHA256, ALGORITHM_SHA512)
    @Retention(AnnotationRetention.SOURCE)
    annotation class AlgorithmForMessageDigest

    /**
     * 对字节数组进行摘要计算
     * @param data 进行摘要计算的数据
     * @param algorithm 所使用的算法
     * @return 生成的摘要字节数组
     */
    private fun digestFromByteArray(
        data: ByteArray,
        @AlgorithmForMessageDigest algorithm: String
    ): ByteArray {
        val messageDigest = MessageDigest.getInstance(algorithm)
        messageDigest.update(data)
        return messageDigest.digest()
    }

    /**
     * 对数据流进行摘要计算
     * @param inputStream 进行摘要计算的数据流
     * @param algorithm 所使用的算法
     * @return 生成的摘要字节数组
     * @suppress [inputStream]在该方法执行完之后会被关闭
     */
    private fun digestFromStream(
        inputStream: InputStream,
        @AlgorithmForMessageDigest algorithm: String
    ): ByteArray {
        val messageDigest = MessageDigest.getInstance(algorithm)
        val tempByteArray = ByteArray(1024 * 4)
        var readSize: Int
        inputStream.use {
            while (true) {
                readSize = it.read(tempByteArray)
                if (readSize != -1) {
                    messageDigest.update(tempByteArray, 0, readSize)
                } else break
            }
        }
        return messageDigest.digest()
    }


    fun digestToString(
        str: String,
        @AlgorithmForMessageDigest algorithm: String
    ) = ByteEncode.byteArrayToBase64Str(
        digestFromByteArray(
            str.toByteArray(StandardCharsets.UTF_8),
            algorithm
        )
    )

    fun digestToString(
        inputStream: InputStream,
        @AlgorithmForMessageDigest algorithm: String
    ) = ByteEncode.byteArrayToBase64Str(digestFromStream(inputStream, algorithm))

}