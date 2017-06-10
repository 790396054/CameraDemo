package com.hdtytech.camerademo;

import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CameraInterface.OpenCameraCallBack {

    private Button mTakePicture; // 拍照

    private SurfaceHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mTakePicture = (Button) findViewById(R.id.takePicture);
        mTakePicture.setOnClickListener(this);

        CameraSurfaceView surfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mHolder = surfaceView.getHolder();

        /***/
        new Thread(new Runnable() {
            @Override
            public void run() {
                CameraInterface.getsInstance().doOpenCamera(MainActivity.this);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        // 执行拍照
    }

    @Override
    public void hasOpenCamera() {
        // 说明我的摄像头已经可以正常打开
        CameraInterface.getsInstance().initCameraParameter();
        CameraInterface.getsInstance().setPreviewDisplay(mHolder);
        CameraInterface.getsInstance().doStartPreview();
    }
}
