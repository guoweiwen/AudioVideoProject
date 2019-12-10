package com.wyman.cameralibrary.preview

import android.content.Context
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import com.wyman.cameralibrary.core.hardware.Resolution
import java.lang.IllegalArgumentException
import java.util.concurrent.CountDownLatch

/**
 * 该 View 可以放在 主Activity那里
 *
 * 1:先传递 glSurfaceView
 * 2：再设置 surfaceTexture
 * 3：打开摄像头
 * */
open class GLSurfaceViewToCamera <T : GLSurfaceView>@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), CameraRenderer{

    //将 caincamera 的 GLRecordView 创建对象到这里
    private var glSurfaceView : T? = null
        set(value) {
            addView(value)
        }


    private var surfaceTexture: SurfaceTexture? = null

    private lateinit var scaleType: ScaleType
    private lateinit var previewResolution: Resolution

    override fun setScaleType(scaleType: ScaleType) {
        this.scaleType = scaleType
    }

    override fun setPreviewResolution(resolution: Resolution) {
        post{
            previewResolution = resolution
            requestLayout()
        }
    }

    /**
     * 设置 SurfaceTexture
     * */
    open fun setSurfaceTexture(surfaceTexture : SurfaceTexture) {
        this.surfaceTexture = surfaceTexture
    }

    override fun getPreview(): Preview {
        return surfaceTexture?.toPreview() ?: throw IllegalArgumentException()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (isInEditMode || !::previewResolution.isInitialized || !::scaleType.isInitialized) {
            super.onLayout(changed, left, top, right, bottom)
        } else {
            layoutSurfaceView(previewResolution, scaleType)
        }
    }

}

private fun ViewGroup.layoutSurfaceView(
        previewResolution: Resolution?,
        scaleType: ScaleType?
) = when (scaleType) {
    ScaleType.CenterInside -> previewResolution?.centerInside(this)
    ScaleType.CenterCrop -> previewResolution?.centerCrop(this)
    else -> null
}

private fun Resolution.centerInside(view: ViewGroup) {
    val scale = Math.min(
            view.measuredWidth / width.toFloat(),
            view.measuredHeight / height.toFloat()
    )

    val width = (width * scale).toInt()
    val height = (height * scale).toInt()

    val extraX = Math.max(0, view.measuredWidth - width)
    val extraY = Math.max(0, view.measuredHeight - height)

    val rect = Rect(
            extraX / 2,
            extraY / 2,
            width + extraX / 2,
            height + extraY / 2
    )

    view.layoutChildrenAt(rect)
}

private fun Resolution.centerCrop(view: ViewGroup) {
    val scale = Math.max(
            view.measuredWidth / width.toFloat(),
            view.measuredHeight / height.toFloat()
    )

    val width = (width * scale).toInt()
    val height = (height * scale).toInt()

    val extraX = Math.max(0, width - view.measuredWidth)
    val extraY = Math.max(0, height - view.measuredHeight)

    val rect = Rect(
            -extraX / 2,
            -extraY / 2,
            width - extraX / 2,
            height - extraY / 2
    )

    view.layoutChildrenAt(rect)
}

private fun ViewGroup.layoutChildrenAt(rect: Rect) {
    (0 until childCount).forEach {
        getChildAt(it).layout(
                rect.left,
                rect.top,
                rect.right,
                rect.bottom
        )
    }
}









