package com.wyman.cameralibrary.core.hardware

/**
 * 帧率（FPS）：摄像头预览显示的范围
 * The values are multiplied by 1000 and represented in integers.
 * 该值乘以 1000 并用整数表示
 * For example, if frame rate is 26.623 frames per second, the value is 26623.
 * 举例如果码率是 26.623 该值为 26623
 * */
data class FpsRange(
        val min : Int,
        val max : Int
) : ClosedRange<Int> by IntRange(
        //预览支持的最低帧率：26623
        start = min,
        //预览的最大帧率：30000
        endInclusive = max
) {
    /**
     *  如果返回 true ，当前范围是固定
     * */
    val isFixed by lazy {
        max == min
    }
}