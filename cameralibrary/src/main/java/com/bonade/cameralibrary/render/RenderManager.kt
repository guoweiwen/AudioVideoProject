package com.bonade.cameralibrary.render

import android.content.Context
import android.util.Log
import android.util.SparseArray
import android.view.MotionEvent
import com.badlogic.gdx.math.Vector3
import com.bonade.filterlibrary.glfilter.color.DynamicColor


import com.bonade.cameralibrary.core.camera.CameraParam
import com.bonade.cameralibrary.model.ScaleType
import com.bonade.filterlibrary.beauty.bean.IBeautify
import com.bonade.filterlibrary.glfilter.GLImageFilter
import com.bonade.filterlibrary.glfilter.GLImageOESInputFilter
import com.bonade.filterlibrary.glfilter.makeup.bean.DynamicMakeup
import com.bonade.filterlibrary.utils.OpenGLUtils
import com.bonade.filterlibrary.utils.TextureRotationUtils

import java.nio.FloatBuffer

/**
 * 渲染管理器
 */
class RenderManager private constructor() {

    // 滤镜列表
    private val mFilterArrays = SparseArray<GLImageFilter>()

    // 坐标缓冲
    private val mScaleType = ScaleType.CENTER_CROP
    private var mVertexBuffer: FloatBuffer? = null
    private var mTextureBuffer: FloatBuffer? = null
    // 用于显示裁剪的纹理顶点缓冲
    private var mDisplayVertexBuffer: FloatBuffer? = null
    private var mDisplayTextureBuffer: FloatBuffer? = null

    // 视图宽高
    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    // 输入图像大小
    private var mTextureWidth: Int = 0
    private var mTextureHeight: Int = 0

    // 相机参数
    private val mCameraParam: CameraParam
    // 上下文
    private var mContext: Context? = null

    private object RenderManagerHolder {
        var instance = RenderManager()
    }

    init {
        mCameraParam = CameraParam.INSTANCE
    }

    /**
     * 初始化
     */
    fun init(context: Context) {
        initBuffers()
        initFilters(context)
        mContext = context
    }

    /**
     * 释放资源
     */
    fun release() {
        releaseBuffers()
        releaseFilters()
        mContext = null
    }

    /**
     * 释放滤镜
     */
    private fun releaseFilters() {
        for (i in 0 until NumberIndex) {
            if (mFilterArrays.get(i) != null) {
                mFilterArrays.get(i).release()
            }
        }
        mFilterArrays.clear()
    }

    /**
     * 释放缓冲区
     */
    private fun releaseBuffers() {
        if (mVertexBuffer != null) {
            mVertexBuffer!!.clear()
            mVertexBuffer = null
        }
        if (mTextureBuffer != null) {
            mTextureBuffer!!.clear()
            mTextureBuffer = null
        }
        if (mDisplayVertexBuffer != null) {
            mDisplayVertexBuffer!!.clear()
            mDisplayVertexBuffer = null
        }
        if (mDisplayTextureBuffer != null) {
            mDisplayTextureBuffer!!.clear()
            mDisplayTextureBuffer = null
        }
    }

