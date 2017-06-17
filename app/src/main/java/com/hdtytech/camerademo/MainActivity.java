package com.hdtytech.camerademo;

import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CameraInterface.OpenCameraCallBack, CameraInterface.TakePictureCallBack {

    private Button mTakePicture; // 拍照

    private SurfaceHolder mHolder;

    private FaceView mFaceView;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 100) {
                Camera.Face[] faces = (Camera.Face[]) msg.obj;
                // 将检测到的人脸数组设置到视图组件中
                mFaceView.setFaces(faces, faces.length);
            }

            return false;
        }
    });

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

        mFaceView = (FaceView) findViewById(R.id.faceView);

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
        String path = Environment.getExternalStorageDirectory() + "/share.png";
        CameraInterface.getsInstance().doTakePicture(path, this);
    }

    @Override
    public void hasOpenCamera() {
        // 说明我的摄像头已经可以正常打开
        CameraInterface.getsInstance().initCameraParameter();
        CameraInterface.getsInstance().setPreviewDisplay(mHolder);
        CameraInterface.getsInstance().setFaceDetectionListener(new GoogleFaceDetection(mHandler));
        CameraInterface.getsInstance().doStartPreview();
    }

    @Override
    public void hasTakePicture(String path) {
        Toast.makeText(this, "拍照成功，保存路径" + path, Toast.LENGTH_SHORT).show();
        hasOpenCamera();
    }
}
