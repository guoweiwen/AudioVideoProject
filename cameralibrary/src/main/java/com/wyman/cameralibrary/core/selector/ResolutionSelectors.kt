package com.wyman.cameralibrary.core.selector

import com.wyman.cameralibrary.core.hardware.Resolution

typealias ResolutionSelector = Iterable<Resolution>.() -> Resolution?

/**
 * 返回最大分辨率
 *
 * 1、不明白：方法名冒号后 返回值 Iterable<Resolution>.() -> Resolution?  是返回什么？
 * 2、不明白：返回值紧跟 ={} 与  {}  或 = 的区别？
 *      ={} 为字面量即 函数类型实例
 * */
fun highestResolution() : Iterable<Resolution>.() -> Resolution? = {
    //里面的 this 就是 Iterable 接收者
    maxBy(Resolution::area)
}
/**
 * 如果写成这样：
 *      val hightestResolution : ResolutionSelector = { maxBy(Resolution::area) }
 *
 *      那么返回值类型就是  Resolution 非 ResolutionSelector 即：Iterable<Resolution>.() -> Resolution?
 *      val pictureResolution : ResolutionSelector = highestResolution(),
 * */

fun lowestResolution() : ResolutionSelector = { minBy(Resolution::area) }