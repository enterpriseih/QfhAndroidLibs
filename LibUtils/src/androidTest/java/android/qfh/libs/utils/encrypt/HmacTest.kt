package android.qfh.libs.utils.encrypt

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.StandardCharsets

/**
 * 以Android 11 api30为基准测试
 * 以下版本测试通过:
 * Android 30
 * Android 29
 * Android 28
 * Android 26
 * Android 24
 * Android 23
 */
@RunWith(AndroidJUnit4::class)
class HmacTest {
    val oriData = "123456asdffg!@#$%^&*我的你好他的"
    val keyMD5 = "DX+ys2xPY2xhai9QFBz5SQ=="
    val keySHA1 = "znsUfdvbyRcvbKDTj3uy2L57GMs="
    val keySHA256 = "TZ0GboR9Hf4FSmKCaO0xhsycxOIjdwZCV8qrZ9sj2Xg="
    val keySHA512 = "NxnwbNPQE6MBoYuGgQ/0+DugKkroYQdgdVfj8BSLav3L5OPT36lhIiEwe5Gh7+7ZQkLC/cGSWuMSzt2wnVZ51A=="
    val dstDataForAndroidAPI30_MD5 = "THpquQ2Lv4z8AQUCiYP2HQ=="
    val dstDataForAndroidAPI30_SHA1 = "1CmeVxrukAxmXRdGH5uLHWYDFtY="
    val dstDataForAndroidAPI30_SHA256 = "0mB4GbMZ4gWZMBPJSq5LxTM0QMxl+PQOtKW237biO5s="
    val dstDataForAndroidAPI30_SH512 = "vECqvjryuG4NnMTAl+IL/0CVxIfHtexIsfW1TfFxoeUZiqPwBJLBGuHAQocTHh4xqLGmRN3Y+pOAIciJTl+28A=="
    @Test
    fun testKey(){
        val generalKeyMD5 = HmacUtil.generalKey(HmacUtil.ALGORITHM_HMAC_MD5)
            .run { ByteEncode.byteArrayToBase64Str(this) }
        val generalKeySHA1 = HmacUtil.generalKey(HmacUtil.ALGORITHM_HMAC_SHA1)
            .run { ByteEncode.byteArrayToBase64Str(this) }
        val generalKeySHA256 = HmacUtil.generalKey(HmacUtil.ALGORITHM_HMAC_SHA256)
            .run { ByteEncode.byteArrayToBase64Str(this) }
        val generalKey512 = HmacUtil.generalKey(HmacUtil.ALGORITHM_HMAC_SHA512)
            .run { ByteEncode.byteArrayToBase64Str(this) }

        assertNotEquals(keyMD5, generalKeyMD5)
        assertNotEquals(keySHA1, generalKeySHA1)
        assertNotEquals(keySHA256, generalKeySHA256)
        assertNotEquals(keySHA512, generalKey512)
    }
    @Test
    fun testStr() {
        val digestMD5 = digestToString(
            oriData,
            ByteEncode.base64StrToByteArray(keyMD5),
            HmacUtil.ALGORITHM_HMAC_MD5
        )
        val digestSHA1 = digestToString(
            oriData,
            ByteEncode.base64StrToByteArray(keySHA1),
            HmacUtil.ALGORITHM_HMAC_SHA1
        )
        val digestSHA256 = digestToString(
            oriData,
            ByteEncode.base64StrToByteArray(keySHA256),
            HmacUtil.ALGORITHM_HMAC_SHA256
        )
        val digestSHA512 = digestToString(
            oriData,
            ByteEncode.base64StrToByteArray(keySHA512),
            HmacUtil.ALGORITHM_HMAC_SHA512
        )
        assertEquals(digestMD5, dstDataForAndroidAPI30_MD5)
        assertEquals(digestSHA1, dstDataForAndroidAPI30_SHA1)
        assertEquals(digestSHA256, dstDataForAndroidAPI30_SHA256)
        assertEquals(digestSHA512, dstDataForAndroidAPI30_SH512)
    }
    fun digestToString(
        str: String,
        hKey: ByteArray,
        @HmacUtil.AlgorithmForHmac algorithm: String
    ): String = ByteEncode.byteArrayToBase64Str(
        HmacUtil.digestFromByteArray(
            str.toByteArray(StandardCharsets.UTF_8),
            hKey,
            algorithm
        )
    )
}