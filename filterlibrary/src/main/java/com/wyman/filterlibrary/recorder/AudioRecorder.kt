package com.wyman.filterlibrary.recorder

import android.media.AudioRecord
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread

import com.cgfay.media.SoundTouch
import com.cgfay.uitls.utils.FileUtils

import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

/**
 * 音频录制器
 * @author CainHuang
 * @date 2019/6/30
 */
class AudioRecorder : Runnable {

    // 录音器
    private var mRecorder: AudioRecord? = null
    // 录音Handler
    private var mRecordHandler: Handler? = null
    // 编码Handler
    private var mEncodeHandler: Handler? = null
    // 编码器
    private var mMediaCodec: MediaCodec? = null
    // 缓冲区
    private var mBufferInfo: MediaCodec.BufferInfo? = null
    // 音频参数
    private var mAudioParams: AudioParams? = null
    // SoundTouch
    private var mSoundTouch: SoundTouch? = null
    // 文件流
    private var mFileStream: BufferedOutputStream? = null
    // 录制状态监听器
    private var mRecordListener: OnRecordListener? = null
    // 采样时长
    private var mSampleDuration: Long = 0
    // 时长
    private var mDuration: Long = 0

    val mediaType: MediaType
        get() = MediaType.AUDIO

    /**
     * 获取时钟
     * @return
     */
    private val timeUs: Long
        get() = System.nanoTime() / 1000L

    fun setOnRecordListner(listener: OnRecordListener) {
        mRecordListener = listener
    }

    /**
     * 循环遍历
     */
    private fun loop() {
        mRecordHandler!!.post(this)
    }

    /**
     * 开始录制
     */
    fun startRecord() {
        mRecorder!!.startRecording()
        mMediaCodec!!.start()
        loop()
        if (mRecordListener != null) {
            mRecordListener!!.onRecordStart(MediaType.AUDIO)
        }
    }

    /**
     * 停止录制
     */
    fun stopRecord() {
        if (mRecordHandler == null) {
            return
        }
        mRecordHandler!!.post {
            if (mRecorder == null) {
                return@mRecordHandler.post
            }
            mRecorder!!.stop()
            mRecorder!!.release()
            mRecorder = null
        }
    }

