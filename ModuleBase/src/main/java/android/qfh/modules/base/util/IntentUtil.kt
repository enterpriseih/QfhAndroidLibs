package android.qfh.modules.base.util

import android.content.Intent
import androidx.fragment.app.FragmentActivity

inline fun <reified A> FragmentActivity.jumpActivity(intentApply: Intent.() -> Unit = {}) {
    startActivity(Intent(this, A::class.java).apply(intentApply))
}