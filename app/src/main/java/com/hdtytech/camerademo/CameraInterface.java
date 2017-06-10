package com.hdtytech.camerademo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.*;
import android.view.SurfaceHolder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by gmm on 2017/6/10.
 * 相机工具类，提供取景，拍照等功能
 */

public class CameraInterface {

    private Camera mCamera;

    private static CameraInterface sInstance;
    private boolean isPreview;
    private String savePath; // 存储拍照完毕后存储照片的路径

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
        mCamera = Camera.open(1);
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

        // 设置预览的方向
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

        // 支持人脸检测
        if (mCamera.getParameters().getMaxNumDetectedFaces() > 0) {
            // 开启人脸检测
            mCamera.startFaceDetection();
        }
    }

    public void doTakePicture(String path, final TakePictureCallBack callBack) {
        savePath = path;
        mCamera.takePicture(null, null, new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                save(data);//savePicture(data);
                if (callBack != null) {
                    callBack.hasTakePicture(savePath);
                }
            }
        });
    }

    /**
     * 关闭摄像头的操作
     */
    public void closeCamera(){
        if (mCamera != null) {
            if (mCamera.getParameters().getMaxNumDetectedFaces() > 0) {
                // 停止人脸检测
                mCamera.stopFaceDetection();
            }
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            isPreview = false;
        }
    }

    /**
     * 设置人脸检测监听
     * @param listener
     */
    public void setFaceDetectionListener(FaceDetectionListener listener){
        mCamera.setFaceDetectionListener(listener);
    }

    /**
     * 根据字节数组保存照片到文件中
     * @param bytes
     */
    private String savePicture(byte[] bytes) {
        try {
            FileOutputStream fos = new FileOutputStream(savePath);
            fos.write(bytes);
            fos.flush();
            fos.close();
            return savePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    public void save(byte[] data){
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix = new Matrix();
        matrix.setRotate(270);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        try {
            FileOutputStream fos = new FileOutputStream(savePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得中间支持的 Size
     * @param parameters
     * @return
     */
    private Size getPictureSize(Parameters parameters){
        List<Size> pictureSizes = parameters.getSupportedPictureSizes();
        return pictureSizes.get(0);
    }

    public interface OpenCameraCallBack{
        // 说明我的摄像头已经可以正常打开
        void hasOpenCamera();
    }

    /**
     * 拍照成功后的回调接口
     */
    public interface TakePictureCallBack{
        void hasTakePicture(String path);
    }
}

