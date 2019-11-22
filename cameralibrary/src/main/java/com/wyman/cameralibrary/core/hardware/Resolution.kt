package com.wyman.cameralibrary.core.hardware

import androidx.annotation.IntRange

/**
 * 分辨率：即宽高
 * */
data class Resolution(
        @[JvmField IntRange(from = 0L)] val width : Int,
        @[JvmField IntRange(from = 0L)] val height : Int
) {
    val area : Int by lazy { width * height}

    //宽高比
    val aspectRatio : Float by lazy {
        when {
            width == 0 -> Float.NaN
            height == 0 -> Float.NaN
            else -> width.toFloat() / height
        }
    }

    /**
     * 返回一个将 高宽 置换的 Resolution 对象
     * */
    fun flipDimensions() : Resolution = Resolution(height,width)
}