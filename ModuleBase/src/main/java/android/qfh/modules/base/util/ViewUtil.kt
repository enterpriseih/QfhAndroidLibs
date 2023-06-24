package android.qfh.modules.base.util

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.invert() {
    if (isVisible) {
        gone()
    } else {
        visible()
    }
}

/**
 * 获取指定主主题属性的对应值
 * @suppress 限定主题属性为颜色值
 */
fun Context.getResourceColorByAttr(attrId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrId, typedValue, true)
    @Suppress("DEPRECATION")
    return resources.getColor(typedValue.resourceId)
}
