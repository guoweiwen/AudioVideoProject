package com.wyman.cameralibrary.core.selector

import com.wyman.cameralibrary.core.hardware.Resolution
import java.util.*

typealias ResolutionSelector = Iterable<Resolution>.() -> Resolution?

/**
 * 返回最大分辨率
 * */
fun highestResolution() : Iterable<Resolution>.() -> Resolution? = {
    maxBy(Resolution::area)
}

fun lowestResolution() : ResolutionSelector = { minBy(Resolution::area) }