    /**
     * 准备编码器
     * @throws IOException
     */
    @Throws(IOException::class)
    fun prepare(params: AudioParams) {

        mAudioParams = params
        val bufferSize = AudioRecord.getMinBufferSize(
                params.getSampleRate(), AudioParams.CHANNEL,
                AudioParams.BITS_PER_SAMPLE)
        mRecorder = AudioRecord(
                MediaRecorder.AudioSource.MIC, params.getSampleRate(),
                AudioParams.CHANNEL, AudioParams.BITS_PER_SAMPLE, bufferSize)
        mRecordHandler = bindHandlerThread("AudioRecordThread")
        mEncodeHandler = bindHandlerThread("AudioEncodeThread")

        // 创建变速变调处理库
        mSoundTouch = SoundTouch()
        mSoundTouch!!.setChannels(AudioParams.CHANNEL_COUNT)
        mSoundTouch!!.setSampleRate(mAudioParams!!.getSampleRate())

        // 变速不变调
        val rate = params.getSpeedMode().speed
        mSoundTouch!!.setRate(rate)
        mSoundTouch!!.setPitch(1.0f / rate)

        mSampleDuration = (params.getNbSamples() * SECOND_IN_US / params.getSampleRate()).toLong()
        mDuration = 0


        val audioFormat = MediaFormat.createAudioFormat(AudioParams.MIME_TYPE,
                AudioParams.SAMPLE_RATE, AudioParams.CHANNEL_COUNT)
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,
                MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioParams.CHANNEL)
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, AudioParams.BIT_RATE)
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, AudioParams.CHANNEL_COUNT)
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1024 * 4)
        mMediaCodec = MediaCodec.createEncoderByType(AudioParams.MIME_TYPE)
        mMediaCodec!!.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mBufferInfo = MediaCodec.BufferInfo()
        mFileStream = BufferedOutputStream(FileOutputStream(mAudioParams!!.getAudioPath()))
    }

    /**
     * 释放数据
     */
    fun release() {
        if (mEncodeHandler != null) {
            mEncodeHandler!!.looper.quitSafely()
            mEncodeHandler = null
        }
        if (mRecordHandler != null) {
            mRecordHandler!!.looper.quitSafely()
            mRecordHandler = null
        }
    }

    override fun run() {
        if (mRecorder == null) {
            //通知编码线程退出
            stopEncode()
            mRecordHandler!!.looper.quitSafely()
            mRecordHandler = null
            return
        }
        val buffer = ByteArray(mAudioParams!!.getNbSamples())
        val bytes = mRecorder!!.read(buffer, 0, buffer.size)
        if (bytes > 0) {
            pcmEncode(buffer, bytes)
        }
        loop()
    }

    /**
     * 停止编码
     */
    private fun stopEncode() {
        mEncodeHandler!!.post {
            if (mMediaCodec == null) {
                return@mEncodeHandler.post
            }
            mMediaCodec!!.stop()
            mMediaCodec!!.release()
            mMediaCodec = null
            FileUtils.closeSafely(mFileStream)
            mFileStream = null
            mSoundTouch!!.close()
            mSoundTouch = null
            if (mRecordListener != null) {
                mRecordListener!!.onRecordFinish(RecordInfo(mAudioParams!!.getAudioPath(),
                        mDuration, mediaType))
            }
            mEncodeHandler!!.looper.quitSafely()
            mEncodeHandler = null
        }
    }

    /**
     * 编码一帧数据
     * @param data
     * @param length
     */
    private fun pcmEncode(data: ByteArray, length: Int) {
        mEncodeHandler!!.post {
            if (mSoundTouch != null) {
                mSoundTouch!!.putSamples(data)
                while (true) {
                    val modified = ByteArray(4096)
                    val count = mSoundTouch!!.receiveSamples(modified)
                    if (count > 0) {
                        onEncode(modified, count * 2)
                        drain()
                    } else {
                        break
                    }
                }
            } else {
                onEncode(data, length)
                drain()
            }
        }
    }

    /**
     * 编码一帧数据
     * @param data
     * @param length
     */
    private fun onEncode(data: ByteArray?, length: Int) {
        val inputBuffers = mMediaCodec!!.inputBuffers
        while (true) {
            val inputBufferIndex = mMediaCodec!!.dequeueInputBuffer(BUFFER_TIME_OUT.toLong())
            if (inputBufferIndex >= 0) {
                val inputBuffer = inputBuffers[inputBufferIndex]
                inputBuffer.clear()
                inputBuffer.position(0)
                if (data != null) {
                    inputBuffer.put(data, 0, length)
                }
                if (length <= 0) {
                    mMediaCodec!!.queueInputBuffer(inputBufferIndex, 0, 0,
                            timeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                    break
                } else {
                    mMediaCodec!!.queueInputBuffer(inputBufferIndex, 0, length,
                            timeUs, 0)
                }
                break
            }
        }
    }

    /**
     * 将音频数据写入文件中
     */
    private fun drain() {
        mBufferInfo = MediaCodec.BufferInfo()
        val encoderOutputBuffers = mMediaCodec!!.outputBuffers
        var encoderStatus = mMediaCodec!!.dequeueOutputBuffer(mBufferInfo!!, BUFFER_TIME_OUT.toLong())
        while (encoderStatus >= 0) {
            val encodedData = encoderOutputBuffers[encoderStatus]
            val outSize = mBufferInfo!!.size
            encodedData.position(mBufferInfo!!.offset)
            encodedData.limit(mBufferInfo!!.offset + mBufferInfo!!.size)
            val data = ByteArray(outSize + 7)
            addADTSHeader(data, outSize + 7)
            encodedData.get(data, 7, outSize)
            try {
                mFileStream!!.write(data, 0, data.size)
                mFileStream!!.flush()
                mDuration += mSampleDuration
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (mDuration >= mAudioParams!!.getMaxDuration()) {
                stopRecord()
            }
            mMediaCodec!!.releaseOutputBuffer(encoderStatus, false)
            encoderStatus = mMediaCodec!!.dequeueOutputBuffer(mBufferInfo!!, BUFFER_TIME_OUT.toLong())
        }
    }

    /**
     * 添加AAC的ADTS头部信息
     * @param packet
     * @param length
     */
    private fun addADTSHeader(packet: ByteArray, length: Int) {
        val audioType = 2  // AAC LC, Audio Object Type
        val freqIndex = 4  // 48KHz, Sampling Frequency Index
        val channels = 1   // 1 channel, Channel Configuration
        packet[0] = 0xFF.toByte()
        packet[1] = 0xF9.toByte()
        packet[2] = ((audioType - 1 shl 6) + (freqIndex shl 2) + (channels shr 2)).toByte()
        packet[3] = ((channels and 3 shl 6) + (length shr 11)).toByte()
        packet[4] = (length and 0x7FF shr 3).toByte()
        packet[5] = ((length and 7 shl 5) + 0x1F).toByte()
        packet[6] = 0xFC.toByte()
    }

    companion object {

        private val SECOND_IN_US = 1000000

        private val BUFFER_TIME_OUT = 10000

        /**
         * 绑定Handler线程
         * @param tag
         * @return
         */
        private fun bindHandlerThread(tag: String): Handler {
            val thread = HandlerThread(tag)
            thread.start()
            return Handler(thread.looper)
        }
    }

}
