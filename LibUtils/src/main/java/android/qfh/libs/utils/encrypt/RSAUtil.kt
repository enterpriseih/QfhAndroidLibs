package android.qfh.libs.utils.encrypt

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

/**
 * 非对称加密算法RSA
 */
object RSAUtil {
    /**
     * android 的 rsa 机制和 php ，java 的默认机制有点不同
     * android 系统的 RSA 实现是 "RSA/None/NoPadding" ，而标准 JDK 实现是 "RSA/ECB/PKCS1Padding"
     * 具体解决方法http://stackoverflow.com/questions/13556295/rsa-encryption-in-android
     * 稍微作修改一下这里：Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
     * 指定加密算法为 RSA/ECB/PKCS1Padding
     *
     */
    //指定填充模式
    private const val ALGORITHM = "RSA/ECB/PKCS1Padding"

    /**
     * 密钥长度，用来初始化
     */
    private const val KEY_SIZE = 1024

    /**
     * 获取公钥时对应 key 值
     */
    const val PUBLIC_KEY = "PublicKey"

    /**
     * 获取私钥对应 key 值
     */
    const val PRIVATE_KEY = "PrivateKey"

    /**
     * 生成秘钥对
     */
    fun generateKeyPair(): Map<String, ByteArray> {
        val secureRandom = SecureRandom()
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(KEY_SIZE, secureRandom)
        val keyPair = keyPairGenerator.generateKeyPair()
        val publicKey: Key = keyPair.public
        val privateKey: Key = keyPair.private

        val hashMap = HashMap<String, ByteArray>()
        hashMap[PUBLIC_KEY] = publicKey.encoded
        hashMap[PRIVATE_KEY] = privateKey.encoded
        return hashMap
    }

    /**
     * @param content 加密内容
     * @param key     用于加密的公钥
     * @return 加密后的字节数组
     */
    fun encrypt(content: ByteArray, key: Key): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(content)
    }

    /**
     * @param textBytes  已被加密的内容
     * @param privateKey 用于解密的私钥
     * @return 解密后的内容
     */
    fun decrypt(textBytes: ByteArray, privateKey: Key): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(textBytes)
    }

    /**
     * 字节流转公钥
     */
    fun byteToPublicKey(key: ByteArray): PublicKey {
        val keySpec = X509EncodedKeySpec(key)
        val kf: KeyFactory = KeyFactory.getInstance("RSA")
        return kf.generatePublic(keySpec)
    }

    /**
     * 字节流转私钥
     */
    fun byteToPrivateKey(key: ByteArray): PrivateKey {
        val keySpec = PKCS8EncodedKeySpec(key)
        val kf: KeyFactory = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(keySpec)
    }

}