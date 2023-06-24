package android.qfh.libs.utils

// 正则表达式匹配规则
object RegularRules {
    // ip加端口号匹配
    val ipMatcher by lazy {
        """^(\d{1,3}\.){3}\d{1,3}:\d{1,5}$""".toRegex()
    }
}