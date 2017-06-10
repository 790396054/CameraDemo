package com.hdtytech.camerademo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by gmm on 2017/6/10.
 */

public class FaceView extends View {
    private Paint mPaint;

    private Camera.Face[] mFaces; // 放所有检测到的人类的数据

    private int numFaces; // 检测到的人脸数

    private Matrix mMatrix;

    private RectF mRectF;

    public void setFaces(Camera.Face[] faces, int numFaces) {
        mFaces = faces;
        this.numFaces = numFaces;
        // 刷新页面
        invalidate();
    }

    public FaceView(Context context) {
        super(context);
        init();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5f);

        mMatrix = new Matrix();
        mRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制检查出来的人脸
        if (mFaces == null || mFaces.length < 1) {
            return;
        }

        prepareMatrix(mMatrix, false, 90, getWidth(), getHeight());
        canvas.save();
        mMatrix.postRotate(0);
        canvas.rotate(- 0);

        for (int i = 0; i < mFaces.length; i++) {
            mRectF.set(mFaces[i].rect); // 把人脸矩阵设置到 rectF 中
            mMatrix.mapRect(mRectF);

            // 画矩形框，用检测并通过 matrix 转换之后的 reactf 绘制
            canvas.drawRect(mRectF, mPaint);
        }


        canvas.restore();
    }

    /**
     * 用于将返回的人脸矩阵转换为正常在取景区显示的矩阵
     * @param matrix
     * @param isMirror
     * @param displayOriention
     * @param viewWidth
     * @param viewHeight
     */
    private void prepareMatrix(Matrix matrix, boolean isMirror, int displayOriention,
                               int viewWidth, int viewHeight){
        matrix.setScale(isMirror?-1:1,1);
        matrix.postRotate(displayOriention);
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }
}
