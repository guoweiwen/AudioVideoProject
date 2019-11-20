package com.wyman.utillibrary

import android.os.Build

/**
 * Created by wyman
 * on 2019-09-13.
 */

//当前版本是否大于5.0版本
fun isAboveLollipops() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP