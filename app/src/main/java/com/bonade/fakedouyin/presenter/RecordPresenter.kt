package com.bonade.fakedouyin.presenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaRecorder
import android.opengl.EGLContext
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.bonade.cameralibrary.core.camera.CameraEngine
import com.bonade.cameralibrary.core.camera.CameraParam
import com.bonade.fakedouyin.BaseActivity
import com.bonade.fakedouyin.recordvideo.CameraActivity


import java.io.File
import java.util.ArrayList

/**
 * 录制器的presenter
 * @author CainHuang
 * @date 2019/7/7
 */
class RecordPresenter(private var mActivity: BaseActivity?) : SurfaceTexture.OnFrameAvailableListener {

    // 音视频参数
//    private val mVideoParams: VideoParams
//    private val mAudioParams: AudioParams
    // 录制操作开始
    private var mOperateStarted = false

    // 当前录制进度
    private var mCurrentProgress: Float = 0.toFloat()
    // 最大时长
    private var mMaxDuration: Long = 0
    // 剩余时长
    private var mRemainDuration: Long = 0

    // 视频编码器
//    private val mMediaRecorder: MediaRecorder?

    // 视频列表
//    private val mVideoList = ArrayList<MediaInfo>()

    // 录制音频信息
//    private var mAudioInfo: RecordInfo? = null
    // 录制视频信息
//    private var mVideoInfo: RecordInfo? = null

    // 视频合成命令队列
//    private var mCommandQueue: VideoCommandQueue? = null


    /**
     * 获取绑定的Activity
     * @return
     */
    val activity: Activity?
        get() = mActivity

    /**
     * 获取录制视频的段数
     * @return
     */
//    val recordVideos: Int
//        get() = mVideoList.size

    init {

//        // 创建视频流
//        mMediaRecorder = MediaRecorder(this)
//
//        // 视频参数
//        mVideoParams = VideoParams()
//        mVideoParams.setVideoPath(getVideoTempPath(mActivity!!))
//
//        // 音频参数
//        mAudioParams = AudioParams()
//        mAudioParams.setAudioPath(getAudioTempPath(mActivity!!))
//
//        // 命令队列
//        mCommandQueue = VideoCommandQueue()
    }

    /**
     * 启动
     */
    fun onResume() {
        openCamera()
    }

    /**
     * 暂停
     */
    fun onPause() {
        releaseCamera()
    }

    /**
     * 释放资源
     */
    fun release() {
        mActivity = null
//        if (mMediaRecorder != null) {
//            mMediaRecorder!!.release()
//        }
//        if (mCommandQueue != null) {
//            mCommandQueue!!.release()
//            mCommandQueue = null
//        }
    }

    /**
     * 设置速度模式
     * @param mode
     */
//    fun setSpeedMode(mode: SpeedMode) {
//        mVideoParams.setSpeedMode(mode)
//        mAudioParams.setSpeedMode(mode)
//    }

    /**
     * 设置录制时长
     * @param seconds
     */
//    fun setRecordSeconds(seconds: Int) {
//        mRemainDuration = seconds * MediaRecorder.SECOND_IN_US
//        mMaxDuration = mRemainDuration
//        mVideoParams.setMaxDuration(mMaxDuration)
//        mAudioParams.setMaxDuration(mMaxDuration)
//    }

    /**
     * 设置是否允许音频录制
     * @param enable
     */
//    fun setAudioEnable(enable: Boolean) {
//        if (mMediaRecorder != null) {
//            mMediaRecorder!!.setEnableAudio(enable)
//        }
//    }

    /**
     * 开始录制
     */
//    fun startRecord() {
//        if (mOperateStarted) {
//            return
//        }
//        mMediaRecorder!!.startRecord(mVideoParams, mAudioParams)
//        mOperateStarted = true
//    }

    /**
     * 停止录制
     */
    fun stopRecord() {
//        if (!mOperateStarted) {
//            return
//        }
//        mOperateStarted = false
//        mMediaRecorder!!.stopRecord()
    }

