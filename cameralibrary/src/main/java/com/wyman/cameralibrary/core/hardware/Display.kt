package com.wyman.cameralibrary.core.hardware

import android.content.Context
import android.view.Surface
import android.view.WindowManager
import com.wyman.cameralibrary.core.orientation.Orientation
import com.wyman.cameralibrary.core.orientation.Orientation.*

/**
 * 获取设备角度
 * */
internal open class Display (context : Context){

    private val display = context.getDisplay()

    open fun getOrientation() : Orientation = when(display.rotation){
        Surface.ROTATION_0 -> Vertical.Portrait
        Surface.ROTATION_90 -> Horizontal.Landscape
        Surface.ROTATION_180 -> Vertical.Portrait
        Surface.ROTATION_270 -> Horizontal.ReverseLanscape
        else -> Vertical.Portrait
    }
}

private fun Context.getDisplay() = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay