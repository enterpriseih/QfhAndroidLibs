package android.qfh.modules.base.util.updateApk

import java.io.File

/**
 * 文件序列化接口，定义了如果将配置文件解析为对应的 bean 数据
 */
interface UpdateConfigParser {
    fun parserConfigFile(file: File): RemoteConfigInterface
}
