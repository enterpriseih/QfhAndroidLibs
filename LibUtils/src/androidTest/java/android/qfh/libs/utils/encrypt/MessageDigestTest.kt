package android.qfh.libs.utils.encrypt

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

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
class MessageDigestTest {
    val oriData = "123456asdffg!@#$%^&*我的你好他的"
    val dstDataForAndroidAPI30_MD5 = "qNxbtJn1k4106IZrxuKpgg=="
    val dstDataForAndroidAPI30_SHA1 = "JBLYN+u26xFjB6EJZv0/PKVOgNU="
    val dstDataForAndroidAPI30_SHA256 = "Y6JVqxwX3o38wjJ2M6LDvJLaUuqG5IVJS2xiYVVLy9Q="
    val dstDataForAndroidAPI30_SH512 =
        "snGii0S4Uuz5X/ET8Jb3/IKjp327vyEvXsBoEyo9C+REunMKfagji/sM7OPmE6+Z2Wsvy9ii73BUBUmAZI26qQ=="

    @Test
    fun testStr() {
        val digestMD5 = MessageDigestUtil.digestToString(oriData, MessageDigestUtil.ALGORITHM_MD5)
        val digestSHA1 = MessageDigestUtil.digestToString(oriData, MessageDigestUtil.ALGORITHM_SHA1)
        val digestSHA256 =
            MessageDigestUtil.digestToString(oriData, MessageDigestUtil.ALGORITHM_SHA256)
        val digestSHA512 =
            MessageDigestUtil.digestToString(oriData, MessageDigestUtil.ALGORITHM_SHA512)

        assertEquals(digestMD5, dstDataForAndroidAPI30_MD5)
        assertEquals(digestSHA1, dstDataForAndroidAPI30_SHA1)
        assertEquals(digestSHA256, dstDataForAndroidAPI30_SHA256)
        assertEquals(digestSHA512, dstDataForAndroidAPI30_SH512)
    }

    @Test
    fun testFile() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val fileMD5 = File(appContext.filesDir, "md5.txt")
        val fileSHA1 = File(appContext.filesDir, "sha1.txt")
        val fileSHA256 = File(appContext.filesDir, "sha256.txt")
        val fileSHA512 = File(appContext.filesDir, "sha512.txt")
        fileMD5.writeText(oriData)
        fileSHA1.writeText(oriData)
        fileSHA256.writeText(oriData)
        fileSHA512.writeText(oriData)

        val digestMD5 =
            MessageDigestUtil.digestToString(fileMD5.inputStream(), MessageDigestUtil.ALGORITHM_MD5)
        val digestSHA1 = MessageDigestUtil.digestToString(
            fileSHA1.inputStream(),
            MessageDigestUtil.ALGORITHM_SHA1
        )
        val digestSHA256 =
            MessageDigestUtil.digestToString(
                fileSHA256.inputStream(),
                MessageDigestUtil.ALGORITHM_SHA256
            )
        val digestSHA512 =
            MessageDigestUtil.digestToString(
                fileSHA512.inputStream(),
                MessageDigestUtil.ALGORITHM_SHA512
            )

        assertEquals(digestMD5, dstDataForAndroidAPI30_MD5)
        assertEquals(digestSHA1, dstDataForAndroidAPI30_SHA1)
        assertEquals(digestSHA256, dstDataForAndroidAPI30_SHA256)
        assertEquals(digestSHA512, dstDataForAndroidAPI30_SH512)
    }
}