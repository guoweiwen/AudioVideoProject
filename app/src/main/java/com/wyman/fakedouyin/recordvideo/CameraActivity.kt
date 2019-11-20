package com.wyman.fakedouyin.recordvideo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.wyman.fakedouyin.BaseActivity
import com.wyman.fakedouyin.R
import com.wyman.fakedouyin.common.callback.PermissionListener
import com.wyman.fakedouyin.presenter.RecordPresenter
import com.wyman.fakedouyin.renderer.RecordRenderer
import com.wyman.fakedouyin.widget.*
import com.wyman.fakedouyin.widget.horizontal_mul_button.HorizontalMul
import io.fotoapparat.Fotoapparat
import io.fotoapparat.log.fileLogger
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.selector.back
import io.fotoapparat.view.CameraView
import kotlinx.android.synthetic.main.activity_camera.*

/**
 * Created by wyman
 * on 2019-07-03.
 */
class CameraActivity : BaseActivity() ,RoundFrameLayout.OnTouchScroller{
    lateinit var cameraView : CameraView
    lateinit var recordedButton: RecordedButton
    lateinit var mulButton: HorizontalMul
    lateinit var glSurfaceview: GLSurfaceView
    lateinit var cameraFramelayout: RoundFrameLayout
    lateinit var testView_ : TestView
    lateinit var moveView_ : View
    lateinit var recordView : GLRecordView
    lateinit var myCameraView : MyCameraView
    private var fotoapparat : Fotoapparat? = null

