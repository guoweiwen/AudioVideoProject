package com.bonade.fakedouyin.widget;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;


import com.bonade.fakedouyin.renderer.RecordRenderer;
import com.bonade.fakedouyin.utils.DensityKt;

import java.io.IOException;
import java.util.List;

/**
 * 管理摄像头的类
 * */
public class MyCamera {
    private static final String TAG = "MyCamera";

    private Camera camera;
    private SurfaceTexture surfaceTexture;


    private int width;
    private int height;

    public MyCamera(Context context){
        //获取屏幕宽高
        this.width = DensityKt.getScreenResolution()[0];
        this.height = DensityKt.getScreenResolution()[1];
    }

    private RecordRenderer recordRenderer;
    public void initCamera(SurfaceTexture surfaceTexture, int cameraId,RecordRenderer recordRenderer){
        this.surfaceTexture = surfaceTexture;
        this.recordRenderer = recordRenderer;
        setCameraParm(cameraId,recordRenderer);
    }

    private void setCameraParm(int cameraId,RecordRenderer recordRenderer){
        try {
            camera = Camera.open(cameraId);
            //将预览的纹理给 surfaceTexture
            camera.setPreviewTexture(surfaceTexture);
            Camera.Parameters parameters = camera.getParameters();

            parameters.setFlashMode("off");
            parameters.setPreviewFormat(ImageFormat.NV21);

            Camera.Size size = getFitSize(parameters.getSupportedPictureSizes());
            parameters.setPictureSize(size.width, size.height);

            size = getFitSize(parameters.getSupportedPreviewSizes());
            parameters.setPreviewSize(size.width, size.height);

            if(recordRenderer != null){
                recordRenderer.setTextureSize(width,height);
            }

            camera.setParameters(parameters);
            camera.setDisplayOrientation(90);
            camera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPreview(){
        if(camera != null){
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void changeCamera(int cameraId) {
        if(camera != null){
            stopPreview();
        }
        setCameraParm(cameraId,recordRenderer);
    }

    private Camera.Size getFitSize(List<Camera.Size> sizes){
        //将宽高置换，因为摄像头的宽高信息是调换的
        if(width < height) {
            int t = height;
            height = width;
            width = t;
        }

        for(Camera.Size size : sizes) {
            if(1.0f * size.width / size.height == 1.0f * width / height)
            {
                //获取等同于屏幕的宽高，找不到即选第一个
                return size;
            }
        }
        return sizes.get(0);
    }
}
