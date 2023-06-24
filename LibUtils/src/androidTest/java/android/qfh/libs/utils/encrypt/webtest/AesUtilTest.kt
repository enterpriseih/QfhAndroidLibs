package android.qfh.libs.utils.encrypt.webtest

import android.qfh.libs.utils.encrypt.AesUtil
import android.qfh.libs.utils.encrypt.AesUtil.MODE_DECRYPT
import android.qfh.libs.utils.encrypt.AesUtil.MODE_ENCRYPT
import android.qfh.libs.utils.encrypt.ByteEncode
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AesUtilTest {
    @Test
    fun testStr() {
        val aes_key = "1234567812345678"
        val test_str = listOf("嘎嘎嘎嘎嘎112233", "dsfdsdf1212312223ewq")

        for (testItem in test_str) {
            val encodeValue = ByteEncode.byteArrayToBase64Str(
                AesUtil.convertByteArray(
                    testItem.toByteArray(),
                    aes_key.toByteArray(),
                    MODE_ENCRYPT,
                    AesUtil.TRANSFORMATION_AES_ECB
                )
            )
            val decodeValue = String(
                AesUtil.convertByteArray(
                    ByteEncode.base64StrToByteArray(encodeValue),
                    aes_key.toByteArray(),
                    MODE_DECRYPT,
                    AesUtil.TRANSFORMATION_AES_ECB
                )
            )
            println("加密数据:${testItem}\n,加密后:${encodeValue}\n,解密后:${decodeValue}\n,是否相等:${encodeValue == decodeValue}\n")
            Assert.assertEquals(testItem,decodeValue)
        }

    }
}