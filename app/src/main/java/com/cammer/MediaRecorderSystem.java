package com.cammer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.IOException;

import static android.media.CamcorderProfile.get;

/**
 * Created by Fang Ruijiao on 2016/10/12.
 */

public class MediaRecorderSystem extends MediaRecorderBase implements android.media.MediaRecorder.OnErrorListener{

    private MediaRecorder mMediaRecorder;
    private OnRecorderCallback mCallback;
    private boolean isOk = false;

    public MediaRecorderSystem(Activity con, SurfaceHolder surfaceHolder) {
        super(con, surfaceHolder);
    }

    @Override
    public void startRecording(String filePath,OnRecorderCallback callback){
        mCallback = callback;
        startRecoding(filePath,0);
    }

    private void startRecoding(String filePath,int index){
        try {
            if(index >= previewSizes.size()){
                mCallback.onStarFail();
            }
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            } else {
                mMediaRecorder.reset();
            }
            mCamera.unlock();

            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); // 视频源
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 输出格式为mp4
            //设置视频输出的格式
            CamcorderProfile mProfile = get(CamcorderProfile.QUALITY_HIGH);
            if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {

            }
            if (mProfile.videoBitRate > 2 * 1024 * 1024)
                mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
            else
                mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
            mMediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);// 视频帧频率

            Camera.Size size = previewSizes.get(index);
            previewW = size.width;
            previewH = size.height;

            mMediaRecorder.setVideoSize(previewW, previewH);// 视频尺寸
            Log.i(TAG,"setVideoSize previewW:" + previewW);
            Log.i(TAG,"setVideoSize previewH:" + previewH);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);// 视频编码


//            mMediaRecorder.setMaxDuration(maxDurationInMs);

            File tempFile = new File(filePath);
            if(!tempFile.getParentFile().exists()){
                tempFile.getParentFile().mkdirs();
            }
//            Log.i(TAG,"filePath:" + filePath);
//            Log.i(TAG,"save Path:" + tempFile.getPath());

            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface()); // 预览
//
//            mMediaRecorder.setMaxFileSize(maxFileSizeInBytes);

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isOk = true;
            mCallback.onStarSucess();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            exception(filePath,index);
        } catch (IOException e) {
            e.printStackTrace();
            exception(filePath,index);
        }catch (Exception e){
            e.printStackTrace();
            exception(filePath,index);
        }
    }

    private void exception(String filePath,int index){
        releaseMediaRecorder();
        startRecoding(filePath,++ index);
    }

    /**
     * 停止拍摄，则：
     */
    @Override
    public void stopRecording(){
        if (mMediaRecorder != null) {
            //设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
                save();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mCamera != null) {
            try {
                mCamera.lock();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        }
        if (mOnErrorListener != null)
            mOnErrorListener.onVideoError(what, extra);
    }

    public void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    public void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private void save() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/videoDemo/mm.mp4";
        //添加到相册
//        ContentResolver localContentResolver = mCon.getContentResolver();
//        ContentValues localContentValues = getVideoContentValues(new File(path), System.currentTimeMillis());
//        Uri uri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
        Uri uri = Uri.parse(path);
        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        localIntent.setData(uri);
        mCon.sendBroadcast(localIntent);
//        if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
//            MediaScannerConnection.scanFile(mCon, new String[] {Environment.getExternalStorageDirectory().getPath()}, null, null);
//        }else{
//            mCon.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                    Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
//        }
    }



    public ContentValues getVideoContentValues(File paramFile, long paramLong)
    {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/mp4");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }

    private String getParentPathByImgUri(String sUri){
        Uri uri2 = Uri.parse(sUri);
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor actualimagecursor = mCon.managedQuery(uri2,proj,null,null,null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        File file = new File(img_path);
        return file.getParentFile().getAbsolutePath();
    }

    private void saveImage(){
        try{
            // 如果是打开相机拍照成功的;用游标从Media.DATA中获取;
            ContentResolver cr = mCon.getContentResolver();
            String path = Environment.getExternalStorageDirectory().getPath() + "/videoDemo/hh.jpg";
            MediaStore.Images.Media.insertImage(cr, path, "", "");
            if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                MediaScannerConnection.scanFile(mCon, new String[] {Environment.getExternalStorageDirectory().getPath()}, null, null);
            }else{
                mCon.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface OnRecorderCallback{
        public void onStarSucess();
        public void onStarFail();
    }

}
