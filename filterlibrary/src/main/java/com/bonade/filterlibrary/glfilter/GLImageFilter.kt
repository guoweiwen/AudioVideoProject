package com.bonade.filterlibrary.glfilter

import android.content.Context
import android.graphics.PointF
import android.opengl.GLES30
import android.text.TextUtils
import com.bonade.filterlibrary.utils.OpenGLUtils
import com.bonade.filterlibrary.utils.TextureRotationUtils

import java.nio.FloatBuffer
import java.util.LinkedList

/**
 * 基类滤镜
 * Created by cain on 2017/7/9.
 */

open class GLImageFilter
@JvmOverloads constructor(protected var mContext: Context, // 纹理字符串
      protected var mVertexShader: String = VERTEX_SHADER,
      protected var mFragmentShader: String = FRAGMENT_SHADER) {


    protected var TAG = javaClass.simpleName

    private val mRunOnDraw: LinkedList<Runnable>

    // 是否初始化成功
    /**
     * 判断是否初始化
     * @return
     */
    var isInitialized: Boolean = false
        protected set
    // 滤镜是否可用，默认可用
    protected var mFilterEnable = true

    // 每个顶点坐标有几个参数
    protected var mCoordsPerVertex = TextureRotationUtils.CoordsPerVertex
    // 顶点坐标数量
    protected var mVertexCount = TextureRotationUtils.CubeVertices.size / mCoordsPerVertex

    // 句柄
    protected var mProgramHandle: Int = 0
    protected var mPositionHandle: Int = 0
    protected var mTextureCoordinateHandle: Int = 0
    protected var mInputTextureHandle: Int = 0

    // 渲染的Image的宽高
    protected var mImageWidth: Int = 0
    protected var mImageHeight: Int = 0

    // 显示输出的宽高
    /**
     * 获取输出宽度
     * @return
     */
    var displayWidth: Int = 0
        protected set
    /**
     * 获取输出高度
     * @return
     */
    var displayHeight: Int = 0
        protected set

    // FBO的宽高，可能跟输入的纹理大小不一致
    protected var mFrameWidth = -1
    protected var mFrameHeight = -1

    // FBO
    protected var mFrameBuffers: IntArray? = null
    protected var mFrameBufferTextures: IntArray? = null

    /**
     * 获取Texture类型
     * GLES30.TEXTURE_2D / GLES11Ext.GL_TEXTURE_EXTERNAL_OES等
     */
    open fun getTextureType(): Int {
        return GLES30.GL_TEXTURE_2D
    }

    init {
        mRunOnDraw = LinkedList()
        // 初始化程序句柄
        initProgramHandle()
    }// 记录shader数据

    /**
     * 初始化程序句柄
     */
    open fun initProgramHandle() {
        // 只有在shader都不为空的情况下才初始化程序句柄
        if (!TextUtils.isEmpty(mVertexShader) && !TextUtils.isEmpty(mFragmentShader)) {
            mProgramHandle = OpenGLUtils.createProgram(mVertexShader, mFragmentShader)
            mPositionHandle = GLES30.glGetAttribLocation(mProgramHandle, "aPosition")
            mTextureCoordinateHandle = GLES30.glGetAttribLocation(mProgramHandle, "aTextureCoord")
            mInputTextureHandle = GLES30.glGetUniformLocation(mProgramHandle, "inputTexture")
            isInitialized = true
        } else {
            mPositionHandle = OpenGLUtils.GL_NOT_INIT
            mTextureCoordinateHandle = OpenGLUtils.GL_NOT_INIT
            mInputTextureHandle = OpenGLUtils.GL_NOT_TEXTURE
            isInitialized = false
        }
    }

    /**
     * Surface发生变化时调用
     * @param width
     * @param height
     */
    fun onInputSizeChanged(width: Int, height: Int) {
        mImageWidth = width
        mImageHeight = height
    }

    /**
     * 显示视图发生变化时调用
     * @param width
     * @param height
     */
    fun onDisplaySizeChanged(width: Int, height: Int) {
        displayWidth = width
        displayHeight = height
    }

    /**
     * 绘制Frame
     * @param textureId
     * @param vertexBuffer
     * @param textureBuffer
     */
    fun drawFrame(textureId: Int, vertexBuffer: FloatBuffer,
                  textureBuffer: FloatBuffer): Boolean {
        // 没有初始化、输入纹理不合法、滤镜不可用时直接返回
        if (!isInitialized || textureId == OpenGLUtils.GL_NOT_INIT || !mFilterEnable) {
            return false
        }

        // 设置视口大小
        GLES30.glViewport(0, 0, displayWidth, displayHeight)
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        // 使用当前的program
        GLES30.glUseProgram(mProgramHandle)
        // 运行延时任务
        runPendingOnDrawTasks()

        // 绘制纹理
        onDrawTexture(textureId, vertexBuffer, textureBuffer)

        return true
    }

    /**
     * 绘制到FBO
     * @param textureId
     * @param vertexBuffer
     * @param textureBuffer
     * @return FBO绑定的Texture
     */
    fun drawFrameBuffer(textureId: Int, vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer): Int {
        // 没有FBO、没初始化、输入纹理不合法、滤镜不可用时，直接返回
        if (textureId == OpenGLUtils.GL_NOT_TEXTURE || mFrameBuffers == null
                || !isInitialized || !mFilterEnable) {
            return textureId
        }

        // 绑定FBO
        GLES30.glViewport(0, 0, mFrameWidth, mFrameHeight)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers!![0])
        // 使用当前的program
        GLES30.glUseProgram(mProgramHandle)
        // 运行延时任务，这个要放在glUseProgram之后，要不然某些设置项会不生效
        runPendingOnDrawTasks()

        // 绘制纹理
        onDrawTexture(textureId, vertexBuffer, textureBuffer)

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        return mFrameBufferTextures!![0]
    }

    fun drawFrameBufferClear(textureId: Int, vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer): Int {
        // 没有FBO、没初始化、输入纹理不合法、滤镜不可用时，直接返回
        if (textureId == OpenGLUtils.GL_NOT_TEXTURE || mFrameBuffers == null
                || !isInitialized || !mFilterEnable) {
            return textureId
        }

        // 绑定FBO
        GLES30.glViewport(0, 0, mFrameWidth, mFrameHeight)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers!![0])
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        // 使用当前的program
        GLES30.glUseProgram(mProgramHandle)
        // 运行延时任务，这个要放在glUseProgram之后，要不然某些设置项会不生效
        runPendingOnDrawTasks()

        // 绘制纹理
        onDrawTexture(textureId, vertexBuffer, textureBuffer)

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)
        return mFrameBufferTextures!![0]
    }

    /**
     * 绘制
     * @param textureId
     * @param vertexBuffer
     * @param textureBuffer
     */
    protected fun onDrawTexture(textureId: Int, vertexBuffer: FloatBuffer, textureBuffer: FloatBuffer) {
        // 绑定顶点坐标缓冲
        vertexBuffer.position(0)
        GLES30.glVertexAttribPointer(mPositionHandle, mCoordsPerVertex,
                GLES30.GL_FLOAT, false, 0, vertexBuffer)
        GLES30.glEnableVertexAttribArray(mPositionHandle)
        // 绑定纹理坐标缓冲
        textureBuffer.position(0)
        GLES30.glVertexAttribPointer(mTextureCoordinateHandle, 2,
                GLES30.GL_FLOAT, false, 0, textureBuffer)
        GLES30.glEnableVertexAttribArray(mTextureCoordinateHandle)
        // 绑定纹理
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(getTextureType(), textureId)
        GLES30.glUniform1i(mInputTextureHandle, 0)
        onDrawFrameBegin()
        onDrawFrame()
        onDrawFrameAfter()
        // 解绑
        GLES30.glDisableVertexAttribArray(mPositionHandle)
        GLES30.glDisableVertexAttribArray(mTextureCoordinateHandle)
        GLES30.glBindTexture(getTextureType(), 0)

        GLES30.glUseProgram(0)
    }

    /**
     * 调用glDrawArrays/glDrawElements之前，方便添加其他属性
     */
    open fun onDrawFrameBegin() {

    }

    /**
     * 绘制图像
     */
    protected fun onDrawFrame() {
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, mVertexCount)
    }

    /**
     * glDrawArrays/glDrawElements调用之后，方便销毁其他属性
     */
    fun onDrawFrameAfter() {

    }

    protected fun onUnbindTextureValue() {

    }

    /**
     * 释放资源
     */
    fun release() {
        if (isInitialized) {
            GLES30.glDeleteProgram(mProgramHandle)
            mProgramHandle = OpenGLUtils.GL_NOT_INIT
        }
        destroyFrameBuffer()
    }

    /**
     * 创建FBO
     * @param width
     * @param height
     */
    fun initFrameBuffer(width: Int, height: Int) {
        if (!isInitialized) {
            return
        }
        if (mFrameBuffers != null && (mFrameWidth != width || mFrameHeight != height)) {
            destroyFrameBuffer()
        }
        if (mFrameBuffers == null) {
            mFrameWidth = width
            mFrameHeight = height
            mFrameBuffers = IntArray(1)
            mFrameBufferTextures = IntArray(1)
            OpenGLUtils.createFrameBuffer(mFrameBuffers, mFrameBufferTextures, width, height)
        }
    }

    /**
     * 销毁纹理
     */
    fun destroyFrameBuffer() {
        if (!isInitialized) {
            return
        }
        if (mFrameBufferTextures != null) {
            GLES30.glDeleteTextures(1, mFrameBufferTextures, 0)
            mFrameBufferTextures = null
        }

        if (mFrameBuffers != null) {
            GLES30.glDeleteFramebuffers(1, mFrameBuffers, 0)
            mFrameBuffers = null
        }
        mFrameWidth = -1
        mFrameWidth = -1
    }

    /**
     * 设置滤镜是否可用
     * @param enable
     */
    fun setFilterEnable(enable: Boolean) {
        mFilterEnable = enable
    }

    ///------------------ 统一变量(uniform)设置 ------------------------///
    protected fun setInteger(location: Int, intValue: Int) {
        runOnDraw(Runnable { GLES30.glUniform1i(location, intValue) })
    }

    protected fun setFloat(location: Int, floatValue: Float) {
        runOnDraw(Runnable { GLES30.glUniform1f(location, floatValue) })
    }

    protected fun setFloatVec2(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES30.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setFloatVec3(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES30.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setFloatVec4(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES30.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setFloatArray(location: Int, arrayValue: FloatArray) {
        runOnDraw(Runnable { GLES30.glUniform1fv(location, arrayValue.size, FloatBuffer.wrap(arrayValue)) })
    }

    protected fun setPoint(location: Int, point: PointF) {
        runOnDraw(Runnable {
            val vec2 = FloatArray(2)
            vec2[0] = point.x
            vec2[1] = point.y
            GLES30.glUniform2fv(location, 1, vec2, 0)
        })
    }

    protected fun setUniformMatrix3f(location: Int, matrix: FloatArray) {
        runOnDraw(Runnable { GLES30.glUniformMatrix3fv(location, 1, false, matrix, 0) })
    }

    protected fun setUniformMatrix4f(location: Int, matrix: FloatArray) {
        runOnDraw(Runnable { GLES30.glUniformMatrix4fv(location, 1, false, matrix, 0) })
    }

    /**
     * 添加延时任务
     * @param runnable
     */
    protected fun runOnDraw(runnable: Runnable) {
        synchronized(mRunOnDraw) {
            mRunOnDraw.addLast(runnable)
        }
    }

    /**
     * 运行延时任务
     */
    protected fun runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run()
        }
    }

    companion object {

        protected val VERTEX_SHADER = "" +
                "attribute vec4 aPosition;                                  \n" +
                "attribute vec4 aTextureCoord;                              \n" +
                "varying vec2 textureCoordinate;                            \n" +
                "void main() {                                              \n" +
                "    gl_Position = aPosition;                               \n" +
                "    textureCoordinate = aTextureCoord.xy;                  \n" +
                "}                                                          \n"

        protected val FRAGMENT_SHADER = "" +
                "precision mediump float;                                   \n" +
                "varying vec2 textureCoordinate;                            \n" +
                "uniform sampler2D inputTexture;                                \n" +
                "void main() {                                              \n" +
                "    gl_FragColor = texture2D(inputTexture, textureCoordinate); \n" +
                "}                                                          \n"

        protected fun clamp(value: Float, min: Float, max: Float): Float {
            if (value < min) {
                return min
            } else if (value > max) {
                return max
            }
            return value
        }
    }
}
