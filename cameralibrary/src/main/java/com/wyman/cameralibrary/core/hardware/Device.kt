package com.wyman.cameralibrary.core.hardware

import com.wyman.cameralibrary.core.orientation.Orientation

internal open class Device (
        private val display : Display
) {

    open fun getScreenOrientation() : Orientation{
        return display.getOrientation()
    }
}