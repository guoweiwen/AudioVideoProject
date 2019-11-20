package com.wyman.cameralibrary.core.orientation

typealias DeviceRotationDegrees = Int

/**
 * 寻找最接近的角度
 * 用于确定是横竖屏正或反
 * */
internal fun Int.toCloseestRightAngle() : Int{
    //this 为 Int
    val roundUp = this % 90 > 45 //返回 取模得 ：0-89

    val roundAppModifier = if(roundUp) 1 else 0

    return  (this / 90 + roundAppModifier) * 90 % 360
}