    fun onRecordStart() {
//        mActivity!!.hidViews()
    }

    fun onRecording(duration: Long) {
//        val progress = duration * 1.0f / mVideoParams.getMaxDuration()
//        mActivity!!.setProgress(progress)
//        if (duration > mRemainDuration) {
//            stopRecord()
//        }
    }

//    fun onRecordFinish(info: RecordInfo) {
//        if (info.getType() === MediaType.AUDIO) {
//            mAudioInfo = info
//        } else if (info.getType() === MediaType.VIDEO) {
//            mVideoInfo = info
//            mCurrentProgress = info.getDuration() * 1.0f / mVideoParams.getMaxDuration()
//        }
//        mActivity!!.showViews()
//        if (mMediaRecorder!!.enableAudio() && (mAudioInfo == null || mVideoInfo == null)) {
//            return
//        }
//        if (mMediaRecorder!!.enableAudio()) {
//            val currentFile = generateOutputPath()
//            FileUtils.createFile(currentFile)
//            mCommandQueue!!.execCommand(VideoCommandQueue.mergeAudioVideo(mVideoInfo!!.getFileName(),
//                    mAudioInfo!!.getFileName(), currentFile),
//                    { result ->
//                        if (result === 0) {
//                            mVideoList.add(MediaInfo(currentFile, mVideoInfo!!.getDuration()))
//                            mRemainDuration -= mVideoInfo!!.getDuration()
//                            mActivity!!.addProgressSegment(mCurrentProgress)
//                            mActivity!!.showViews()
//                            mCurrentProgress = 0f
//                        }
//                        // 删除旧的文件
//                        FileUtils.deleteFile(mAudioInfo!!.getFileName())
//                        FileUtils.deleteFile(mVideoInfo!!.getFileName())
//                        mAudioInfo = null
//                        mVideoInfo = null
//
//                        // 如果剩余时间为0
//                        if (mRemainDuration <= 0) {
//                            mergeAndEdit()
//                        }
//                    })
//        } else {
//            if (mVideoInfo != null) {
//                val currentFile = generateOutputPath()
//                FileUtils.moveFile(mVideoInfo!!.getFileName(), currentFile)
//                mVideoList.add(MediaInfo(currentFile, mVideoInfo!!.getDuration()))
//                mRemainDuration -= mVideoInfo!!.getDuration()
//                mAudioInfo = null
//                mVideoInfo = null
//                mActivity!!.addProgressSegment(mCurrentProgress)
//                mActivity!!.showViews()
//                mCurrentProgress = 0f
//            }
//        }
//    }

    /**
     * SurfaceTexture创建成功回调
     * @param surfaceTexture
     */
    fun onBindSurfaceTexture(surfaceTexture: SurfaceTexture?) {
        CameraEngine.setPreviewSurface(surfaceTexture!!)
        Log.e(TAG,"setPreviewSurface...")
        surfaceTexture?.setOnFrameAvailableListener(this)
    }

    /**
     * 绑定EGLContext
     * @param context
     */
    fun onBindSharedContext(context: EGLContext) {
//        mVideoParams.setEglContext(context)
    }

    /**
     * 录制帧可用
     * @param texture
     * @param timestamp
     */
    fun onRecordFrameAvailable(texture: Int, timestamp: Long) {
//        if (mOperateStarted && mMediaRecorder != null && mMediaRecorder!!.isRecording()) {
//            mMediaRecorder!!.frameAvailable(texture, timestamp)
//        }
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
//        Log.e(TAG,"onFrameAvailable...")
        (mActivity as CameraActivity).onFrameAvailable()
    }

    /**
     * 删除上一段视频
     */
    fun deleteLastVideo() {
//        val index = mVideoList.size - 1
//        if (index >= 0) {
//            val mediaInfo = mVideoList.get(index)
//            val path = mediaInfo.getFileName()
//            mRemainDuration += mediaInfo.getDuration()
//            if (!TextUtils.isEmpty(path)) {
//                FileUtils.deleteFile(path)
//                mVideoList.removeAt(index)
//            }
//        }
//        mActivity!!.deleteProgressSegment()
    }

