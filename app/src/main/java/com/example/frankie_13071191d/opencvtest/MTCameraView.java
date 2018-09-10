package com.example.frankie_13071191d.opencvtest;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.JavaCameraView;

import android.R.integer;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;

import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

public class MTCameraView extends JavaCameraView implements AutoFocusCallback  {
    Context mContext;
    public MTCameraView(Context context, AttributeSet  attrs) {
        super(context, attrs);
        mContext=context;
        // TODO Auto-generated constructor stub
        mCamera = Camera.open(0);
    }
    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }
    private  Handler handler;
    public void setFocusMode(Context item, int type) {
        handler = new Handler(item.getMainLooper());
        Camera.Parameters params = mCamera.getParameters();
        List<String> FocusModes = params.getSupportedFocusModes();

        switch (type) {
            case 0:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    Toast.makeText(item, "Continuous Mode", Toast.LENGTH_SHORT).show();}
                else
                    Toast.makeText(item, "Continuous Mode is not supported", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)){
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                    Toast.makeText(item, "Infinity Mode", Toast.LENGTH_SHORT).show();}
                else
                    Toast.makeText(item, "Infinity Mode is not supported", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_MACRO)){
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                    Toast.makeText(item, "Macro Mode", Toast.LENGTH_SHORT).show();}
                else
                    Toast.makeText(item, "Macro Mode is not supported", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                if (FocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
                    params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    Toast.makeText(item, "FOCUS_MODE", Toast.LENGTH_SHORT).show();}
                else
                    Toast.makeText(item, "FOCUS_MODE is not supported", Toast.LENGTH_SHORT).show();
                break;
        }
        disconnectCamera();
        connectCamera(getWidth(), getHeight());
        mCamera.setParameters(params);
    }
    public Camera.Size getResolution() {
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size s = params.getPreviewSize();
        return s;
    }


    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x <min) {
            return min;
        }
        return x;
    }
    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / getResolution().width - 1000);
        int centerY = (int) (y / getResolution().height - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    public void focusOnTouch(MotionEvent event) {
        Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY(), 1f);
        Rect meteringRect = calculateTapArea(event.getRawX(), event.getRawY(), 1.5f);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        if (parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            focusAreas.add(new Camera.Area(focusRect, 1000));
            parameters.setFocusAreas(focusAreas);
        }

        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
            meteringAreas.add(new Camera.Area(meteringRect, 1000));
            parameters.setMeteringAreas(meteringAreas);
        }

        mCamera.setParameters(parameters);
        mCamera.autoFocus(this);
    }
    public void onAutoFocus(boolean success, Camera camera) {
    }
}