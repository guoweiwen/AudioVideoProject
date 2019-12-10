package com.wyman.filterlibrary.recorder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import android.view.Surface

import java.io.IOException
import java.nio.ByteBuffer

/**
 * 视频编码器
 * @author CainHuang
 * @date 2019/6/30
 */
class VideoEncoder
/**
 * 配置编码器和复用器等参数
 */
@Throws(IOException::class)
constructor(/**
             * 获取视频参数
             * @return
             */
            val videoParams: VideoParams, private val mRecordingListener: OnEncodingListener?) {

    /**
     * 返回编码器的Surface
     */
    val inputSurface: Surface
    private var mMediaMuxer: MediaMuxer? = null
    private var mMediaCodec: MediaCodec? = null
    private val mBufferInfo: MediaCodec.BufferInfo = MediaCodec.BufferInfo()
    private var mTrackIndex: Int = 0
    private var mMuxerStarted: Boolean = false
    // 录制起始时间戳
    private var mStartTimeStamp: Long = 0
    // 录制时长
    /**
     * 获取编码的时长
     * @return
     */
    var duration: Long = 0
        private set

    init {

        // 设置编码格式
        val videoWidth = if (videoParams.getVideoWidth() % 2 == 0)
            videoParams.getVideoWidth()
        else
            videoParams.getVideoWidth() - 1
        val videoHeight = if (videoParams.getVideoHeight() % 2 == 0)
            videoParams.getVideoHeight()
        else
            videoParams.getVideoHeight() - 1
        val format = MediaFormat.createVideoFormat(VideoParams.MIME_TYPE, videoWidth, videoHeight)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, videoParams.getBitRate())
        format.setInteger(MediaFormat.KEY_FRAME_RATE, VideoParams.FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, VideoParams.I_FRAME_INTERVAL)
        if (VERBOSE) {
            Log.d(TAG, "format: $format")
        }
        // 创建编码器
        mMediaCodec = MediaCodec.createEncoderByType(VideoParams.MIME_TYPE)
        mMediaCodec!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        inputSurface = mMediaCodec!!.createInputSurface()
        mMediaCodec!!.start()

        // 创建复用器
        mMediaMuxer = MediaMuxer(videoParams.getVideoPath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        mTrackIndex = -1
        mMuxerStarted = false
    }

    /**
     * 释放编码器资源
     */
    fun release() {
        if (VERBOSE) {
            Log.d(TAG, "releasing encoder objects")
        }
        if (mMediaCodec != null) {
            mMediaCodec!!.stop()
            mMediaCodec!!.release()
            mMediaCodec = null
        }
        if (mMediaMuxer != null) {
            if (mMuxerStarted) {
                mMediaMuxer!!.stop()
            }
            mMediaMuxer!!.release()
            mMediaMuxer = null
        }
    }

    /**
     * 编码一帧数据到复用器中
     * @param endOfStream
     */
    fun drainEncoder(endOfStream: Boolean) {
        val TIMEOUT_USEC = 10000
        if (VERBOSE) {
            Log.d(TAG, "drainEncoder($endOfStream)")
        }

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder")
            mMediaCodec!!.signalEndOfInputStream()
        }

        var encoderOutputBuffers = mMediaCodec!!.outputBuffers
        while (true) {
            val encoderStatus = mMediaCodec!!.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC.toLong())
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) {
                    break      // out of while
                } else {
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS")
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                encoderOutputBuffers = mMediaCodec!!.outputBuffers
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (mMuxerStarted) {
                    throw RuntimeException("format changed twice")
                }
                val newFormat = mMediaCodec!!.outputFormat
                if (VERBOSE) {
                    Log.d(TAG, "encoder output format changed: " + newFormat.getString(MediaFormat.KEY_MIME))
                }
                // 提取视频轨道并打开复用器
                mTrackIndex = mMediaMuxer!!.addTrack(newFormat)
                mMediaMuxer!!.start()
                mMuxerStarted = true
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: $encoderStatus")
            } else {
                val encodedData = encoderOutputBuffers[encoderStatus]
                        ?: throw RuntimeException("encoderOutputBuffer " + encoderStatus +
                                " was null")

                if (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    if (VERBOSE) {
                        Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG")
                    }
                    mBufferInfo.size = 0
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw RuntimeException("muxer hasn't started")
                    }

                    // 计算录制时钟
                    calculateTimeUs(mBufferInfo)
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset)
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size)
                    // 将编码数据写入复用器中
                    mMediaMuxer!!.writeSampleData(mTrackIndex, encodedData, mBufferInfo)
                    if (VERBOSE) {
                        Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer, ts=" +
                                mBufferInfo.presentationTimeUs)
                    }

                    // 录制时长回调
                    mRecordingListener?.onEncoding(duration)

                }

                mMediaCodec!!.releaseOutputBuffer(encoderStatus, false)

                if (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly")
                    } else {
                        if (VERBOSE) {
                            Log.d(TAG, "end of stream reached")
                        }
                    }
                    break      // out of while
                }
            }
        }
    }

    /**
     * 计算pts
     * @param info
     */
    private fun calculateTimeUs(info: MediaCodec.BufferInfo) {
        if (mStartTimeStamp == 0L) {
            mStartTimeStamp = info.presentationTimeUs
        } else {
            duration = info.presentationTimeUs - mStartTimeStamp
        }
    }

    /**
     * 编码监听器
     */
    interface OnEncodingListener {

        fun onEncoding(duration: Long)
    }

    companion object {

        private val TAG = "VideoEncoder"
        private val VERBOSE = false
    }

}