    /**
     * 打开相机
     */
    private fun openCamera() {
        releaseCamera()
        CameraParam.INSTANCE.backCamera = true
        Log.e(TAG,"openCamera")
        CameraEngine.openCamera(mActivity!!)
        calculateImageSize()
        CameraEngine.startPreview()
        Log.e(TAG,"startPreview")
    }

    /**
     * 计算imageView 的宽高
     */
    private fun calculateImageSize() {
        val width: Int
        val height: Int
        if (CameraParam.INSTANCE.orientation === 90 || CameraParam.INSTANCE.orientation === 270) {
            width = CameraParam.INSTANCE.previewHeight
            height = CameraParam.INSTANCE.previewWidth
        } else {
            width = CameraParam.INSTANCE.previewWidth
            height = CameraParam.INSTANCE.previewHeight
        }
//        mVideoParams.setVideoSize(width, height)
//        mActivity!!.updateTextureSize(width, height)
    }

    /**
     * 释放资源
     */
    private fun releaseCamera() {
        CameraEngine.releaseCamera()
    }

    /**
     * 合并视频并跳转至编辑页面
     */
    fun mergeAndEdit() {
//        if (mVideoList.size < 1) {
//            return
//        }
//
//        if (mVideoList.size == 1) {
//            val path = mVideoList.get(0).getFileName()
//            val outputPath = generateOutputPath()
//            FileUtils.copyFile(path, outputPath)
//            val intent = Intent(mActivity, VideoEditActivity::class.java)
//            intent.putExtra(VideoEditActivity.VIDEO_PATH, outputPath)
//            mActivity!!.startActivity(intent)
//        } else {
//            mActivity!!.showProgressDialog()
//            val videos = ArrayList<String>()
//            for (info in mVideoList) {
//                if (info != null && !TextUtils.isEmpty(info!!.getFileName())) {
//                    videos.add(info!!.getFileName())
//                }
//            }
//            val finalPath = generateOutputPath()
//            mCommandQueue!!.execCommand(VideoCommandQueue.mergeVideo(mActivity, videos, finalPath),
//                    { result ->
//                        mActivity!!.hideProgressDialog()
//                        if (result === 0) {
//                            val intent = Intent(mActivity, VideoEditActivity::class.java)
//                            intent.putExtra(VideoEditActivity.VIDEO_PATH, finalPath)
//                            mActivity!!.startActivity(intent)
//                        } else {
//                            mActivity!!.showToast("合成失败")
//                        }
//                    })
//        }
    }

    /**
     * 创建合成的视频文件名
     * @return
     */
//    fun generateOutputPath(): String {
//        return PathConstraints.getVideoCachePath(mActivity)
//    }

    companion object {

        private val TAG = "RecordPresenter"

        /**
         * 获取音频缓存绝对路径
         * @param context
         * @return
         */
        private fun getAudioTempPath(context: Context): String {
            val directoryPath: String
            // 判断外部存储是否可用，如果不可用则使用内部存储路径
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                directoryPath = context.externalCacheDir!!.absolutePath
            } else { // 使用内部存储缓存目录
                directoryPath = context.cacheDir.absolutePath
            }
            val path = directoryPath + File.separator + "temp.aac"
            val file = File(path)
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            return path
        }

        /**
         * 获取视频缓存绝对路径
         * @param context
         * @return
         */
        private fun getVideoTempPath(context: Context): String {
            val directoryPath: String
            // 判断外部存储是否可用，如果不可用则使用内部存储路径
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                directoryPath = context.externalCacheDir!!.absolutePath
            } else { // 使用内部存储缓存目录
                directoryPath = context.cacheDir.absolutePath
            }
            val path = directoryPath + File.separator + "temp.mp4"
            val file = File(path)
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            return path
        }
    }

}
