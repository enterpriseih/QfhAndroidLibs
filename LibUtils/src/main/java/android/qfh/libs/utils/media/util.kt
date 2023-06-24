package android.qfh.libs.utils.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * 一个 tag，内部给 media 和 document 操作相关使用的
 */
internal const val TAG_MEDIA_AND_DOCUMENT = "media_and_document"

/**
 * 获取当前界面的一个不可见的隐藏 fragment
 * @param activity 如果获取指定 activity 的 隐藏 fragment，此字段不能为空
 * @param fragment 如果获取指定 fragment 隐藏 fragment，此字段不能为空
 * @param tag 隐藏 fragment 的 tag
 */
inline fun <reified F : Fragment> getOperateHelperFragment(
    activity: FragmentActivity? = null,
    fragment: Fragment? = null,
    tag: String,
): F {
    val fragmentManager = (activity?.supportFragmentManager ?: fragment?.childFragmentManager)!!
    return (fragmentManager.findFragmentByTag(tag) ?: F::class.java.newInstance().apply {
        val beginTransaction = fragmentManager.beginTransaction()
        beginTransaction.add(this, tag)
        beginTransaction.commitNowAllowingStateLoss()
    }) as F
}