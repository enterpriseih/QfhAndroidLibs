package android.qfh.libs.utils

import android.graphics.Matrix
import android.view.View

object MatrixUtil {
    // 水平镜像翻转
    fun horizontalMirrorFlip(matrix: Matrix,view: View) {
        matrix.postScale(-1f, 1f,view.width/2f,view.height/2f)
    }

    // 竖直镜像翻转
    fun verticalMirrorFlip(matrix: Matrix,view: View) {
        matrix.postScale(1f, -1f,view.width/2f,view.height/2f)
    }

}