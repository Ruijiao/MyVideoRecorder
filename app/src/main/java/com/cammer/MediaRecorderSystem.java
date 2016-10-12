package com.cammer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

/**
 * Created by Fang Ruijiao on 2016/10/12.
 */

public class MediaRecorderSystem extends MediaRecorderBase{

    private MediaRecorder mMediaRecorder;

    public MediaRecorderSystem(Activity con, SurfaceHolder surfaceHolder) {
        super(con, surfaceHolder);
    }

    public boolean startRecording(String filePath){
        try {
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            } else {
                mMediaRecorder.reset();
            }
            mCamera.unlock();

            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface()); // 预览
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); // 视频源
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 输出格式为mp4
            //设置视频输出的格式
            CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            if (mProfile.videoBitRate > 2 * 1024 * 1024)
                mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
            else
                mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
            mMediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);// 视频帧频率

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

//            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
//
//            mMediaRecorder.setMaxFileSize(maxFileSizeInBytes);

            mMediaRecorder.prepare();
            mMediaRecorder.start();

            return true;
        } catch (IllegalStateException e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
            return false;
        }catch (Exception e){
            e.printStackTrace();
            stopRecording();
            return false;
        }
    }

    /**
     * 停止拍摄，则：
     */
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
        Log.i("FRJ","getVideoContentValues() name:" + paramFile.getName());
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
//		Log.i("FRJ","img_path:" + img_path);
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
}
