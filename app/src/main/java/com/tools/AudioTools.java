package com.tools;

import android.content.Context;
import android.media.MediaPlayer;

import com.video.R;

/**
 * Created by Fang Ruijiao on 2016/10/12.
 */

public class AudioTools {

    public static void play(Context con, final OnPlayAudioListener listener){
        MediaPlayer mediaPlayer = MediaPlayer.create(con,R.raw.dish_video_star);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (listener != null)listener.playOver();
            }
        });
        mediaPlayer.start();
    }

    public interface OnPlayAudioListener{
        public void playOver();
    }
}
