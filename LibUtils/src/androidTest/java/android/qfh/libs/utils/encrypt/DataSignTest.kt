package android.qfh.libs.utils.encrypt

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
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
class DataSignTest {
    val oriData = "123456asdffg!@#$%^&*我的你好他的"
    val pubKey =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZVpTCAe/YuYnh/eKnnAKodbOpVqBX6PbNwwX0yHQcyjJuweobLE+IC9etpWTt2AsCOHyxjLu7q68YHk/W7+DNb3OjZ9WiNCoHcsqS2x/DN5EO6Bw1A2VV7h8tmQTmn+84/EzP2S8Hogks2E7uVQgF9GetBrxs0SCeN5tnYqcbBwIDAQAB"
    val priKey =
        "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANlWlMIB79i5ieH94qecAqh1s6lWoFfo9s3DBfTIdBzKMm7B6hssT4gL162lZO3YCwI4fLGMu7urrxgeT9bv4M1vc6Nn1aI0KgdyypLbH8M3kQ7oHDUDZVXuHy2ZBOaf7zj8TM/ZLweiCSzYTu5VCAX0Z60GvGzRIJ43m2dipxsHAgMBAAECgYAJ0qHyisfeUbRrpF/kF2b/WiJ+ms2wa3UMI65XO+ATlQfmfZkpFT2a9n4O+RSwszz8IrVwCN5LQx0sx+Hv4vOUAAWYmUwdRyJ0czKFADmsYzO/BoULVtOP448dEt/DzPA8Go+opSLFMlHCZvz+nWwYWg3y7mIsna01ncFh+8ntwQJBAPigcIfvOGMVOYAHbY0vxNFPCN5kwj7oveagZhGQZH7tl9abWgmmgCJvq+O/wuKbl3OdxFfsLAXzszSoFyIfd9ECQQDfyJnR1km6+pD1yjc3lKw3kz8OuTBm5MOqPTZUvFuxrxZGYjAUue/7dHM8LIDNSNfCcrRHWZZez0qEgZjAGvNXAkEAj2irwBziH3Tcp9ahADEvi7PU38KqsHK7Js4aUcNB1FzjmiwdsTdS7YoQUdwIY4FvyIaV/EkD06MQGS1jupEAIQJBANyrYksSRRhVFvbYlcSAHaab03WAVeNQuEU6IUZDiyPWpF95WTJG/Ad4mF2BAO3tY51CINWu2OHbBP9PEEJ3EPcCQBsBeGTY7wcEEIqJnXP5q6W7dBb1/dP7ieJNWFk5Jd71bBLJw56dXDVEp5/3cu4vT6seZb7qNbiYunVjpEjRwPU="

    @Test
    fun testStr() {

        val keyPub = RSAUtil.byteToPublicKey(ByteEncode.base64StrToByteArray(pubKey))
        val keyPri = RSAUtil.byteToPrivateKey(ByteEncode.base64StrToByteArray(priKey))

        listOf(
            DataSign.Algorithm_MD5withRSA,
            DataSign.Algorithm_SHA1withRSA,
            DataSign.Algorithm_SHA256withRSA,
            DataSign.Algorithm_SHA512withRSA
        ).forEach {
            val signData =
                DataSign.signMethod(oriData.toByteArray(StandardCharsets.UTF_8), keyPri, it)
            val verifyMethod = DataSign.verifyMethod(
                oriData.toByteArray(StandardCharsets.UTF_8),
                signData,
                keyPub,
                it
            )
            Assert.assertEquals(true,verifyMethod)
        }
    }
}