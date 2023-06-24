package android.qfh.libs.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

object ViewUtil {

    //根据文字中心y值获取基线的坐标
    fun getBaseLineByCenterY(paint: Paint, centerY: Float): Float {
        return centerY + (paint.fontMetrics.bottom - paint.fontMetrics.top) / 2 - paint.fontMetrics.bottom
    }

    fun dp2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale
    }

    fun px2dp(context: Context, pxValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return pxValue / scale
    }

    // 获取应用内某个指定 view 当前显示的图像内容（类似于截屏）
    fun getViewBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas().apply {
            setBitmap(bitmap)
        }
        view.draw(canvas)
        return bitmap
    }

}
