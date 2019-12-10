package com.wyman.fakedouyin.renderer

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.wyman.fakedouyin.presenter.RecordPresenter
import com.wyman.filterlibrary.glfilter.GLImageFilter
import com.wyman.filterlibrary.glfilter.GLImageOESInputFilter
import com.wyman.filterlibrary.glfilter.color.DynamicColor
import com.wyman.filterlibrary.utils.OpenGLUtils
import com.wyman.filterlibrary.utils.TextureRotationUtils
import java.lang.ref.WeakReference


import java.nio.FloatBuffer

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 录制线程
 */
class RecordRenderer(presenter: RecordPresenter) : GLSurfaceView.Renderer,SurfaceTexture.OnFrameAvailableListener {
    private var mInputFilter: GLImageOESInputFilter? = null // 相机输入滤镜
    private var mColorFilter: GLImageFilter? = null  // 颜色滤镜
    private var mImageFilter: GLImageFilter? = null // 输出滤镜
    // 顶点坐标缓冲
    private var mVertexBuffer: FloatBuffer? = null
    // 纹理坐标缓冲
    private var mTextureBuffer: FloatBuffer? = null
    // 预览顶点坐标缓冲
    private var mDisplayVertexBuffer: FloatBuffer? = null
    // 预览纹理坐标缓冲
    private var mDisplayTextureBuffer: FloatBuffer? = null
    // 输入纹理大小
    protected var mTextureWidth: Int = 0
    protected var mTextureHeight: Int = 0
    // 控件视图大小
    protected var mViewWidth: Int = 0
    protected var mViewHeight: Int = 0
    // 输入纹理
    private var mInputTexture: Int = 0
    private var mSurfaceTexture: SurfaceTexture? = null
    private val mMatrix = FloatArray(16)
    // presenter
    private val mWeakPresenter: WeakReference<RecordPresenter> = WeakReference(presenter)

