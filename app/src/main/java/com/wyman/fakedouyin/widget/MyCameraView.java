package com.wyman.fakedouyin.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.wyman.fakedouyin.renderer.RecordRenderer;


/**
 * 关联摄像头与 GLSurfaceView 的类
 * */
public class MyCameraView extends GLSurfaceView implements RecordRenderer.OnFrameAvailableListener{
    private static final String TAG = "MyCameraView";

//    private MyCameraRender cameraRender;
    private RecordRenderer recordRenderer;
    private MyCamera myCamera;
    private Context context;

    //默认开启后置摄像头
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    //共享的纹理Id
    private int textureId = -1;
    //共享纹理包装类

    public MyCameraView(Context context) {
        this(context, null);
    }

    public MyCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
//        cameraRender = new MyCameraRender(context);
//        //初始化 共享纹理包装类对象
//        shareTextureBean = new ShareTextureBean();
        //设置opengl版本为2.0
        setEGLContextClientVersion(3);
        recordRenderer = new RecordRenderer(context);
        recordRenderer.setOnFrameAvailableListener(this);
    }

    /**
     * 获取共享纹理包装类对象
     * */
//    public ShareTextureBean getShareTextureBean(){
//        if(shareTextureBean != null){
//            return shareTextureBean;
//        }
//        return null;
//    }

    public void initConfigure(){
//        setRender(cameraRender);
        setRenderer(recordRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
//        previewAngle(context);
        myCamera = new MyCamera(context);
//        cameraRender.setOnSurfaceCreateListener(new MyCameraRender.OnSurfaceCreateListener() {
//            @Override
//            public void onSurfaceCreate(SurfaceTexture surfaceTexture, int tid) {
//                myCamera.initCamera(surfaceTexture, cameraId,frameProcessor);
//                textureId = tid;
////                shareTextureBean.setSharingTextureId(tid);
////                Log.e(TAG,"textureId:" + textureId);
//            }
//        });
        recordRenderer.setOnSurfaceCreateListener(new RecordRenderer.OnSurfaceCreateListener() {
            @Override
            public void onSurfaceCreate(SurfaceTexture surfaceTexture) {
                myCamera.initCamera(surfaceTexture,cameraId,recordRenderer);
            }
        });
    }

    public void onDestory() {
        if(myCamera != null) {
            myCamera.stopPreview();
        }
    }

    /**
     * 计算imageView 的宽高
     * 已经放到 MyCamera 的 setCameraParm() 方法
     */
    private void calculateImageSize() {
//        int width;
//        int height;
//        if (CameraParam.getInstance().orientation == 90 || CameraParam.getInstance().orientation == 270) {
//            width = CameraParam.getInstance().previewHeight;
//            height = CameraParam.getInstance().previewWidth;
//        } else {
//            width = CameraParam.getInstance().previewWidth;
//            height = CameraParam.getInstance().previewHeight;
//        }
//        mVideoParams.setVideoSize(width, height);
//        mActivity.updateTextureSize(width, height);
    }

    //MyCameraRender 需要使用的方法
//    public void previewAngle(Context context) {
//        int angle = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
//        cameraRender.resetMatrix();
//        switch (angle) {
//            case Surface.ROTATION_0:
//                Log.d(TAG, "0");
//                if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    cameraRender.setAngle(90, 0, 0, 1);
//                    if(shareTextureBean != null){
//                        shareTextureBean.setAngleDegrees(90);
//                        shareTextureBean.setXaxis(0);
//                        shareTextureBean.setYaxis(0);
//                        shareTextureBean.setZaxis(1);
//                    }
//                    cameraRender.setAngle(180, 1, 0, 0);
//                    if(shareTextureBean != null){
//                        shareTextureBean.setAngleDegreesSecond(180);
//                        shareTextureBean.setXaxisSecond(1);
//                        shareTextureBean.setYaxisSecond(0);
//                        shareTextureBean.setZaxisSecond(0);
//                    }
//                } else {
//                    cameraRender.setAngle(90f, 0f, 0f, 1f);
//                }
//                break;
//            case Surface.ROTATION_90:
//                Log.d(TAG, "90");
//                if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    cameraRender.setAngle(180, 0, 0, 1);
//                    cameraRender.setAngle(180, 0, 1, 0);
//                } else {
//                    cameraRender.setAngle(90f, 0f, 0f, 1f);
//                }
//                break;
//            case Surface.ROTATION_180:
//                Log.d(TAG, "180");
//                if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    cameraRender.setAngle(90f, 0.0f, 0f, 1f);
//                    cameraRender.setAngle(180f, 0.0f, 1f, 0f);
//                } else {
//                    cameraRender.setAngle(-90, 0f, 0f, 1f);
//                }
//                break;
//            case Surface.ROTATION_270:
//                Log.d(TAG, "270");
//                if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    cameraRender.setAngle(180f, 0.0f, 1f, 0f);
//                } else {
//                    cameraRender.setAngle(0f, 0f, 0f, 1f);
//                }
//                break;
//        }
//    }

    public RecordRenderer getRender() {
//        return cameraRender;
        return recordRenderer;
    }

    public int getTextureId(){
        return textureId;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }
}
