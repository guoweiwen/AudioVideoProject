package com.wyman.cameralibrary.preview

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.view.SurfaceHolder
import com.wyman.cameralibrary.preview.Preview.Surface
import com.wyman.cameralibrary.preview.Preview.Texture

sealed class Preview {


    data class Texture(
            val surfaceTexture: SurfaceTexture
    ) : Preview()

    data class Surface(
            //val surfaceHolder: SurfaceHolder
            val glsurface : GLSurfaceView
    ) : Preview()

}

//internal 只有该module 可用
// SurfaceTexture.toPreview() 为给 SurfaceTexture 内置函数
internal fun SurfaceTexture.toPreview() = Texture(surfaceTexture = this)

internal fun GLSurfaceView.toPreview() = Surface(glsurface = this)