    //传递 SurfaceTexture 接口
    private var onSurfaceCreateListener: OnSurfaceCreateListener? = null
    fun setOnSurfaceCreateListener(onSurfaceCreateListener: OnSurfaceCreateListener) {
        this.onSurfaceCreateListener = onSurfaceCreateListener
    }
    interface OnSurfaceCreateListener {
        fun onSurfaceCreate(surfaceTexture: SurfaceTexture)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        mVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices)
        mTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices)
        mDisplayVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices)
        mDisplayTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices)

        mInputTexture = OpenGLUtils.createOESTexture()
        mSurfaceTexture = SurfaceTexture(mInputTexture)
        GLES30.glDisable(GL10.GL_DITHER)
        GLES30.glClearColor(0f, 0f, 0f, 0f)
        GLES30.glEnable(GL10.GL_CULL_FACE)
        GLES30.glEnable(GL10.GL_DEPTH_TEST)
        initFilters()
        mWeakPresenter.get()?.let{
            it.onBindSurfaceTexture(mSurfaceTexture)
            it.onBindSharedContext(EGL14.eglGetCurrentContext())
        }
        onSurfaceCreateListener?.onSurfaceCreate(mSurfaceTexture!!)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        mViewWidth = width
        mViewHeight = height
        onFilterSizeChanged()
        adjustCoordinateSize()
    }

    override fun onDrawFrame(gl: GL10) {
        //        Log.e("Record","onDrawFrame...");
        if (mSurfaceTexture != null) {
            mSurfaceTexture!!.updateTexImage()
            mSurfaceTexture!!.getTransformMatrix(mMatrix)
        }
        GLES30.glClearColor(0f, 0f, 0f, 0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        if (mInputFilter == null || mImageFilter == null) {
            return
        }
        mInputFilter!!.setTextureTransformMatrix(mMatrix)
        // 将OES纹理绘制到FBO中
        var currentTexture = mInputTexture
        currentTexture = mInputFilter!!.drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!)
        // 将德罗斯特滤镜绘制到FBO中
        if (mColorFilter != null) {
            currentTexture = mColorFilter!!.drawFrameBuffer(currentTexture, mVertexBuffer!!, mTextureBuffer!!)
        }
        // 将最终的结果会是预览
        mImageFilter!!.drawFrame(currentTexture, mDisplayVertexBuffer!!, mDisplayTextureBuffer!!)
        // 录制视频
//        mWeakPresenter.get()?.onRecordFrameAvailable(currentTexture, mSurfaceTexture!!.timestamp)
    }

    fun setTextureSize(width: Int, height: Int) {
        mTextureWidth = width
        mTextureHeight = height
    }

    private fun initFilters() {
//        mInputFilter = GLImageOESInputFilter(mWeakPresenter.get()?.activity!!)
        mInputFilter = mWeakPresenter.get()?.activity?.let {
            //it 为 activity
            GLImageOESInputFilter(it)
        }
//        mColorFilter = GLImageDrosteFilter(mWeakPresenter.get().getActivity())
//        mImageFilter = GLImageFilter(mWeakPresenter.get()?.activity!!)
        mImageFilter = mWeakPresenter.get()?.activity?.let { GLImageFilter(it) }
    }

    private fun onFilterSizeChanged() {
        if (mInputFilter != null) {
            mInputFilter!!.onInputSizeChanged(mTextureWidth, mTextureHeight)
            mInputFilter!!.initFrameBuffer(mTextureWidth, mTextureHeight)
            mInputFilter!!.onDisplaySizeChanged(mViewWidth, mViewHeight)
        }
        if (mColorFilter != null) {
            mColorFilter!!.onInputSizeChanged(mTextureWidth, mTextureHeight)
            mColorFilter!!.initFrameBuffer(mTextureWidth, mTextureHeight)
            mColorFilter!!.onDisplaySizeChanged(mViewWidth, mViewHeight)
        }
        if (mImageFilter != null) {
            mImageFilter!!.onInputSizeChanged(mTextureWidth, mTextureHeight)
            mImageFilter!!.onDisplaySizeChanged(mViewWidth, mViewHeight)
        }
    }

    /**
     * 切换滤镜
     * @param context
     * @param color
     */
    @Synchronized
    fun changeDynamicFilter(context: Context, color: DynamicColor?) {
        if (mColorFilter != null) {
            mColorFilter!!.release()
            mColorFilter = null
        }
        if (color == null) {
            return
        }
//        mColorFilter = GLImageDynamicColorFilter(context, color)
//        mColorFilter!!.onInputSizeChanged(mTextureWidth, mTextureHeight)
//        mColorFilter!!.initFrameBuffer(mTextureWidth, mTextureHeight)
//        mColorFilter!!.onDisplaySizeChanged(mViewWidth, mViewHeight)
    }

    /**
     * 调整由于surface的大小与SurfaceView大小不一致带来的显示问题
     */
    private fun adjustCoordinateSize() {
        val textureCoord: FloatArray
        val vertexCoord = TextureRotationUtils.CubeVertices
        val textureVertices = TextureRotationUtils.TextureVertices
        val ratioMax = Math.max(mViewWidth.toFloat() / mTextureWidth,
                mViewHeight.toFloat() / mTextureHeight)
        // 新的宽高
        val imageWidth = mTextureWidth * ratioMax
        val imageHeight = mTextureHeight * ratioMax
        // 获取视图跟texture的宽高比
        val ratioWidth = imageWidth / mViewWidth.toFloat()
        val ratioHeight = imageHeight / mViewHeight.toFloat()
        val distHorizontal = (1 - 1 / ratioWidth) / 2
        val distVertical = (1 - 1 / ratioHeight) / 2
        textureCoord = floatArrayOf(addDistance(textureVertices[0], distHorizontal), addDistance(textureVertices[1], distVertical), addDistance(textureVertices[2], distHorizontal), addDistance(textureVertices[3], distVertical), addDistance(textureVertices[4], distHorizontal), addDistance(textureVertices[5], distVertical), addDistance(textureVertices[6], distHorizontal), addDistance(textureVertices[7], distVertical))
        // 更新VertexBuffer 和 TextureBuffer
        mDisplayVertexBuffer!!.clear()
        mDisplayVertexBuffer!!.put(vertexCoord).position(0)
        mDisplayTextureBuffer!!.clear()
        mDisplayTextureBuffer!!.put(textureCoord).position(0)
    }

    private fun addDistance(coordinate: Float, distance: Float): Float {
        return if (coordinate == 0.0f) distance else 1 - distance
    }

    interface OnFrameAvailableListener {
        fun onFrameAvailable(surfaceTexture: SurfaceTexture)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        onFrameAvailableListener?.onFrameAvailable(surfaceTexture!!)
    }
    private var onFrameAvailableListener: OnFrameAvailableListener? = null
    fun setOnFrameAvailableListener(onFrameAvailableListener: OnFrameAvailableListener) {
        this.onFrameAvailableListener = onFrameAvailableListener
    }

    companion object {

        // 录制状态
        private val RECORDING_OFF = 0
        private val RECORDING_ON = 1
    }
}
