@file:Suppress("unused")

package android.qfh.libs.utils.encrypt

import androidx.annotation.IntDef
import androidx.annotation.StringDef
import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * aes 对称加密
 */
object AesUtil {

    // cbc 模式 需要指定 ivKey 参数
    const val TRANSFORMATION_AES_CBC = "AES/CBC/PKCS5Padding"

    //简单模式，不需要指定ivKey参数
    const val TRANSFORMATION_AES_ECB = "AES/ECB/PKCS5Padding"


    const val MODE_ENCRYPT = Cipher.ENCRYPT_MODE
    const val MODE_DECRYPT = Cipher.DECRYPT_MODE

    @IntDef(MODE_ENCRYPT, MODE_DECRYPT)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Mode

    @StringDef(TRANSFORMATION_AES_CBC, TRANSFORMATION_AES_ECB)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Transformation

    /**
     * 随机生成 AES 密钥
     * @param keySize 密钥长度，可选长度为128,192,256位.
     */
    fun generateAesKey(
        keySize: Int,
    ): ByteArray {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(keySize)
        val secretKey = keyGenerator.generateKey()
        return secretKey.encoded
    }

    /**
     * 对字节数组进行对称加解密
     * @param data 进行计算的数据
     * @param key 对称密钥
     * @param mode 模式，指明加密还是解密
     * @param transformation 算法参数
     * @param ivKey 额外参数，[transformation]为部分值时需要传入
     * @return 生成的摘要字节数组
     */
    fun convertByteArray(
        data: ByteArray,
        key: ByteArray,
        @Mode mode: Int,
        @Transformation transformation: String,
        ivKey: ByteArray? = null,
    ): ByteArray {
        val cipher: Cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(key, "AES")
        val ivParameterSpec = ivKey?.let { IvParameterSpec(it) }
        if (ivParameterSpec == null) {
            cipher.init(mode, keySpec)
        } else {
            cipher.init(mode, keySpec, ivParameterSpec)
        }
        return cipher.doFinal(data)
    }

    /**
     * 对数据流进行对称加解密
     * @param inputStream 进行计算的数据流
     * @param key 对称密钥
     * @param mode 模式，指明加密还是解密
     * @param transformation 算法参数
     * @param ivKey 额外参数，[transformation]为部分值时需要传入
     * @return 生成的摘要字节数组
     */
    fun convertInputStream(
        inputStream: InputStream,
        key: ByteArray,
        @Mode mode: Int,
        @Transformation transformation: String,
        ivKey: ByteArray?,
    ): ByteArray {
        val cipher: Cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(key, "AES")
        val ivParameterSpec = ivKey?.let { IvParameterSpec(it) }
        if (ivParameterSpec == null) {
            cipher.init(mode, keySpec)
        } else {
            cipher.init(mode, keySpec, ivParameterSpec)
        }
        val tempByteArray = ByteArray(1024 * 4)
        var readSize: Int
        inputStream.use {
            while (true) {
                readSize = it.read(tempByteArray)
                if (readSize != -1) {
                    cipher.update(tempByteArray, 0, readSize)
                } else break
            }
        }
        return cipher.doFinal()
    }
}