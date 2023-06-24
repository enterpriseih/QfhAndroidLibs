package android.qfh.libs.utils.encrypt

import android.util.Base64

/**
 * 字节编解码
 */
object ByteEncode {
    /**
     * 字节数组编码为 Base64 字符串
     */
    fun byteArrayToBase64Str(byteArray: ByteArray): String =
        Base64.encodeToString(byteArray, Base64.NO_WRAP)

    /**
     * Base64 字符串解码为字节数组
     */
    fun base64StrToByteArray(str: String): ByteArray = Base64.decode(str, Base64.NO_WRAP)

}