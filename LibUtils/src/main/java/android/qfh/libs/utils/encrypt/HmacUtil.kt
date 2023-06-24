@file:Suppress("unused")

package android.qfh.libs.utils.encrypt

import androidx.annotation.StringDef
import java.io.InputStream
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/**
 * HmacXXX 相关算法做摘要生成,[MessageDigestUtil]相关方法的替代品，更加安全，本质还是摘要算法
 */
object HmacUtil {

    const val ALGORITHM_HMAC_MD5 = "HmacMD5"
    const val ALGORITHM_HMAC_SHA1 = "HmacSHA1"
    const val ALGORITHM_HMAC_SHA256 = "HmacSHA256"
    const val ALGORITHM_HMAC_SHA512 = "HmacSHA512"

    @StringDef(
        ALGORITHM_HMAC_MD5,
        ALGORITHM_HMAC_SHA1,
        ALGORITHM_HMAC_SHA256,
        ALGORITHM_HMAC_SHA512
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class AlgorithmForHmac

    /**
     * 对字节数组进行摘要计算
     * @param data 进行摘要计算的数据
     * @param hKey Hmac相关算法所需要的 key
     * @param algorithm 所使用的算法
     * @return 生成的摘要字节数组
     */
    fun digestFromByteArray(
        data: ByteArray,
        hKey: ByteArray,
        @AlgorithmForHmac algorithm: String
    ): ByteArray {
        val key: SecretKey = SecretKeySpec(hKey, algorithm)
        val mac: Mac = Mac.getInstance(algorithm)
        mac.init(key)
        mac.update(data)
        return mac.doFinal()
    }

    /**
     * 对数据流进行摘要计算
     * @param inputStream 进行摘要计算的数据流
     * @param hKey Hmac相关算法所需要的key
     * @param algorithm 所使用的算法
     * @return 生成的摘要字节数组
     * @suppress 数据流被读取后被关闭，外界无法在使用
     */
    fun digestFromStream(
        inputStream: InputStream,
        hKey: ByteArray,
        @AlgorithmForHmac algorithm: String
    ): ByteArray {
        val key: SecretKey = SecretKeySpec(hKey, algorithm)
        val mac: Mac = Mac.getInstance(algorithm)
        mac.init(key)
        val tempByteArray = ByteArray(1024 * 4)
        var readSize: Int
        inputStream.use {
            while (true) {
                readSize = it.read(tempByteArray)
                if (readSize != -1) {
                    mac.update(tempByteArray, 0, readSize)
                } else break
            }
        }
        return mac.doFinal()
    }

    /**
     * 生成KEY
     */
    fun generalKey(@AlgorithmForHmac algorithm: String): ByteArray {
        val keyGen: KeyGenerator = KeyGenerator.getInstance(algorithm)
        val key: SecretKey = keyGen.generateKey()
        return key.encoded
    }

}