package android.qfh.modules.base.util.updateApk.default

import android.qfh.modules.base.util.updateApk.UpdateConfigParser
import android.qfh.modules.base.util.updateApk.UpdateInterfaceFactory

class DefaultUpdateInterfaceFactory : UpdateInterfaceFactory {
    override fun createConfigParser(): UpdateConfigParser {
        return JsonUpdateConfigParserImpl()
    }
}