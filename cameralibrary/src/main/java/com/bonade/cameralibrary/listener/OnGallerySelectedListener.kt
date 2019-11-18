package com.bonade.cameralibrary.listener

import com.bonade.cameralibrary.model.GalleryType

/**
 * Created by wyman
 * on 2019-09-13.
 */
interface OnGallerySelectedListener {
    //图库点击监听器
    fun onGalleryClickListener(type : GalleryType)
}