    /**
     * 初始化缓冲区
     */
    private fun initBuffers() {
        releaseBuffers()
        mDisplayVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices)
        mDisplayTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices)
        mVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices)
        mTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices)
    }

    /**
     * 初始化滤镜
     * @param context
     */
    private fun initFilters(context: Context) {
        releaseFilters()
        // 相机输入滤镜
        mFilterArrays.put(CameraIndex, GLImageOESInputFilter(context))
        // 美颜滤镜
//        mFilterArrays.put(BeautyIndex, GLImageBeautyFilter(context))
//         彩妆滤镜
//        mFilterArrays.put(MakeupIndex, GLImageMakeupFilter(context, null))
        // 美型滤镜
//        mFilterArrays.put(FaceAdjustIndex, GLImageFaceReshapeFilter(context))
        // LUT/颜色滤镜
//        mFilterArrays.put(FilterIndex, null)
        // 贴纸资源滤镜
//        mFilterArrays.put(ResourceIndex, null)
        // 景深滤镜
//        mFilterArrays.put(DepthBlurIndex, GLImageDepthBlurFilter(context))
        // 暗角滤镜
//        mFilterArrays.put(VignetteIndex, GLImageVignetteFilter(context))
        // 显示输出
        mFilterArrays.put(DisplayIndex, GLImageFilter(context))
        // 人脸关键点调试
//        mFilterArrays.put(FacePointIndex, GLImageFacePointsFilter(context))
    }

    /**
     * 是否切换边框模糊
     * @param enableEdgeBlur
     */
    @Synchronized
    fun changeEdgeBlurFilter(enableEdgeBlur: Boolean) {
        if (enableEdgeBlur) {
//            mFilterArrays.get(DisplayIndex).release()
//            val filter = GLImageFrameEdgeBlurFilter(mContext)
//            filter.onInputSizeChanged(mTextureWidth, mTextureHeight)
//            filter.onDisplaySizeChanged(mViewWidth, mViewHeight)
//            mFilterArrays.put(DisplayIndex, filter)
        } else {
            mFilterArrays.get(DisplayIndex).release()
            val filter = GLImageFilter(mContext!!)
            filter.onInputSizeChanged(mTextureWidth, mTextureHeight)
            filter.onDisplaySizeChanged(mViewWidth, mViewHeight)
            mFilterArrays.put(DisplayIndex, filter)
        }
    }

    /**
     * 切换动态滤镜
     * @param color
     */
    @Synchronized
    fun changeDynamicFilter(color: DynamicColor?) {
//        if (mFilterArrays.get(FilterIndex) != null) {
//            mFilterArrays.get(FilterIndex).release()
//            mFilterArrays.put(FilterIndex, null)
//        }
//        if (color == null) {
//            return
//        }
//        val filter = GLImageDynamicColorFilter(mContext, color)
//        filter.onInputSizeChanged(mTextureWidth, mTextureHeight)
//        filter.initFrameBuffer(mTextureWidth, mTextureHeight)
//        filter.onDisplaySizeChanged(mViewWidth, mViewHeight)
//        mFilterArrays.put(FilterIndex, filter)
    }

    /**
     * 切换动态滤镜
     * @param dynamicMakeup
     */
    @Synchronized
    fun changeDynamicMakeup(dynamicMakeup: DynamicMakeup) {
//        if (mFilterArrays.get(MakeupIndex) != null) {
//            (mFilterArrays.get(MakeupIndex) as GLImageMakeupFilter).changeMakeupData(dynamicMakeup)
//        } else {
//            val filter = GLImageMakeupFilter(mContext, dynamicMakeup)
//            filter.onInputSizeChanged(mTextureWidth, mTextureHeight)
//            filter.initFrameBuffer(mTextureWidth, mTextureHeight)
//            filter.onDisplaySizeChanged(mViewWidth, mViewHeight)
//            mFilterArrays.put(MakeupIndex, filter)
//        }
    }

    /**
     * 切换动态资源
     * @param color
     */
    @Synchronized
    fun changeDynamicResource(color: DynamicColor?) {
//        if (mFilterArrays.get(ResourceIndex) != null) {
//            mFilterArrays.get(ResourceIndex).release()
//            mFilterArrays.put(ResourceIndex, null)
//        }
//        if (color == null) {
//            return
//        }
//        val filter = GLImageDynamicColorFilter(mContext, color)
//        filter.onInputSizeChanged(mTextureWidth, mTextureHeight)
//        filter.initFrameBuffer(mTextureWidth, mTextureHeight)
//        filter.onDisplaySizeChanged(mViewWidth, mViewHeight)
//        mFilterArrays.put(ResourceIndex, filter)
    }

    /**
     * 切换动态资源
     * @param sticker
     */
    @Synchronized
    fun changeDynamicResource(sticker: Any/*DynamicSticker?*/) {
        // 释放旧滤镜
//        if (mFilterArrays.get(ResourceIndex) != null) {
//            mFilterArrays.get(ResourceIndex).release()
//            mFilterArrays.put(ResourceIndex, null)
//        }
//        if (sticker == null) {
//            return
//        }
//        val filter = GLImageDynamicStickerFilter(mContext, sticker)
//        // 设置输入输入大小，初始化fbo等
//        filter.onInputSizeChanged(mTextureWidth, mTextureHeight)
//        filter.initFrameBuffer(mTextureWidth, mTextureHeight)
//        filter.onDisplaySizeChanged(mViewWidth, mViewHeight)
//        mFilterArrays.put(ResourceIndex, filter)
    }

    /**
     * 绘制纹理
     * @param inputTexture
     * @param mMatrix
     * @return
     */
    fun drawFrame(inputTexture: Int, mMatrix: FloatArray): Int {
        var currentTexture = inputTexture
        if (mFilterArrays.get(CameraIndex) == null || mFilterArrays.get(DisplayIndex) == null) {
            return currentTexture
        }
        if (mFilterArrays.get(CameraIndex) is GLImageOESInputFilter) {
            (mFilterArrays.get(CameraIndex) as GLImageOESInputFilter).setTextureTransformMatrix(mMatrix)
        }
        currentTexture = mFilterArrays.get(CameraIndex)
                .drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!)
        // 如果处于对比状态，不做处理
        if (!mCameraParam.showCompare) {
            // 美颜滤镜
            if (mFilterArrays.get(BeautyIndex) != null) {
                if (mFilterArrays.get(BeautyIndex) is IBeautify && mCameraParam.beauty != null) {
                    (mFilterArrays.get(BeautyIndex) as IBeautify).onBeauty(mCameraParam.beauty!!)
                }
                currentTexture = mFilterArrays.get(BeautyIndex).drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!)
            }

            // 彩妆滤镜
