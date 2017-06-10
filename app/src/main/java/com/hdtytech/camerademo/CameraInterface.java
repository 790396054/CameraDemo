package com.hdtytech.camerademo;

import android.hardware.Camera;
import android.hardware.Camera.*;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

/**
 * Created by gmm on 2017/6/10.
 * 相机工具类，提供取景，拍照等功能
 */

public class CameraInterface {

    private Camera mCamera;

    private static CameraInterface sInstance;
    private boolean isPreview;

    public static synchronized CameraInterface getsInstance(){
        if (sInstance == null) {
            sInstance = new CameraInterface();
        }
        return sInstance;
    }

    private  CameraInterface(){}

    /**
     * 打开摄像头
     */
    public void doOpenCamera(OpenCameraCallBack callBack){
        mCamera = Camera.open();
        if (callBack != null) {
            callBack.hasOpenCamera();
        }
    }

    /**
     * 对摄像头进行一些初始化参数的设置
     */
    public void initCameraParameter(){
        Parameters parameters = mCamera.getParameters();

        /**设置拍照生成图片的Size*/
        Size pictureSizes = getPictureSize(parameters);
        parameters.setPictureSize(pictureSizes.width, pictureSizes.height);

        /**设置取景区的 Size*/
        Size previewSize = getPreviewSize(parameters);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        parameters.setFocusMode(getFocusMode(parameters));

        mCamera.setParameters(parameters);

        mCamera.setDisplayOrientation(90);
    }

    /**
     * 设置取景区的一个载体
     * @param holder 从 SurfaceView 中获得的 SurfaceHolder
     */
    public void setPreviewDisplay(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始取景区的预览
     */
    public void doStartPreview(){
        if (isPreview) {
            mCamera.stopPreview();
        }
        mCamera.startPreview();
        isPreview = true;
    }

    /**
     * 关闭摄像头的操作
     */
    public void closeCamera(){
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            isPreview = false;
        }
    }

    /**
     * 返回自动对焦的模式
     * @param parameters
     * @return
     */
    private String getFocusMode(Parameters parameters) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains("continuous_video")) {
            return Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
        }else {
            return Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
        }
    }

    /**
     * 获得中间支持的取景区的 Size
     * @param parameters
     * @return
     */
    private Size getPreviewSize(Parameters parameters) {
        List<Size> previewSizes = parameters.getSupportedPreviewSizes();
        return previewSizes.get(previewSizes.size() / 2);
    }

    /**
     * 获得中间支持的 Size
     * @param parameters
     * @return
     */
    private Size getPictureSize(Parameters parameters){
        List<Size> pictureSizes = parameters.getSupportedPictureSizes();
        return pictureSizes.get(pictureSizes.size() / 2);
    }

    public interface OpenCameraCallBack{
        // 说明我的摄像头已经可以正常打开
        void hasOpenCamera();
    }
}

