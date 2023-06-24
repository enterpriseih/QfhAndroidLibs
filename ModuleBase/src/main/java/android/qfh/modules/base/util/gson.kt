package android.qfh.modules.base.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonNull

private inline fun <reified D> JsonElement.convertPrimitive(): D? {
    if (isJsonNull || !isJsonPrimitive) {
        return null
    }
    return when {
        "" is D -> asString
        0 is D -> asInt
        0f is D -> asFloat
        0L is D -> asLong
        else -> asString
    } as D
}

/**
 * 根据 key 获取对应字符串类型的值
 * @suppress 如果 key 不存在或者类型不正确或者值为 [JsonNull] 将返回 null
 */
fun JsonObject.getString(key: String): String? {
    val element = get(key) ?: return null
    return element.convertPrimitive()
}
/**
 * 根据 key 获取对应 Int 类型的值
 * @suppress 如果 key 不存在或者类型不正确或者值为 [JsonNull] 将返回 null
 */
fun JsonObject.getInt(key: String): Int? {
    val element = get(key) ?: return null
    return element.convertPrimitive()
}