//            if (mFilterArrays.get(MakeupIndex) != null) {
//                currentTexture = mFilterArrays.get(MakeupIndex).drawFrameBuffer(currentTexture, mVertexBuffer, mTextureBuffer)
//            }
//
//            // 美型滤镜
//            if (mFilterArrays.get(FaceAdjustIndex) != null) {
//                if (mFilterArrays.get(FaceAdjustIndex) is IBeautify) {
//                    (mFilterArrays.get(FaceAdjustIndex) as IBeautify).onBeauty(mCameraParam.beauty)
//                }
//                currentTexture = mFilterArrays.get(FaceAdjustIndex).drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!)
//            }
//
//            // 绘制颜色滤镜
//            if (mFilterArrays.get(FilterIndex) != null) {
//                currentTexture = mFilterArrays.get(FilterIndex).drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!)
//            }
//
//            // 资源滤镜，可以是贴纸、滤镜甚至是彩妆类型
//            if (mFilterArrays.get(ResourceIndex) != null) {
//                currentTexture = mFilterArrays.get(ResourceIndex).drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!)
//            }
//
//            // 景深
//            if (mFilterArrays.get(DepthBlurIndex) != null) {
//                mFilterArrays.get(DepthBlurIndex).setFilterEnable(mCameraParam.enableDepthBlur)
//                currentTexture = mFilterArrays.get(DepthBlurIndex).drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!)
//            }
//
//            // 暗角
//            if (mFilterArrays.get(VignetteIndex) != null) {
//                mFilterArrays.get(VignetteIndex).setFilterEnable(mCameraParam.enableVignette)
//                currentTexture = mFilterArrays.get(VignetteIndex).drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!)
//            }
        }

        // 显示输出，需要调整视口大小
        mFilterArrays.get(DisplayIndex).drawFrame(currentTexture, mDisplayVertexBuffer!!, mDisplayTextureBuffer!!)

        return currentTexture
    }

    /**
     * 绘制调试用的人脸关键点
     * @param mCurrentTexture
     */