    private var mRenderer: RecordRenderer? = null
    private var mPresenter: RecordPresenter? = null

//    var offsetX = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        transparentStatusBar()
        refreshPermissionStatus()
//        recordView = gl_record_view
//        myCameraView = my_cmeraview
        cameraView = camera_view
//        recordedButton = recorded_button
        mulButton = mul_button
//        glSurfaceview = gl_Surfaceview
//        glSurfaceview.setRenderer(SlidingRender())
//        cameraFramelayout = camera_framelayout
//        cameraFramelayout.setOnTouchScroller(this)
//        testView_ = testView
//        moveView_ = move_view


//    mPresenter.setRecordSeconds(15)


//         测试代码
        var currentBtn = 0
//        cameraView.setOnClickListener {
//            val intent = Intent(CameraActivity@this,ChossiceMusicActivity::class.java)
//            startActivity(intent)
//            mulButton.min()
//            if(currentBtn > 3){
//                currentBtn = 0
//            }
//            mulButton.scrollToSpecify(currentBtn)
//            currentBtn++
//        }
    /*
    * 滑动替换滤镜流程：
    *    1、向右滑动，替换滤镜
    *       过屏幕一半，替换滤镜，不过一半不替换
    *    2、向左滑动，替换之前滤镜，如果没有就不替换
    * */
//        testView_.addOnTouchScroller(object : TestView.OnTouchScroller{
//            override fun swipeLeftOrRight(distanceX: Float) {
//                if(distanceX > 0){
//                    //左滑
//                    var offsetX = -10
//                    //这种是View本身移动
//                    Log.e(TAG,"distanceX:$distanceX")
//                    Log.e(TAG,"left:${moveView_.left}")
//                    Log.e(TAG,"right:${moveView_.right}")
//                    moveView_.layout(
//                            moveView_.left + offsetX,
//                            moveView_.top ,
//                            moveView_.right + offsetX,
//                            moveView_.bottom )
//                } else {
//                    //右滑
//                    var offsetX = 10
//                    Log.e(TAG,"distanceX:$distanceX")
//                    Log.e(TAG,"left:${moveView_.left}")
//                    Log.e(TAG,"right:${moveView_.right}")
//                    //这种是View本身移动
//                    moveView_.layout(
//                            moveView_.left + offsetX,
//                            moveView_.top ,
//                            moveView_.right + offsetX,
//                            moveView_.bottom )
//                }
//            }
//
//            override fun swipeBack() {
//                Log.e(TAG,"swipeBack")
//            }
//
//            override fun swipeFrontal() {
//                Log.e(TAG,"swipeFrontal")
//            }
//
//            override fun swipeUpper(startInLeft: Boolean, distance: Float) {
//                Log.e(TAG,"swipeUpper")
//            }
//
//            override fun swipeDown(startInLeft: Boolean, distance: Float) {
//                Log.e(TAG,"swipeDown")
//            }
//
//        })


//        mulButton.setCallBackListener(object : HorizontalMul.CallBackListener{
//            override fun callBack(currentBtn: Int) {
//                TODO
//            }
//        })
            //横向按钮横滑回调（等同上面的java接口）
            mulButton.callBackListener = {
                when(it){
                    HorizontalMul.TAKE_PHOTO -> {
                        toast("拍照")
                    }
                    HorizontalMul.VIDEO_15S -> {
                        toast("拍15秒")
                    }
                    HorizontalMul.VIDEO_60S -> {
                        toast("拍60秒")
                    }
                    HorizontalMul.VIDEO_ALBUM -> {
                        toast("图集")
                    }
                }
            }
    }

    // ----------------------------------------- 切换滤镜 -------------------------------------------
    private var mFilterIndex: Int = 0
    private val mTouchScroller = object : GLRecordView.OnTouchScroller{
        override fun swipeBack() {
            mFilterIndex++
//            if (mFilterIndex >= FilterHelper.getFilterList().size()) {
//                mFilterIndex = 0
//            }
//            changeDynamicFilter(mFilterIndex)
        }

        override fun swipeFrontal() {
            mFilterIndex--
//            if (mFilterIndex < 0) {
//                val count = FilterHelper.getFilterList().size()
//                mFilterIndex = if (count > 0) count - 1 else 0
//            }
//            changeDynamicFilter(mFilterIndex)
        }

        override fun swipeUpper(startInLeft: Boolean, distance: Float) {

        }

        override fun swipeDown(startInLeft: Boolean, distance: Float) {

        }
    }



    /**
     * 刷新画面
     */
    fun onFrameAvailable() {
//        recordView.requestRender()
    }

    override fun moveRight(moveX:Float,moveY:Float) {

    }

    override fun moveLeft(moveX:Float,moveY:Float) {
        Log.e(TAG,"moveLeft")
        glSurfaceview.x = moveX
    }

    fun toast(text : String){
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show()
    }

    private fun refreshPermissionStatus() {
        handlePermission(arrayOf(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
                ),object : PermissionListener{

            override fun onGranted() {
                permissionGranted()
            }

            override fun onDenied(deniedPermissions: List<String>) {
                //无法获取权限 退出当前activity
//                finish()
            }
        })
    }

    var flag : Boolean = true
    override fun permissionGranted() {
//        initCamera()
//        fotoapparat!!.start()
        if(flag){
//            mPresenter = RecordPresenter(this)
//            mRenderer = RecordRenderer(mPresenter!!)

//            recordView.onResume()
//            mPresenter?.onResume()

//            recordView.setEGLContextClientVersion(3)
//            recordView.setRenderer(mRenderer)
//            recordView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY)
//            recordView.addOnTouchScroller(mTouchScroller)
//            myCameraView.initConfigure()

            flag = false
        }
    }

    fun checkPermission(){
        if(checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA), 100)
        }
    }

    fun initCamera(){
        if(fotoapparat == null){
            fotoapparat = Fotoapparat(
               context = this,
               view = cameraView,
               lensPosition = back(),
                    
                logger = loggers(
                    logcat(),
                    fileLogger(this)
                ),
                cameraErrorCallback = { error ->
                    Toast.makeText(this as Context,error.message,Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
//        handleFullScreen()
//        recordView.onResume()

//        mPresenter?.setAudioEnable(PermissionUtils.permissionChecking(this, Manifest.permission.RECORD_AUDIO))
    }

    override fun onStart() {
        super.onStart()
//        refreshPermissionStatus()
    }

    override fun onStop() {
        super.onStop()
        if(fotoapparat != null){
            fotoapparat!!.stop()
        }
    }

    override fun onPause() {
        super.onPause()
//        recordView.onPause()
        mPresenter?.onPause()
    }

    var f : Fotoapparat? = null
    override fun onDestroy() {
        mPresenter?.release()
        mPresenter = null
        super.onDestroy()

        f = Fotoapparat(this as Context,cameraView)
    }

    companion object{
        private const val TAG = "CameraActivity"
    }

















}
