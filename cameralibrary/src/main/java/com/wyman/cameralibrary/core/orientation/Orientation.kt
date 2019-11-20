package com.wyman.cameralibrary.core.orientation

typealias DeviceOrientaion = Orientation
typealias ScreenOrientaion = Orientation

/**
 * 记录屏幕方向枚举类
 * */
sealed class Orientation (
        val degress : Int
){
    sealed class Vertical(degress: Int) : Orientation(degress){
        //屏幕正向垂直
        object Portrait : Vertical(0)

        //屏幕垂直但反转
        object ReversePortrait : Vertical(180)
    }

    sealed class Horizontal(degress: Int) : Orientation(degress){
        //屏幕横屏
        object Landscape : Horizontal(90)

        //横屏反转
        object ReverseLanscape : Horizontal(270)
    }
}

//Int   内置函数
internal fun Int.toOrientation() : Orientation{
    return when (this) {
        0,360 -> Orientation.Vertical.Portrait
        90 -> Orientation.Vertical.ReversePortrait
        180 -> Orientation.Horizontal.Landscape
        270 -> Orientation.Horizontal.ReverseLanscape
        else -> throw IllegalAccessException("Cannot convert $this to absolute Orientation.")
    }
}