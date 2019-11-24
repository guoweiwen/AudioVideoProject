package com.wyman.cameralibrary.core.selector

import com.wyman.cameralibrary.core.hardware.FpsRange
import com.wyman.utillibrary.selector.filtered
import com.wyman.utillibrary.selector.firstAvailable

//别名: FpsRangeSelector 函数类型：() -> FpsRange?
typealias FpsRangeSelector = Iterable<FpsRange>.() -> FpsRange?

/**
 * 返回该 选择函数，该选择函数的 选择的FPS值是在该FPS范围最大值
 * 不固定速率比固定速率好
 * */
fun highestFps() : FpsRangeSelector = firstAvailable(
        highestNonFixedFps(),
        highestFixedFps()
)


/**
 * 返回该选择函数是 最大的FPS值并且不是固定 rate
 * */
fun highestNonFixedFps() : FpsRangeSelector = filtered(
    selector = highestRangeFps(),
        //(T) -> Boolean
    predicate = {!it.isFixed }//{!it.isFixed} 等同与 (T)->Boolean it->!it.isFixed;T为it,!it.isFixed为返回值Boolean
)

fun highestFixedFps() : FpsRangeSelector = filtered(
    selector = highestRangeFps(),
    predicate = {it.isFixed}
)

private fun highestRangeFps() : FpsRangeSelector = {
    maxWith(CompareFpsRangeByBounds)
}

/**
 * 查找较小的那个
 * */
internal object CompareFpsRangeByBounds : Comparator<FpsRange>{

    override fun compare(o1: FpsRange, o2: FpsRange): Int {
        return o1.min.compareTo(o2.min).run{
            when(this){
                0 -> o1.max.compareTo(o2.max)
                else -> this
            }
        }
    }
}



