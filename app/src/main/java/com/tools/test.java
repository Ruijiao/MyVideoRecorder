package com.tools;

/**
 * Created by Fang Ruijiao on 2016/10/11.
 */

public class test {
//    private void init(){
//        mRecordLed = (CheckBox) findViewById(R.id.a_video_recorder_led);
//        mSurfaceView = (SurfaceView)findViewById(R.id.a_video_recorder_surfaceview);
//        mActionIv = (ImageView)findViewById(R.id.a_video_recorder_action);
//        mActionIv.setOnClickListener(this);
//
//        initMediaRecorder();
//    }
//
//    /** 初始化拍摄SDK */
//    private void initMediaRecorder() {
//        mMediaRecorder = new MediaRecorderNative();
//        mRebuild = true;
//
//        mMediaRecorder.setOnErrorListener(onErrorListener);
//        mMediaRecorder.setOnEncodeListener(onEncodeListener);
//        File f = new File(VCamera.getVideoCachePath());
//        if (!FileUtils.checkFile(f)) {
//            f.mkdirs();
//        }
//        String key = String.valueOf(System.currentTimeMillis());
//        mMediaObject = mMediaRecorder.setOutputDirectory(key, VCamera.getVideoCachePath() + key);
//        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
//        mMediaRecorder.prepare();
//    }
//
//    /** 开始录制 */
//    private void startRecord() {
//        if (mMediaRecorder != null) {
//            MediaObject.MediaPart part = mMediaRecorder.startRecord();
//            if (part == null) {
//                return;
//            }
//
//            //如果使用MediaRecorderSystem，不能在中途切换前后摄像头，否则有问题
//            if (mMediaRecorder instanceof MediaRecorderSystem) {
//                mCameraSwitch.setVisibility(View.GONE);
//            }
//            mProgressView.setData(mMediaObject);
//        }
//
//        mRebuild = true;
//        mPressedStatus = true;
//        mRecordController.setImageResource(R.drawable.record_controller_press);
//        mBottomLayout.setBackgroundColor(mBackgroundColorPress);
//
//        if (mHandler != null) {
//            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
//            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);
//
//            mHandler.removeMessages(HANDLE_STOP_RECORD);
//            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD, RECORD_TIME_MAX - mMediaObject.getDuration());
//        }
//        mRecordDelete.setVisibility(View.GONE);
//        mCameraSwitch.setEnabled(false);
//        mRecordLed.setEnabled(false);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch(view.getId()){
//            case R.id.a_video_recorder_led://闪光灯
//                //开启前置摄像头以后不支持开启闪光灯
//                if (mMediaRecorder != null) {
//                    if (mMediaRecorder.isFrontCamera()) {
//                        return;
//                    }
//                }
//
//                if (mMediaRecorder != null) {
//                    mMediaRecorder.toggleFlashMode();
//                }
//                break;
//            case R.id.a_video_recorder_action:
//
//                break;
//        }
//    }
//
//    private void initListener(){
//        //是否支持闪光灯
//        if (ToolsDevice.isSupportCameraLedFlash(getPackageManager())) {
//            mRecordLed.setOnClickListener(this);
//        } else {
//            mRecordLed.setVisibility(View.GONE);
//        }
//        onErrorListener = new MediaRecorderBase.OnErrorListener() {
//            @Override
//            public void onVideoError(int what, int extra) {
//
//            }
//
//            @Override
//            public void onAudioError(int what, String message) {
//
//            }
//        };
//
//        onEncodeListener = new MediaRecorderBase.OnEncodeListener() {
//            @Override
//            public void onEncodeStart() {
//
//            }
//
//            @Override
//            public void onEncodeProgress(int progress) {
//
//            }
//
//            @Override
//            public void onEncodeComplete() {
//
//            }
//
//            @Override
//            public void onEncodeError() {
//
//            }
//        };
//    }
}

