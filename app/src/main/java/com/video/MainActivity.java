package com.video;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cammer.MediaRecorderSystem;
import com.tools.ToolsDevice;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    /**
     * 摄像头数据显示画布
     */
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private MediaRecorderSystem mediaRecorderSystem;

    private ImageView mXiangceIv, mRecordSwitchIv;
    private CheckBox mActionChb, mRecordLed;
    private TextView mTimeTv,mCountdownTv;

    private boolean isActionRecorder = false;

    private Handler handler;

    private int maxTimeS = 10 + 1,countdownS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mRecordLed = (CheckBox) findViewById(R.id.a_video_recorder_led);
        mSurfaceView = (SurfaceView) findViewById(R.id.a_video_recorder_surfaceview);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mActionChb = (CheckBox) findViewById(R.id.a_video_recorder_action);
        mXiangceIv = (ImageView) findViewById(R.id.a_video_recorder_play_ce);
        mRecordSwitchIv = (ImageView) findViewById(R.id.a_video_recorder_switcher);
        mTimeTv = (TextView) findViewById(R.id.a_video_recorder_time);
        mCountdownTv = (TextView) findViewById(R.id.a_video_recorder_number);

        // 设置surfaceView分辨率
        mSurfaceView.getHolder().setFixedSize(800, 480);


        mActionChb.setOnClickListener(this);
        mRecordSwitchIv.setOnClickListener(this);
        mXiangceIv.setOnClickListener(this);
        mRecordLed.setOnClickListener(this);
        mSurfaceView.setOnTouchListener(mOnSurfaveViewTouchListener);

        mediaRecorderSystem = new MediaRecorderSystem(this, mSurfaceHolder);

        handler = new Handler() {

            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        int ss = msg.arg1;
                        int ff = 0;
                        if(ss > 59){
                            ff = ss / 60;
                            ss = ss % 60;
                        }
                        String strS = "" + ss,strF = "" + ff;
                        if(ss < 10){
                            strS = "0" + ss;
                        }
                        if(ff < 10){
                            strF = "0" + ff;
                        }
                        mTimeTv.setText(strF + ":" + strS);
                        break;
                    case 2:
                        mCountdownTv.setVisibility(View.VISIBLE);
                        mCountdownTv.setText("" + msg.arg1);
                        break;
                    case 3:
                        stopCamera();
                        break;
                }

            }
        };
        initSurfaceView();
    }

    /** 初始化画布 */
    private void initSurfaceView() {
        final int h = ToolsDevice.getScreenHeight(this);
        int height = h;
        int width = h / 3 * 4;
        //
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
        lp.width = width;
        lp.height = height;

        mSurfaceView.setLayoutParams(lp);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.a_video_recorder_led://闪光灯
                mediaRecorderSystem.toggleFlashMode();
                break;
            case R.id.a_video_recorder_switcher://前后摄像头切换
                if (mediaRecorderSystem.toggleIsOpen()) { //如果开着闪关灯，则会关闭，此时需要改变闪关灯按钮状态
                    mRecordLed.setChecked(false);
                }
                mediaRecorderSystem.switchCamera();
                if (mediaRecorderSystem.isBackCamera()) {
                    mRecordLed.setVisibility(View.VISIBLE);
                } else {
                    mRecordLed.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.a_video_recorder_action:
                if (!isActionRecorder) {
                    starCamera();
                } else {
                    stopCamera();
                }
                isActionRecorder = !isActionRecorder;
                break;
            case R.id.a_video_recorder_play_ce:
                Toast.makeText(MainActivity.this, "相册", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private boolean isStar = false;
    private void starCamera() {
//        AudioTools.play(MainActivity.this, new AudioTools.OnPlayAudioListener() {
//            @Override
//            public void playOver() {
//                Toast.makeText(MainActivity.this,"播放完毕",Toast.LENGTH_SHORT).show();
//            }
//        });
        isStar = true;
        String path = Environment.getExternalStorageDirectory().getPath() + "/videoDemo/";
        String key = String.valueOf(System.currentTimeMillis()) + ".mp4";
        mediaRecorderSystem.startRecording(path + key);
        mRecordLed.setVisibility(View.GONE);
        findViewById(R.id.a_video_recorder_time_margin_view).setVisibility(View.GONE);
        findViewById(R.id.a_video_recorder_action_point).setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int time = 0;
                while (isStar){
                    if(maxTimeS - time <= 0){
                        handler.sendEmptyMessage(3);
                        break;
                    }
                    if(maxTimeS - time <= countdownS){
                        Message message = new Message();
                        message.arg1 = maxTimeS - time;
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                    Message message = new Message();
                    message.arg1 = time;
                    message.what = 1;
                    handler.sendMessage(message);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time ++;
                }
            }
        }).start();
    }

    private void stopCamera(){
        isStar = false;
        isActionRecorder = false;
        mActionChb.setChecked(false);
        mRecordLed.setVisibility(View.VISIBLE);
        findViewById(R.id.a_video_recorder_time_margin_view).setVisibility(View.VISIBLE);
        findViewById(R.id.a_video_recorder_action_point).setVisibility(View.INVISIBLE);
        mCountdownTv.setVisibility(View.GONE);
        Toast.makeText(MainActivity.this,"已保存",Toast.LENGTH_SHORT).show();
        mTimeTv.setText("00:00");
        mediaRecorderSystem.stopRecording();
    }

    /**
     * 点击屏幕录制
     */
    private View.OnTouchListener mOnSurfaveViewTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //检测是否手动对焦
                    if (checkCameraFocus(event))
                        return true;
                    break;
            }
            return true;
        }

    };

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean checkCameraFocus(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float touchMajor = event.getTouchMajor();
        float touchMinor = event.getTouchMinor();

        Rect touchRect = new Rect((int) (x - touchMajor / 2), (int) (y - touchMinor / 2), (int) (x + touchMajor / 2), (int) (y + touchMinor / 2));
        //The direction is relative to the sensor orientation, that is, what the sensor sees. The direction is not affected by the rotation or mirroring of setDisplayOrientation(int). Coordinates of the rectangle range from -1000 to 1000. (-1000, -1000) is the upper left point. (1000, 1000) is the lower right point. The width and height of focus areas cannot be 0 or negative.
        //No matter what the zoom level is, (-1000,-1000) represents the top of the currently visible camera frame
        if (touchRect.right > 1000)
            touchRect.right = 1000;
        if (touchRect.bottom > 1000)
            touchRect.bottom = 1000;
        if (touchRect.left < 0)
            touchRect.left = 0;
        if (touchRect.right < 0)
            touchRect.right = 0;

        if (touchRect.left >= touchRect.right || touchRect.top >= touchRect.bottom)
            return false;

        ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        focusAreas.add(new Camera.Area(touchRect, 1000));
        if (!mediaRecorderSystem.manualFocus(new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {

            }
        }, focusAreas)) {
//            mFocusImage.setVisibility(View.GONE);
        }

        return true;
    }
}
