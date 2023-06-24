package android.qfh.modules.base.util.updateApk.default

import android.qfh.modules.base.util.updateApk.RemoteConfigInterface
import android.qfh.modules.base.util.updateApk.UpdateConfigParser
import com.google.gson.Gson
import java.io.File
import java.io.FileReader

class JsonUpdateConfigParserImpl : UpdateConfigParser {
    override fun parserConfigFile(file: File): RemoteConfigInterface {
        val gson = Gson()
        return gson.fromJson(FileReader(file), DefaultRemoteConfigImpl::class.java)
    }
}