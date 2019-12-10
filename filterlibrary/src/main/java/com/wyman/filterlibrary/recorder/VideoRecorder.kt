package com.wyman.filterlibrary.recorder

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.annotation.NonNull
import com.wyman.filterlibrary.gles.EglCore
import com.wyman.filterlibrary.gles.WindowSurface
import com.wyman.filterlibrary.glfilter.GLImageFilter
import com.wyman.filterlibrary.utils.OpenGLUtils
import com.wyman.filterlibrary.utils.TextureRotationUtils
import com.wyman.utillibrary.logE


import java.io.IOException
import java.lang.ref.WeakReference
import java.nio.FloatBuffer

/**
 * 视频录制器
 * @author CainHuang
 * @date 2019/6/30
 */
class VideoRecorder : Runnable, VideoEncoder.OnEncodingListener {

    // 录制用的OpenGL上下文和EGLSurface
    private var mInputWindowSurface: WindowSurface? = null
    private var mEglCore: EglCore? = null
    private var mImageFilter: GLImageFilter? = null
    private var mVertexBuffer: FloatBuffer? = null
    private var mTextureBuffer: FloatBuffer? = null

    // 视频编码器
    private var mVideoEncoder: VideoEncoder? = null

    // 录制Handler;
    @Volatile
    private var mHandler: RecordHandler? = null

    // 录制状态锁
    private val mReadyFence = Object()
    private var mReady: Boolean = false
    private var mRunning: Boolean = false

    // 录制监听器
    private var mRecordListener: OnRecordListener? = null

    // 倍速录制索引你
    private var mDrawFrameIndex: Int = 0  // 绘制帧索引，用于表示预览的渲染次数，用于大于1.0倍速录制的丢帧操作
    private var mFirstTime: Long = 0 // 录制开始的时间，方便开始录制

    /**
     * 判断是否正在录制
     * @return
     */
    val isRecording: Boolean
        get() = synchronized(mReadyFence) {
            return mRunning
        }

    /**
     * 设置录制监听器
     * @param listener
     */
    fun setOnRecordListner(listener: OnRecordListener) {
        mRecordListener = listener
    }

    /**
     * 开始录制
     * @param params 录制参数
     */
    fun startRecord(params: VideoParams) {
        "VideoRecorder: startRecord()".logE()
        synchronized(mReadyFence) {
            if (mRunning) {
                "VideoRecorder thread already running".logE()
                return
            }
            mRunning = true
            Thread(this, "VideoRecorder").start()
            while (!mReady) {
                try {
                    mReadyFence.wait()
                } catch (ie: InterruptedException) {
                    // ignore
                }

            }
        }

        mDrawFrameIndex = 0
        mFirstTime = -1
        mHandler!!.sendMessage(mHandler!!.obtainMessage(MSG_START_RECORDING, params))
    }

    /**
     * 停止录制
     */
    fun stopRecord() {
        if (mHandler != null) {
            mHandler!!.sendMessage(mHandler!!.obtainMessage(MSG_STOP_RECORDING))
            mHandler!!.sendMessage(mHandler!!.obtainMessage(MSG_QUIT))
        }
    }

    /**
     * 释放所有资源
     */
    fun release() {
        if (mHandler != null) {
            mHandler!!.sendMessage(mHandler!!.obtainMessage(MSG_QUIT))
        }
    }

    /**
     * 录制帧可用状态
     * @param texture
     * @param timestamp
     */
    fun frameAvailable(texture: Int, timestamp: Long) {
        synchronized(mReadyFence) {
            if (!mReady) {
                return
            }
        }
        // 时间戳为0时，不可用
        if (timestamp == 0L) {
            return
        }

        if (mHandler != null) {
            mHandler!!.sendMessage(mHandler!!.obtainMessage(MSG_FRAME_AVAILABLE,
                    (timestamp shr 32).toInt(), timestamp.toInt(), texture))
        }
    }

    // 编码回调
    override fun onEncoding(duration: Long) {
        if (mRecordListener != null) {
            mRecordListener!!.onRecording(MediaType.VIDEO, duration)
        }
    }


    override fun run() {
        Looper.prepare()
        synchronized(mReadyFence) {
            mHandler = RecordHandler(this)
            mReady = true
            mReadyFence.notify()
        }
        Looper.loop()

        "Video record thread exiting".logE()
        synchronized(mReadyFence) {
            mRunning = false
            mReady = mRunning
            mHandler = null
        }
    }

    /**
     * 录制Handler
     */
    private class RecordHandler(encoder: VideoRecorder) : Handler() {

        private val mWeakRecorder: WeakReference<VideoRecorder>

        init {
            mWeakRecorder = WeakReference(encoder)
        }

