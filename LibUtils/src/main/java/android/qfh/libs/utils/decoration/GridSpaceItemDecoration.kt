package android.qfh.libs.utils.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 竖直方向上为 [GridLayoutManager] 设置 item 之间的间隔
 * @suppress 如果要设置最后一行 item 距离底部的间距，则需要设置 [lastRowSpacing] 和 [dataList]
 */
class GridSpaceItemDecoration(
    //横向条目数量
    private val mSpanCount: Int,
    //上下行间距
    private val mRowSpacing: Int,
    //左右列间距
    private val mColumnSpacing: Int
) : RecyclerView.ItemDecoration() {
    // 最后一行距离底部的间距
    var lastRowSpacing = 0

    // 数据集合
    var dataList: List<*>? = null

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // 获取view 在adapter中的位置。
        val column = position % mSpanCount // view 所在的列
        outRect.left = column * mColumnSpacing / mSpanCount // column * (列间距 * (1f / 列数))
        outRect.right =
            mColumnSpacing - (column + 1) * mColumnSpacing / mSpanCount // 列间距 - (column + 1) * (列间距 * (1f /列数))

        // 如果position > 行数，说明不是在第一行，则不指定行高，其他行的上间距为 top=mRowSpacing
        if (position >= mSpanCount) {
            outRect.top = mRowSpacing // item top
        }
        // 处理最后一行距离底部的间距
        outRect.bottom = 0
        if (lastRowSpacing != 0) {
            dataList?.let {
                if (position >= it.size - it.size%mSpanCount) {
                    outRect.bottom = lastRowSpacing
                }
            }
        }
    }
}