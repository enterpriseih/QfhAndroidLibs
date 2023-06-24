package android.qfh.libs.utils.encrypt

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.StandardCharsets

/**
 * 以 Android api 31 为基准测试
 * 1、每次调用 generalRandomByteArray 和 AesUtil.generateAesKey 方法生产的数据都是不一样的，例如在 Android 31 上生产的两次数据日志如下：
 * 第一次生成
 * ivParamBase64:I8TaGLSXSRtFZT5dCp0jWQ==
 * generalAesKeyBase64:YoZePsl9eWWabBDo8wU0AA==
 * 第二次生成
 * ivParamBase64:A9DoFm2Cu6JHKnMflVWDsQ==
 * generalAesKeyBase64:zpD5w4Hu72Zusth0DZiOJg==
 * 以第二次生产的随机数局为基准数据，生成对应的加密数据如下测试值所示
 * 继续测试加解密数据，在 api 31 下测试成功
 * 以下版本测试通过:
 * Android 30
 * Android 29
 * Android 28
 * Android 26
 * Android 24
 * Android 23
 */
@RunWith(AndroidJUnit4::class)
class AesUtilTest {
    val oriData = "123456asdffg!@#$%^&*我的你好他的"
    val keyAES = "zpD5w4Hu72Zusth0DZiOJg=="
    val keyIv = "A9DoFm2Cu6JHKnMflVWDsQ=="
    val dstData = "ca9tj/Ioh5awRKaKK+x5+sRE9oHxTghpRMLBFS3smrXqV03/WmgilqV+0YkIQvyg"
    val dstDataIV = "nHi98Z8sj6b7JDIkFQdxZ9MYkQgbFd9YW3U8GNnyiv5Z4omvjxePkp4Jiew7kgoR"

    @Test
    fun testStr() {
//        val ivParamBase64 = generalRandomByteArray().run { ByteEncode.byteArrayToBase64Str(this) }
//        val generalAesKeyBase64 =
//            AesUtil.generateAesKey(128)
//                .run { ByteEncode.byteArrayToBase64Str(this) }
//        Log.d("======","ivParamBase64:${ivParamBase64}")
//        Log.d("======","generalAesKeyBase64:${generalAesKeyBase64}")
        val iv = keyIv
        val aesKey = keyAES

        val data = AesUtil.convertByteArray(
            oriData.toByteArray(StandardCharsets.UTF_8),
            ByteEncode.base64StrToByteArray(aesKey),
            AesUtil.MODE_ENCRYPT,
            AesUtil.TRANSFORMATION_AES_ECB,
        ).run { ByteEncode.byteArrayToBase64Str(this) }
//        Log.d("======", "data:${data}")
        Assert.assertEquals(dstData, data)

        val dataIV = AesUtil.convertByteArray(
            oriData.toByteArray(StandardCharsets.UTF_8),
            ByteEncode.base64StrToByteArray(aesKey),
            AesUtil.MODE_ENCRYPT,
            AesUtil.TRANSFORMATION_AES_CBC,
            ByteEncode.base64StrToByteArray(iv)
        ).run { ByteEncode.byteArrayToBase64Str(this) }
//        Log.d("======", "dataIV:${dataIV}")/
        Assert.assertEquals(dataIV, dstDataIV)

        val oriData1 = AesUtil.convertByteArray(
            ByteEncode.base64StrToByteArray(data),
            ByteEncode.base64StrToByteArray(aesKey),
            AesUtil.MODE_DECRYPT,
            AesUtil.TRANSFORMATION_AES_ECB,
        ).run { String(this, StandardCharsets.UTF_8) }
        Assert.assertEquals(oriData1, oriData)

        val oriDataIv = AesUtil.convertByteArray(
            ByteEncode.base64StrToByteArray(dataIV),
            ByteEncode.base64StrToByteArray(aesKey),
            AesUtil.MODE_DECRYPT,
            AesUtil.TRANSFORMATION_AES_CBC,
            ByteEncode.base64StrToByteArray(iv)
        ).run { String(this, StandardCharsets.UTF_8) }
        Assert.assertEquals(oriDataIv, oriData)
    }
}