        override fun handleMessage(inputMessage: Message) {
            val what = inputMessage.what
            val obj = inputMessage.obj

            val encoder = mWeakRecorder.get()
            "RecordHandler.handleMessage: encoder is null".logE()

            when (what) {
                MSG_START_RECORDING -> {
                    encoder!!.onStartRecord(obj as VideoParams)
                }

                MSG_STOP_RECORDING -> {
                    encoder!!.onStopRecord()
                }

                MSG_FRAME_AVAILABLE -> {
                    val timestamp = inputMessage.arg1.toLong() shl 32 or (inputMessage.arg2.toLong() and 0xffffffffL)
                    encoder!!.onRecordFrameAvailable(obj as Int, timestamp)
                }

                MSG_QUIT -> {
                    Looper.myLooper()!!.quit()
                }

                else -> throw RuntimeException("Unhandled msg what=$what")
            }
        }
    }

    /**
     * 开始录制
     * @param params
     */
    private fun onStartRecord(@NonNull params: VideoParams) {
        "onStartRecord $params".logE()
        mVertexBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.CubeVertices)
        mTextureBuffer = OpenGLUtils.createFloatBuffer(TextureRotationUtils.TextureVertices)
        try {
            mVideoEncoder = VideoEncoder(params, this)
        } catch (ioe: IOException) {
            throw RuntimeException(ioe)
        }

        // 创建EGL上下文和Surface
        mEglCore = EglCore(params.getEglContext(), EglCore.FLAG_RECORDABLE)
        mInputWindowSurface = WindowSurface(mEglCore, mVideoEncoder!!.inputSurface, true)
        mInputWindowSurface!!.makeCurrent()
        // 创建录制用的滤镜
        mImageFilter = GLImageFilter(null)
        mImageFilter!!.onInputSizeChanged(params.getVideoWidth(), params.getVideoHeight())
        mImageFilter!!.onDisplaySizeChanged(params.getVideoWidth(), params.getVideoHeight())
        // 录制开始回调
        if (mRecordListener != null) {
            mRecordListener!!.onRecordStart(MediaType.VIDEO)
        }
    }

    /**
     * 停止录制
     */
    private fun onStopRecord() {
        if (mVideoEncoder == null) {
            return
        }
        "onStopRecord".logE()
        mVideoEncoder!!.drainEncoder(true)
        mVideoEncoder!!.release()
        if (mImageFilter != null) {
            mImageFilter!!.release()
            mImageFilter = null
        }
        if (mInputWindowSurface != null) {
            mInputWindowSurface!!.release()
            mInputWindowSurface = null
        }
        if (mEglCore != null) {
            mEglCore!!.release()
            mEglCore = null
        }

        // 录制完成回调
        if (mRecordListener != null) {
            mRecordListener!!.onRecordFinish(RecordInfo(mVideoEncoder!!.videoParams.getVideoPath(),
                    mVideoEncoder!!.duration, MediaType.VIDEO))
        }
        mVideoEncoder = null
    }

    /**
     * 录制帧可用
     * @param texture
     * @param timestampNanos
     */
    private fun onRecordFrameAvailable(texture: Int, timestampNanos: Long) {
        "onRecordFrameAvailable".logE()
        if (mVideoEncoder == null) {
            return
        }
        val mode = mVideoEncoder!!.videoParams.getSpeedMode()
        // 快速录制的时候，需要做丢帧处理
        if (mode === SpeedMode.MODE_FAST || mode === SpeedMode.MODE_EXTRA_FAST) {
            var interval = 2
            if (mode === SpeedMode.MODE_EXTRA_FAST) {
                interval = 3
            }
            if (mDrawFrameIndex % interval == 0) {
                drawFrame(texture, timestampNanos)
            }
        } else {
            drawFrame(texture, timestampNanos)
        }
        mDrawFrameIndex++
    }

    /**
     * 绘制编码一帧数据
     * @param texture
     * @param timestampNanos
     */
    private fun drawFrame(texture: Int, timestampNanos: Long) {
        mInputWindowSurface!!.makeCurrent()
        mImageFilter!!.drawFrame(texture, mVertexBuffer!!, mTextureBuffer!!)
        mInputWindowSurface!!.setPresentationTime(getPTS(timestampNanos))
        mInputWindowSurface!!.swapBuffers()
        mVideoEncoder!!.drainEncoder(false)
    }


    /**
     * 计算时间戳
     * @return
     */
    private fun getPTS(timestameNanos: Long): Long {
        val mode = mVideoEncoder!!.videoParams.getSpeedMode()
        return if (mode === SpeedMode.MODE_NORMAL) { // 正常录制的时候，使用SurfaceTexture传递过来的时间戳
            timestameNanos
        } else { // 倍速状态下，需要根据帧间间隔来算实际的时间戳
            val time = System.nanoTime()
            if (mFirstTime <= 0) {
                mFirstTime = time
            }
            (mFirstTime + (time - mFirstTime) / mode.speed) as Long
        }
    }

    companion object {
        // 开始录制
        private val MSG_START_RECORDING = 0
        // 停止录制
        private val MSG_STOP_RECORDING = 1
        // 录制帧可用
        private val MSG_FRAME_AVAILABLE = 2
        // 退出录制
        private val MSG_QUIT = 3
    }
}
