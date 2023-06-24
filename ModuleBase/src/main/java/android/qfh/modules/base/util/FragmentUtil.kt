package android.qfh.modules.base.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2

/**
 * viewPager2 获取当前正在显示的 fragment 实例
 */
fun ViewPager2.findCurrentFragmentForActivity(fragmentActivity: FragmentActivity): Fragment {
    return fragmentActivity.supportFragmentManager.findFragmentByTag("f$currentItem")!!
}