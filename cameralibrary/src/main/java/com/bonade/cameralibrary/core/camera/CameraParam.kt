package com.bonade.cameralibrary.core.camera

import android.hardware.Camera
import com.bonade.cameralibrary.core.listener.OnCameraCallback
import com.bonade.cameralibrary.core.listener.OnCaptureListener
import com.bonade.cameralibrary.core.listener.OnFpsListener
import com.bonade.cameralibrary.listener.OnGallerySelectedListener
import com.bonade.cameralibrary.listener.OnMusicSelectListener
import com.bonade.cameralibrary.listener.OnPreviewCaptureListener
import com.bonade.cameralibrary.model.AspectRatio
import com.bonade.cameralibrary.model.GalleryType
import com.bonade.filterlibrary.beauty.bean.BeautyParam

/**
 * Created by wyman
 * on 2019-09-06.
 * 摄像头硬件的能力。
 * 传感器的灵敏度不能保证总是包含值。
 * 相机配置参数
 */
class CameraParam private constructor(){

    // 是否显示人脸关键点
    var drawFacePoints : Boolean = false
    // 是否显示fps
    var showFps : Boolean = false
    //相机长宽比
    var aspectRatio : AspectRatio = AspectRatio.Ratio_16_9
    // 当前长宽比
    var currentRatio : Float = Ratio_16_9
    // 期望帧率
    var expectFps : Int = DESIRED_PREVIEW_FPS
    // 实际帧率
    var previewFps : Int = 0
    // 期望预览宽度
    var expectWidth : Int = 0
    // 期望预览高度
    var expectHeight : Int = 0
    //  实际预览宽度
    var previewWidth : Int = 0
    // 实际预览高度
    var previewHeight : Int = 0
    // 是否高清拍照
    var highDefinition : Boolean = false
    //预览角度
    var orientation : Int = 0
    // 是否后置摄像头
    var backCamera : Boolean = false
    // 摄像头id
    var cameraId : Int = Camera.CameraInfo.CAMERA_FACING_FRONT
    // 是否支持闪光灯
    var supportFlash :Boolean = false
    //对焦权重，最大值为1000
    var focusWeight : Int = 1000
    //是否允许录制
    var recordable : Boolean = true
    // 录制时长(ms)
    var recordTime : Int = DEFAULT_RECORD_TIME
    // 录音权限
    var audioPermitted : Boolean = false
    //是否允许录制音频
    var recordAudio :Boolean = true
    // 是否触屏拍照
    var touchTake : Boolean = false

    // 是否延时拍照
    var takeDelay: Boolean = false
    // 是否夜光增强
    var luminousEnhancement: Boolean = false
    // 亮度值
    var brightness: Int = 0
    // 拍照类型
    var mGalleryType: GalleryType = GalleryType.PICTURE

    // 图库监听器
    var gallerySelectedListener: OnGallerySelectedListener? = null
    // 拍摄监听器
    var captureListener: OnPreviewCaptureListener? = null
    // 选音乐监听器
    var musicSelectListener: OnMusicSelectListener? = null

    // 预览回调
    var cameraCallback: OnCameraCallback? = null
    // 截屏回调
    var captureCallback: OnCaptureListener? = null
    // fps回调
    var fpsCallback: OnFpsListener? = null

    // 是否显示对比效果
    var showCompare: Boolean = false
    // 是否拍照
    var isTakePicture: Boolean = false

    // 是否允许景深
    var enableDepthBlur: Boolean = false
    // 是否允许暗角
    var enableVignette: Boolean = false

    // 美颜参数
    var beauty : BeautyParam? = null

    companion object{
        // 最大权重
        val MAX_FOCUS_WEIGHT = 1000
        // 录制时长(毫秒)
        val DEFAULT_RECORD_TIME = 15000
        // 16:9的默认宽高(理想值)
        val DEFAULT_16_9_WIDTH = 1280
        val DEFAULT_16_9_HEIGHT = 720
        //4:3的默认宽高(理想值) 最大公约数：256
        val DEFAULT_4_3_WIDTH = 1024
        val DEFAULT_4_3_HEIGHT = 768
        // 期望fps
        val DESIRED_PREVIEW_FPS = 30
        // 这里反过来是因为相机的分辨率跟屏幕的分辨率宽高刚好反过来
        val Ratio_4_3 = 0.75f
        val Ratio_16_9 = 0.5625f

        //对焦权重最大值
        val Weight = 100

        //单例设计
        val INSTANCE = CameraParam()
    }

    init {
        reset()
    }

    private fun reset(){
        drawFacePoints = false
        showFps = false
        aspectRatio = AspectRatio.Ratio_16_9
        currentRatio = Ratio_16_9
        expectFps = DESIRED_PREVIEW_FPS
        previewFps = 0
        expectWidth = DEFAULT_16_9_WIDTH
        expectHeight = DEFAULT_16_9_HEIGHT
        previewWidth = 0
        previewHeight = 0
        highDefinition = false
        orientation = 0
        backCamera = false
        cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
        supportFlash = false
        focusWeight = 1000
        recordable = true
        recordTime = DEFAULT_RECORD_TIME
        audioPermitted = false
        recordAudio = true
        touchTake = false
        takeDelay = false
        luminousEnhancement = false
        brightness = -1
        mGalleryType = GalleryType.PICTURE
        gallerySelectedListener = null
        captureListener = null
        musicSelectListener = null
        cameraCallback = null
        captureCallback = null
        fpsCallback = null
        showCompare = false
        isTakePicture = false
        enableDepthBlur = false
        enableVignette = false
        beauty = BeautyParam()
    }



}



















