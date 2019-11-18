package com.bonade.cameralibrary.core.camera

import android.app.Activity
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.view.Surface
import android.view.SurfaceHolder
import com.bonade.cameralibrary.model.CalculateType
import java.io.IOException
import java.lang.RuntimeException
import java.util.*
import kotlin.math.abs

/**
 * Created by wyman
 * on 2019-09-13.
 * 相机引擎类
 */
object CameraEngine {
    private const val TAG = "CameraEngine"
    private var mCamera : Camera? = null

    fun openCamera(context: Context) {
        openCamera(context, CameraParam.INSTANCE.expectFps)
    }

    /**
     * 根据ID打开相机
     * @param expectFps
     */
    fun openCamera(context: Context, expectFps: Int) {
        val width = CameraParam.INSTANCE.expectWidth
        val height = CameraParam.INSTANCE.expectHeight
        openCamera(context, CameraParam.INSTANCE.cameraId, expectFps, width, height)
    }

    /**
     * 打开相机
     * @param context
     * @param cameraID
     * @param expectFps
     * @param expectWidth
     * @param expectHeight
     */
    fun openCamera(context : Context,cameraID : Int,expectFps : Int,expectWidth : Int,expectHeight : Int){
        mCamera = Camera.open(cameraID)
        val cameraParam = CameraParam.INSTANCE
        cameraParam.cameraId = cameraID
        val parameters = mCamera!!.parameters
        cameraParam.supportFlash = checkSupportFlashLight(parameters)
        cameraParam.previewFps = chooseFixedPreviewFps(parameters, expectFps * 1000)
        parameters?.setRecordingHint(true)
        mCamera?.parameters = parameters
        setPreviewSize(mCamera, expectWidth, expectHeight)
        setPictureSize(mCamera!!, expectWidth, expectHeight)
        calculateCameraPreviewOrientation(context as Activity)
        mCamera?.setDisplayOrientation(cameraParam.orientation)
    }

    /**
     * 设置预览大小
     * @param camera
     * @param expectWidth
     * @param expectHeight
     */
    private fun setPreviewSize(camera: Camera?, expectWidth: Int, expectHeight: Int) {
        val parameters = camera!!.parameters
        val size = calculatePerfectSize(parameters.supportedPreviewSizes,
                expectWidth, expectHeight, CalculateType.Lower)
        parameters.setPreviewSize(size.width, size.height)
        CameraParam.INSTANCE.previewWidth = size.width
        CameraParam.INSTANCE.previewHeight = size.height
        camera.parameters = parameters
    }

    /**
     * 设置拍摄的照片大小
     * @param camera
     * @param expectWidth
     * @param expectHeight
     */
    private fun setPictureSize(camera: Camera, expectWidth: Int, expectHeight: Int) {
        val parameters = camera.parameters
        val size = calculatePerfectSize(parameters.supportedPictureSizes,
                expectWidth, expectHeight, CalculateType.Max)
        parameters.setPictureSize(size.width, size.height)
        camera.parameters = parameters
    }

    /**
     * 添加预览回调
     * @param callback
     * @param previewBuffer
     */
    fun setPreviewCallbackWithBuffer(callback : Camera.PreviewCallback,previewBuffer : ByteArray){
        mCamera?.setPreviewCallbackWithBuffer(callback)
        mCamera?.addCallbackBuffer(previewBuffer)
    }

    /**
     * 添加预览回调
     * @param callback
     */
    fun setPreviewCallback(callback: Camera.PreviewCallback){
        mCamera?.setPreviewCallback(callback)
    }

