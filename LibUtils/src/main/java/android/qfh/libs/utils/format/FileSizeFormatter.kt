package android.qfh.libs.utils.format

const val BASE = 1024L
const val KB = BASE
const val MB = KB * BASE
const val GB = MB * BASE
const val TB = GB * BASE

/**
 * 将字节数格式化为可读字符串，返回如 2.1 MB、4 G、3 KB 等等
 * @param byteSize 被格式化的字节数量
 * @param formatLength 保留的小数位数
 * @param nullValue [byteSize] 为空时返回的字符串
 */
fun formatByte(byteSize: Long?, formatLength: Int = 2, nullValue: String): String {
    if (byteSize == null) return nullValue
    return StringBuilder().apply {
        when {
            byteSize >= TB -> {
                append(String.format("%.${formatLength}f", byteSize.toFloat() / TB))
                append(" TB")
            }
            byteSize >= GB -> {
                append(String.format("%.${formatLength}f", byteSize.toFloat() / GB))
                append(" GB")
            }
            byteSize >= MB -> {
                append(String.format("%.${formatLength}f", byteSize.toFloat() / MB))
                append(" MB")
            }
            byteSize >= KB -> {
                append(String.format("%.${formatLength}f", byteSize.toFloat() / KB))
                append(" KB")
            }
            else -> {
                append(byteSize)
                append(" BYTE")
            }
        }
    }.toString()
}