//    fun drawFacePoint(mCurrentTexture: Int) {
//        if (mFilterArrays.get(FacePointIndex) != null) {
//            if (mCameraParam.drawFacePoints && LandmarkEngine.getInstance().hasFace()) {
//                mFilterArrays.get(FacePointIndex).drawFrame(mCurrentTexture, mDisplayVertexBuffer!!, mDisplayTextureBuffer!!)
//            }
//        }
//    }

    /**
     * 设置输入纹理大小
     * @param width
     * @param height
     */
    fun setTextureSize(width: Int, height: Int) {
        mTextureWidth = width
        mTextureHeight = height
    }

    /**
     * 设置纹理显示大小
     * @param width
     * @param height
     */
    fun setDisplaySize(width: Int, height: Int) {
        mViewWidth = width
        mViewHeight = height
        adjustCoordinateSize()
        onFilterChanged()
    }

    /**
     * 调整滤镜
     */
    private fun onFilterChanged() {
        for (i in 0 until NumberIndex) {
            if (mFilterArrays.get(i) != null) {
                mFilterArrays.get(i).onInputSizeChanged(mTextureWidth, mTextureHeight)
                // 到显示之前都需要创建FBO，这里限定是防止创建多余的FBO，节省GPU资源
                if (i < DisplayIndex) {
                    mFilterArrays.get(i).initFrameBuffer(mTextureWidth, mTextureHeight)
                }
                mFilterArrays.get(i).onDisplaySizeChanged(mViewWidth, mViewHeight)
            }
        }
    }

    /**
     * 调整由于surface的大小与SurfaceView大小不一致带来的显示问题
     */
    private fun adjustCoordinateSize() {
        var textureCoord: FloatArray? = null
        var vertexCoord: FloatArray? = null
        val textureVertices = TextureRotationUtils.TextureVertices
        val vertexVertices = TextureRotationUtils.CubeVertices
        val ratioMax = Math.max(mViewWidth.toFloat() / mTextureWidth,
                mViewHeight.toFloat() / mTextureHeight)
        // 新的宽高
        val imageWidth = Math.round(mTextureWidth * ratioMax)
        val imageHeight = Math.round(mTextureHeight * ratioMax)
        // 获取视图跟texture的宽高比
        val ratioWidth = imageWidth.toFloat() / mViewWidth.toFloat()
        val ratioHeight = imageHeight.toFloat() / mViewHeight.toFloat()
        if (mScaleType === ScaleType.CENTER_INSIDE) {
            vertexCoord = floatArrayOf(vertexVertices[0] / ratioHeight, vertexVertices[1] / ratioWidth, vertexVertices[2] / ratioHeight, vertexVertices[3] / ratioWidth, vertexVertices[4] / ratioHeight, vertexVertices[5] / ratioWidth, vertexVertices[6] / ratioHeight, vertexVertices[7] / ratioWidth)
        } else if (mScaleType === ScaleType.CENTER_CROP) {
            val distHorizontal = (1 - 1 / ratioWidth) / 2
            val distVertical = (1 - 1 / ratioHeight) / 2
            textureCoord = floatArrayOf(addDistance(textureVertices[0], distHorizontal), addDistance(textureVertices[1], distVertical), addDistance(textureVertices[2], distHorizontal), addDistance(textureVertices[3], distVertical), addDistance(textureVertices[4], distHorizontal), addDistance(textureVertices[5], distVertical), addDistance(textureVertices[6], distHorizontal), addDistance(textureVertices[7], distVertical))
        }
        if (vertexCoord == null) {
            vertexCoord = vertexVertices
        }
        if (textureCoord == null) {
            textureCoord = textureVertices
        }
        // 更新VertexBuffer 和 TextureBuffer
        mDisplayVertexBuffer!!.clear()
        mDisplayVertexBuffer!!.put(vertexCoord).position(0)
        mDisplayTextureBuffer!!.clear()
        mDisplayTextureBuffer!!.put(textureCoord).position(0)
    }

    /**
     * 计算距离
     * @param coordinate
     * @param distance
     * @return
     */
    private fun addDistance(coordinate: Float, distance: Float): Float {
        return if (coordinate == 0.0f) distance else 1 - distance
    }

//    fun touchDown(e: MotionEvent): StaticStickerNormalFilter? {
//
//        if (mFilterArrays.get(ResourceIndex) != null) {
//            val glImageFilter = mFilterArrays.get(ResourceIndex)
//            if (glImageFilter is GLImageDynamicStickerFilter) {
//                val glImageDynamicStickerFilter = glImageFilter as GLImageDynamicStickerFilter
//                tempVec.set(e.x, e.y, 0)
//                val staticStickerNormalFilter = GestureHelp.hit(tempVec, glImageDynamicStickerFilter.getmFilters())
//                if (staticStickerNormalFilter != null) {
//                    Log.d("touchSticker", "找到贴纸")
//                } else {
//                    Log.d("touchSticker", "没有贴纸")
//                }
//                return staticStickerNormalFilter
//            }
//        }
//        return null
//    }

    companion object {

        val instance: RenderManager
            get() = RenderManagerHolder.instance

        val tempVec = Vector3()
    }
}
