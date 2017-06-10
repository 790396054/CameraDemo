package com.hdtytech.camerademo;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

/**
 * Created by gmm on 2017/6/10.
 */

public class GoogleFaceDetection implements Camera.FaceDetectionListener {

    private Handler mHandler;

    public GoogleFaceDetection(Handler handler){
        this.mHandler = handler;
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        // 将检测到的人脸数组返回
        Message message = mHandler.obtainMessage();
        message.what = 100;
        message.obj = faces;
        message.sendToTarget();
    }
}
