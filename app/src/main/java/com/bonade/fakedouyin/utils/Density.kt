package com.bonade.fakedouyin.utils

import com.bonade.fakedouyin.MyApplication

/**
 * Created by wyman
 * on 2019-07-07.
 * dp 与 px 转换工具类
 * 下面都是顶层函数，整个 module 都可以用
 */
fun dp2px(dp : Int) : Float{
    val dm = MyApplication.instance.resources.displayMetrics
    return dm.density * dp + 0.5f
}

fun dp2px(dp : Float) : Float{
    val dm = MyApplication.instance.resources.displayMetrics
    return dm.density * dp + 0.5f
}

fun px2dp(px : Int) : Float{
    val dm = MyApplication.instance.resources.displayMetrics
    return px *1.0f / dm.density + 0.5f
}

/**
 * 获取设备宽高
 * */
fun getScreenResolution() : Array<Int>{
    val heightPixel = MyApplication.instance.resources.displayMetrics.heightPixels
    val widthPixel = MyApplication.instance.resources.displayMetrics.widthPixels
    return arrayOf(widthPixel,heightPixel)
}















