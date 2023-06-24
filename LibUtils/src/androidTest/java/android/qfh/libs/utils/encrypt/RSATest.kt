package android.qfh.libs.utils.encrypt

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
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
class RSATest {
    val oriData = "123456asdffg!@#$%^&*我的你好他的"
    val pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZVpTCAe/YuYnh/eKnnAKodbOpVqBX6PbNwwX0yHQcyjJuweobLE+IC9etpWTt2AsCOHyxjLu7q68YHk/W7+DNb3OjZ9WiNCoHcsqS2x/DN5EO6Bw1A2VV7h8tmQTmn+84/EzP2S8Hogks2E7uVQgF9GetBrxs0SCeN5tnYqcbBwIDAQAB"
    val priKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANlWlMIB79i5ieH94qecAqh1s6lWoFfo9s3DBfTIdBzKMm7B6hssT4gL162lZO3YCwI4fLGMu7urrxgeT9bv4M1vc6Nn1aI0KgdyypLbH8M3kQ7oHDUDZVXuHy2ZBOaf7zj8TM/ZLweiCSzYTu5VCAX0Z60GvGzRIJ43m2dipxsHAgMBAAECgYAJ0qHyisfeUbRrpF/kF2b/WiJ+ms2wa3UMI65XO+ATlQfmfZkpFT2a9n4O+RSwszz8IrVwCN5LQx0sx+Hv4vOUAAWYmUwdRyJ0czKFADmsYzO/BoULVtOP448dEt/DzPA8Go+opSLFMlHCZvz+nWwYWg3y7mIsna01ncFh+8ntwQJBAPigcIfvOGMVOYAHbY0vxNFPCN5kwj7oveagZhGQZH7tl9abWgmmgCJvq+O/wuKbl3OdxFfsLAXzszSoFyIfd9ECQQDfyJnR1km6+pD1yjc3lKw3kz8OuTBm5MOqPTZUvFuxrxZGYjAUue/7dHM8LIDNSNfCcrRHWZZez0qEgZjAGvNXAkEAj2irwBziH3Tcp9ahADEvi7PU38KqsHK7Js4aUcNB1FzjmiwdsTdS7YoQUdwIY4FvyIaV/EkD06MQGS1jupEAIQJBANyrYksSRRhVFvbYlcSAHaab03WAVeNQuEU6IUZDiyPWpF95WTJG/Ad4mF2BAO3tY51CINWu2OHbBP9PEEJ3EPcCQBsBeGTY7wcEEIqJnXP5q6W7dBb1/dP7ieJNWFk5Jd71bBLJw56dXDVEp5/3cu4vT6seZb7qNbiYunVjpEjRwPU="
    val dstData1 = "ewGtY7k9G5oaIgB2SrYuRpRtDcYQYx6Dud/X9YT+3qyNbJkEHhwOLZU9Ci8WY8k+gI/82yVBlQqXnMtdg3vduFFUGR4yC0JWcjHQjp4sO12F9U3TVpScvHX7zbxpK23HBdaClx6OtgdoEG/tuh5uBxH0LS0nPKNPSypeHCmBHzo="
    val dstData2 = "JTV26Gh/BKnkDz7oYNeS8srCpTlVQCYAFVtxUfkMGqk+mjxDA8L9UlUYS6fXnm0Lt4ZRv8pDCJHDplt47ZJADMhVlqFBZxDczLG/fECYVZ9KR+6HZnev4ukOvX4j6ZT3V9753jDkrEvFjAYTP9SKAXMlZdNpcD7nbIFI+dfhXlc="

    @Test
    fun testKey() {
        val generateKeyPair = RSAUtil.generateKeyPair()
        val keyPub = generateKeyPair[RSAUtil.PUBLIC_KEY].run { ByteEncode.byteArrayToBase64Str(this!!) }
        val keyPri = generateKeyPair[RSAUtil.PRIVATE_KEY].run { ByteEncode.byteArrayToBase64Str(this!!) }

        assertNotEquals(keyPub, pubKey)
        assertNotEquals(keyPri, priKey)
    }

    @Test
    fun testStr() {
        val keyPub = RSAUtil.byteToPublicKey(ByteEncode.base64StrToByteArray(pubKey))
        val keyPri = RSAUtil.byteToPrivateKey(ByteEncode.base64StrToByteArray(priKey))
        val generalData1 = RSAUtil.encrypt(oriData.toByteArray(StandardCharsets.UTF_8),keyPub).run { ByteEncode.byteArrayToBase64Str(
            this
        ) }
        //公钥加密对同一数据每次生成的密文都不一样
        assertNotEquals(dstData1,generalData1)
        val data1 = RSAUtil.decrypt(ByteEncode.base64StrToByteArray(generalData1),keyPri).run { String(this,StandardCharsets.UTF_8) }
        Assert.assertEquals(oriData,data1)

        val generalData2 = RSAUtil.encrypt(oriData.toByteArray(StandardCharsets.UTF_8),keyPri).run { ByteEncode.byteArrayToBase64Str(
            this
        ) }
        Assert.assertEquals(dstData2,generalData2)
        val data2 = RSAUtil.decrypt(ByteEncode.base64StrToByteArray(generalData2),keyPub).run { String(this,StandardCharsets.UTF_8) }
        Assert.assertEquals(oriData,data2)
    }
}