package android.qfh.libs.utils

import kotlin.math.acos
import kotlin.math.pow

object MathUtil {
    /**
     * 根据A、B、C三点的坐标求点A的角度
     * 余弦定理：CosA=（AB平方+AC平方-BC平方）/（2*AB*AC）
     * @suppress 角度值在0到180之间
     */
    fun getADegreeByPoints(
        pointAX: Float, pointAY: Float,
        pointBX: Float, pointBY: Float,
        pointCX: Float, pointCY: Float
    ): Float {
        val a2 = (pointBX - pointCX).toDouble().pow(2.0) + (pointBY - pointCY).toDouble().pow(2.0)
        val b2 = (pointAX - pointCX).toDouble().pow(2.0) + (pointAY - pointCY).toDouble().pow(2.0)
        val c2 = (pointAX - pointBX).toDouble().pow(2.0) + (pointAY - pointBY).toDouble().pow(2.0)
        val cosA = (b2 + c2 - a2) / (2 * c2.pow(1.0 / 2) * b2.pow(1.0 / 2))
        return Math.toDegrees(acos(cosA)).toFloat()
    }

    // 勾股定理，根据两个点获取长度
    fun getLengthByTwoPoints(startX: Float, startY: Float, endX: Float, endY: Float): Float {
        val d2 = (startX - endX).toDouble().pow(2.0) + (startY - endY).toDouble().pow(2.0)
        return d2.pow(1.0 / 2).toFloat()
    }
}
