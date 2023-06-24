package android.qfh.libs.utils.encrypt

import androidx.annotation.StringDef
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature

/**
 * 数据签名及校验
 */
object DataSign {

    const val Algorithm_MD5withRSA = "MD5withRSA"
    const val Algorithm_SHA1withRSA = "SHA1withRSA"
    const val Algorithm_SHA256withRSA = "SHA256withRSA"
    const val Algorithm_SHA512withRSA = "SHA512withRSA"

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(
        Algorithm_MD5withRSA,
        Algorithm_SHA1withRSA,
        Algorithm_SHA256withRSA,
        Algorithm_SHA512withRSA
    )
    annotation class Algorithm

    /**
     * 利用私钥对数据生成签名密文
     * @param data 需要签名的数据
     * @param key 签名使用的私钥
     * @param algorithm 算法
     * @return 生成的签名数据
     */
    fun signMethod(data: ByteArray, key: PrivateKey, @Algorithm algorithm: String): ByteArray {
        val signature = Signature.getInstance(algorithm)
        //使用私钥
        signature.initSign(key)
        //需要签名或校验的数据
        signature.update(data)
        //进行数字签名
        return signature.sign()
    }

    /**
     * 利用公钥、数据签名、数据进行验证数据是否合法
     * @param date 签名作用的数据
     * @param sign 生成的签名
     * @param key 验签的公钥
     * @param algorithm 算法
     * @return 数据和签名是否对应
     */
    fun verifyMethod(
        date: ByteArray,
        sign: ByteArray,
        key: PublicKey,
        @Algorithm algorithm: String
    ): Boolean {
        val signature = Signature.getInstance(algorithm)
        signature.initVerify(key)
        signature.update(date)
        return signature.verify(sign)
    }
}