package com.wyman.cameralibrary.preview

import android.graphics.SurfaceTexture
import android.view.SurfaceHolder

sealed class Preview {


    data class Texture(
            val surfaceTexture: SurfaceTexture
    ) : Preview()

    data class Surface(
            val surfaceHolder: SurfaceHolder
    ) : Preview()

    //internal 只有该module 可用
    // SurfaceTexture.toPreview() 为给 SurfaceTexture 内置函数
    internal fun SurfaceTexture.toPreview() = Texture(surfaceTexture = this)

    internal fun SurfaceHolder.toPreview() = Surface(surfaceHolder = this)
}