    /**
     * 设置预览Surface
     * @param holder
     */
    fun setPreviewSurface(holder: SurfaceHolder) {
        try {
            mCamera?.setPreviewDisplay(holder)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 释放相机
     */
    fun releaseCamera(){
        mCamera?.setPreviewCallback(null)
        mCamera?.setPreviewCallbackWithBuffer(null)
        mCamera?.addCallbackBuffer(null)
        mCamera?.stopPreview()
        mCamera?.release()
        mCamera = null
        CameraParam.INSTANCE.supportFlash = false
    }

    /**
     * 设置预览角度，setDisplayOrientation本身只能改变预览的角度
     * previewFrameCallback以及拍摄出来的照片是不会发生改变的，拍摄出来的照片角度依旧不正常的
     * 拍摄的照片需要自行处理
     * 这里Nexus5X的相机简直没法吐槽，后置摄像头倒置了，切换摄像头之后就出现问题了。
     * @param activity
     */
    private fun calculateCameraPreviewOrientation(activity: Activity, cameraId: Int): Int {
        return changeOrientation(activity,cameraId)
    }

    private fun calculateCameraPreviewOrientation(activity: Activity) : Int{
        return changeOrientation(activity,-1)
    }

    private fun changeOrientation(activity: Activity,cameraId : Int) : Int{
        val info = Camera.CameraInfo()
        if(cameraId == -1){
            Camera.getCameraInfo(CameraParam.INSTANCE.cameraId, info)
        } else {
            Camera.getCameraInfo(cameraId, info)
        }
        val rotation = activity.windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        CameraParam.INSTANCE.orientation = result
        return result
    }

    /**
     * 设置预览Surface
     * @param texture
     */
    fun setPreviewSurface(texture: SurfaceTexture) {
        try {
            mCamera?.setPreviewTexture(texture)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 开始预览
     */
    fun startPreview() {
        mCamera?.startPreview()
    }

    /**
     * 停止预览
     */
    fun stopPreview() {
        mCamera?.stopPreview()
    }

    /**
     * 检查摄像头(前置/后置)是否支持闪光灯
     * @param camera   摄像头
     * @return
     */
    fun checkSupportFlashLight(camera: Camera?): Boolean {
        if (camera == null) {
            return false
        }

        val parameters = camera.parameters

        return checkSupportFlashLight(parameters)
    }

    /**
     * 检查摄像头(前置/后置)是否支持闪光灯
     * @param parameters 摄像头参数
     * @return
     */
    fun checkSupportFlashLight(parameters : Camera.Parameters?) : Boolean{
        if(parameters?.flashMode == null) return false

        val supportedFlashModes = parameters?.supportedFlashModes
        if(supportedFlashModes == null
                || supportedFlashModes.isEmpty()
                || (supportedFlashModes.size == 1 && supportedFlashModes[0] == Camera.Parameters.FLASH_MODE_OFF)){
            return false
        }
        return true
    }

    /**
     * 选择合适的FPS
     * @param parameters
     * @param expectedThoudandFps 期望的FPS
     * @return
     */
    fun chooseFixedPreviewFps(parameters: Camera.Parameters, expectedThoudandFps: Int): Int {
        val supportedFps = parameters.supportedPreviewFpsRange
        for (entry in supportedFps) {
            if (entry[0] == entry[1] && entry[0] == expectedThoudandFps) {
                parameters.setPreviewFpsRange(entry[0], entry[1])
                return entry[0]
            }
        }
        val temp = IntArray(2)
        val guess: Int
        parameters.getPreviewFpsRange(temp)
        guess = if (temp[0] == temp[1]) {
            temp[0]
        } else {
            temp[1] / 2
        }
        return guess
    }

    /**
     * 计算最完美的Size
     * @param sizes
     * @param expectWidth
     * @param expectHeight
     * @return
     */
    private fun calculatePerfectSize(sizes: List<Camera.Size>, expectWidth: Int,
                                     expectHeight: Int, calculateType: CalculateType): Camera.Size {
        sortList(sizes) // 根据宽度进行排序

        // 根据当前期望的宽高判定
        val bigEnough = ArrayList<Camera.Size>()
        val noBigEnough = ArrayList<Camera.Size>()
        for (size in sizes) {
            if (size.height * expectWidth / expectHeight == size.width) {
                if (size.width > expectWidth && size.height > expectHeight) {
                    bigEnough.add(size)
                } else {
                    noBigEnough.add(size)
                }
            }
        }
        // 根据计算类型判断怎么如何计算尺寸
        var perfectSize: Camera.Size? = null
        when (calculateType) {
            // 直接使用最小值
            CalculateType.Min ->
                // 不大于期望值的分辨率列表有可能为空或者只有一个的情况，
                // Collections.min会因越界报NoSuchElementException
                if (noBigEnough.size > 1) {
                    perfectSize = Collections.min(noBigEnough, CompareAreaSize())
                } else if (noBigEnough.size == 1) {
                    perfectSize = noBigEnough[0]
                }

            // 直接使用最大值
            CalculateType.Max ->
                // 如果bigEnough只有一个元素，使用Collections.max就会因越界报NoSuchElementException
                // 因此，当只有一个元素时，直接使用该元素
                if (bigEnough.size > 1) {
                    perfectSize = Collections.max(bigEnough, CompareAreaSize())
                } else if (bigEnough.size == 1) {
                    perfectSize = bigEnough[0]
                }

            // 小一点
            CalculateType.Lower ->
                // 优先查找比期望尺寸小一点的，否则找大一点的，接受范围在0.8左右
                if (noBigEnough.size > 0) {
                    val size = Collections.max(noBigEnough, CompareAreaSize())
                    if (size.width.toFloat() / expectWidth >= 0.8 && size.height.toFloat() / expectHeight > 0.8) {
                        perfectSize = size
                    }
                } else if (bigEnough.size > 0) {
                    val size = Collections.min(bigEnough, CompareAreaSize())
                    if (expectWidth.toFloat() / size.width >= 0.8 && (expectHeight / size.height).toFloat() >= 0.8) {
                        perfectSize = size
                    }
                }

            // 大一点
            CalculateType.Larger ->
                // 优先查找比期望尺寸大一点的，否则找小一点的，接受范围在0.8左右
                if (bigEnough.size > 0) {
                    val size = Collections.min(bigEnough, CompareAreaSize())
                    if (expectWidth.toFloat() / size.width >= 0.8 && (expectHeight / size.height).toFloat() >= 0.8) {
                        perfectSize = size
                    }
                } else if (noBigEnough.size > 0) {
                    val size = Collections.max(noBigEnough, CompareAreaSize())
                    if (size.width.toFloat() / expectWidth >= 0.8 && size.height.toFloat() / expectHeight > 0.8) {
                        perfectSize = size
                    }
                }
        }
        // 如果经过前面的步骤没找到合适的尺寸，则计算最接近expectWidth * expectHeight的值
        if (perfectSize == null) {
            var result: Camera.Size = sizes[0]
            var widthOrHeight = false // 判断存在宽或高相等的Size
            // 辗转计算宽高最接近的值
            for (size in sizes) {
                // 如果宽高相等，则直接返回
                if (size.width == expectWidth && size.height == expectHeight
                        && size.height.toFloat() / size.width.toFloat() == CameraParam.INSTANCE.currentRatio) {
                    result = size
                    break
                }
                // 仅仅是宽度相等，计算高度最接近的size
                if (size.width == expectWidth) {
                    widthOrHeight = true
                    if (abs(result.height - expectHeight) > abs(size.height - expectHeight) && size.height.toFloat() / size.width.toFloat() == CameraParam.INSTANCE.currentRatio) {
                        result = size
                        break
                    }
                } else if (size.height == expectHeight) {
                    widthOrHeight = true
                    if (abs(result.width - expectWidth) > abs(size.width - expectWidth) && size.height.toFloat() / size.width.toFloat() == CameraParam.INSTANCE.currentRatio) {
                        result = size
                        break
                    }
                } else if (!widthOrHeight) {
                    if (abs(result.width - expectWidth) > abs(size.width - expectWidth)
                            && abs(result.height - expectHeight) > abs(size.height - expectHeight)
                            && size.height.toFloat() / size.width.toFloat() == CameraParam.INSTANCE.currentRatio) {
                        result = size
                    }
                }// 如果之前的查找不存在宽或高相等的情况，则计算宽度和高度都最接近的期望值的Size
                // 高度相等，则计算宽度最接近的Size
            }
            perfectSize = result
        }
        return perfectSize
    }

    /**
     * 比较器
     */
    private class CompareAreaSize : Comparator<Camera.Size> {
        override fun compare(pre: Camera.Size, after: Camera.Size): Int {
            return java.lang.Long.signum(pre.width.toLong() * pre.height - after.width.toLong() * after.height)
        }
    }

    /**
     * 分辨率由大到小排序
     * @param list
     */
    private fun sortList(list: List<Camera.Size>) {
        Collections.sort(list, CompareAreaSize())
    }
}

















