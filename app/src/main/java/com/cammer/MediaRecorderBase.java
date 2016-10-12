package com.cammer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.tools.ToolsDevice;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Fang Ruijiao on 2016/10/11.
 */

public class MediaRecorderBase implements SurfaceHolder.Callback {
    protected final String TAG = "FRJ";

    protected Activity mCon;
    /** 摄像头对象 */
    protected Camera mCamera;
    /** 摄像头参数 */
    protected Camera.Parameters mParameters = null;
    /** 画布 */
    protected  SurfaceHolder mSurfaceHolder;

    /** 状态标记 */
    private boolean previewRunning,mStartPreview;

    /** 摄像头类型（前置/后置），默认后置 */
    protected int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    /** 最大帧率 */
    public static final int MAX_FRAME_RATE = 25;
    /** 最小帧率 */
    public static final int MIN_FRAME_RATE = 15;
    /** 帧率 */
    protected int mFrameRate = MIN_FRAME_RATE;

    protected int previewW = 640,previewH = 480;

    public MediaRecorderBase(Activity con,SurfaceHolder surfaceHolder){
        mCon = con;
        mSurfaceHolder = surfaceHolder;
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
            mCamera = Camera.open();
        else
            mCamera = Camera.open(mCameraId);

        if (mCamera != null){
            //设置摄像头参数
            mParameters = mCamera.getParameters();
            prepareCameraParaments();
            mCamera.setParameters(mParameters);
        }
        else {
            Toast.makeText(mCon, "Camera not available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (previewRunning){
            mCamera.stopPreview();
        }
        Camera.Parameters p = mCamera.getParameters();
//        List<Camera.Size> list = p.getSupportedPreviewSizes();
//        List<Camera.Size> list = p.getSupportedPreviewSizes();
//        for(Camera.Size size : list){
//            if(size.height / 9.0 * 16 == size.width){
//                previewH = size.height;
//                previewW = size.width;
//                Log.i("FRJ","previewH:" + previewH);
//                Log.i("FRJ","previewW:" + previewW);
//            }
//        }
        p.setPreviewSize(previewW,previewH);// 16:9
        p.setPreviewFormat(ImageFormat.NV21);
        mCamera.setParameters(p);

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            previewRunning = true;
        }
        catch (IOException e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
        previewRunning = false;
        mCamera.release();
    }

    /** 开始预览 */
    public void startPreview() {
        if (mStartPreview || mSurfaceHolder == null)
            return;
        else
            mStartPreview = true;

        try {

            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                mCamera = Camera.open();
            else
                mCamera = Camera.open(mCameraId);

//			camera.setDisplayOrientation(90);
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //设置摄像头参数
            mParameters = mCamera.getParameters();
            prepareCameraParaments();
            mCamera.setParameters(mParameters);
            setPreviewCallback();
            mCamera.startPreview();

            onStartPreviewSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 预览调用成功，子类可以做一些操作 */
    protected void onStartPreviewSuccess() {

    }

    /** 设置回调 */
    protected void setPreviewCallback() {
        Camera.Size size = mParameters.getPreviewSize();
        if (size != null) {
            PixelFormat pf = new PixelFormat();
            PixelFormat.getPixelFormatInfo(mParameters.getPreviewFormat(), pf);
            int buffSize = size.width * size.height * pf.bitsPerPixel / 8;
            try {
                mCamera.addCallbackBuffer(new byte[buffSize]);
                mCamera.addCallbackBuffer(new byte[buffSize]);
                mCamera.addCallbackBuffer(new byte[buffSize]);
//                mCamera.setPreviewCallbackWithBuffer(this);
            } catch (OutOfMemoryError e) {
                Log.e("Yixia", "startPreview...setPreviewCallback...", e);
            }
            Log.e("Yixia", "startPreview...setPreviewCallbackWithBuffer...width:" + size.width + " height:" + size.height);
        } else {
//            mCamera.setPreviewCallback(this);
        }
    }

    /**
     * 预处理一些拍摄参数
     * 注意：自动对焦参数cam_mode和cam-mode可能有些设备不支持，导致视频画面变形，需要判断一下，已知有"GT-N7100", "GT-I9308"会存在这个问题
     */
    @SuppressWarnings("deprecation")
    protected void prepareCameraParaments() {
        if (mParameters == null)
            return;

        List<Integer> rates = mParameters.getSupportedPreviewFrameRates();
        if (rates != null) {
            if (rates.contains(MAX_FRAME_RATE)) {
                mFrameRate = MAX_FRAME_RATE;
            } else {
                Collections.sort(rates);
                for (int i = rates.size() - 1; i >= 0; i--) {
                    if (rates.get(i) <= MAX_FRAME_RATE) {
                        mFrameRate = rates.get(i);
                        break;
                    }
                }
            }
        }

        mParameters.setPreviewFrameRate(mFrameRate);
        // mParameters.setPreviewFpsRange(15 * 1000, 20 * 1000);
        //TODO
        List<Camera.Size> list = mParameters.getSupportedPreviewSizes();
        int previewW = 640,previewH = 480;
//		for(Size size : list){
//			if(size.height / 9 * 16 == size.width){
//				previewH = size.height;
//				previewW = size.width;
//				Log.i("FRJ","previewH:" + previewH);
//				Log.i("FRJ","previewW:" + previewW);
//			}
//		}
        mParameters.setPreviewSize(previewW,previewH);// 3:2

        // 设置输出视频流尺寸，采样率
        mParameters.setPreviewFormat(ImageFormat.NV21);

        //设置自动连续对焦
        String mode = getAutoFocusMode();
        if (!TextUtils.isEmpty(mode)) {
            mParameters.setFocusMode(mode);
        }

        //设置人像模式，用来拍摄人物相片，如证件照。数码相机会把光圈调到最大，做出浅景深的效果。而有些相机还会使用能够表现更强肤色效果的色调、对比度或柔化效果进行拍摄，以突出人像主体。
        //		if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && isSupported(mParameters.getSupportedSceneModes(), Camera.Parameters.SCENE_MODE_PORTRAIT))
        //			mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);

        if (isSupported(mParameters.getSupportedWhiteBalance(), "auto"))
            mParameters.setWhiteBalance("auto");

        //是否支持视频防抖
        if ("true".equals(mParameters.get("video-stabilization-supported")))
            mParameters.set("video-stabilization", "true");

        //		mParameters.set("recording-hint", "false");
        //
        //		mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if (!ToolsDevice.isDevice("GT-N7100", "GT-I9308", "GT-I9300")) {
            mParameters.set("cam_mode", 1);
            mParameters.set("cam-mode", 1);
        }
    }

    /** 停止预览 */
    public void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                // camera.lock();
                mCamera.release();
            } catch (Exception e) {
                Log.e("Yixia", "stopPreview...");
            }
            mCamera = null;
        }
        mStartPreview = false;
    }


    /** 连续自动对焦 */
    private String getAutoFocusMode() {
        if (mParameters != null) {
            //持续对焦是指当场景发生变化时，相机会主动去调节焦距来达到被拍摄的物体始终是清晰的状态。
            List<String> focusModes = mParameters.getSupportedFocusModes();
            if ((Build.MODEL.startsWith("GT-I950") || Build.MODEL.endsWith("SCH-I959") || Build.MODEL.endsWith("MEIZU MX3")) && isSupported(focusModes, "continuous-picture")) {
                return "continuous-picture";
            } else if (isSupported(focusModes, "continuous-video")) {
                return "continuous-video";
            } else if (isSupported(focusModes, "auto")) {
                return "auto";
            }
        }
        return null;
    }

    /** 检测是否支持指定特性 */
    private boolean isSupported(List<String> list, String key) {
        return list != null && list.contains(key);
    }


    /**
     * 手动对焦
     * @param focusAreas 对焦区域
     * @return
     */
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean manualFocus(Camera.AutoFocusCallback cb, List<Camera.Area> focusAreas) {
        if (mCamera != null && focusAreas != null && mParameters != null && ToolsDevice.hasICS()) {
            try {
                mCamera.cancelAutoFocus();
                // getMaxNumFocusAreas检测设备是否支持
                if (mParameters.getMaxNumFocusAreas() > 0) {
                    // mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);//
                    // Macro(close-up) focus mode
                    mParameters.setFocusAreas(focusAreas);
                }

                if (mParameters.getMaxNumMeteringAreas() > 0)
                    mParameters.setMeteringAreas(focusAreas);

                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                mCamera.setParameters(mParameters);
                mCamera.autoFocus(cb);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 切换闪关灯，默认关闭
     */
    public boolean toggleFlashMode() {
        if (mParameters != null) {
            try {
                final String mode = mParameters.getFlashMode();
                if (TextUtils.isEmpty(mode) || Camera.Parameters.FLASH_MODE_OFF.equals(mode))
                    setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                else
                    setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean toggleIsOpen(){
        if (mParameters != null) {
            try {
                final String mode = mParameters.getFlashMode();
                if (TextUtils.isEmpty(mode) || Camera.Parameters.FLASH_MODE_OFF.equals(mode))
                    return false;
                else
                    return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 设置闪光灯
     * @param value
     * @see Camera.Parameters.FLASH_MODE_TORCH
     * @see Camera.Parameters.FLASH_MODE_OFF
     */
    private boolean setFlashMode(String value) {
        if (mParameters != null && mCamera != null) {
            try {
                if (Camera.Parameters.FLASH_MODE_TORCH.equals(value) || Camera.Parameters.FLASH_MODE_OFF.equals(value)) {
                    mParameters.setFlashMode(value);
                    mCamera.setParameters(mParameters);
                }
                return true;
            } catch (Exception e) {
                Log.e("Yixia", "setFlashMode", e);
            }
        }
        return false;
    }

    /** 切换前置/后置摄像头 */
    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
    }

    /** 切换前置/后置摄像头 */
    public void switchCamera(int cameraFacingFront) {
        switch (cameraFacingFront) {
            case Camera.CameraInfo.CAMERA_FACING_FRONT:
            case Camera.CameraInfo.CAMERA_FACING_BACK:
                mCameraId = cameraFacingFront;
                stopPreview();
                startPreview();
                break;
        }
    }

    public boolean isBackCamera(){
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
            return true;
        else
            return false;
    }
}
