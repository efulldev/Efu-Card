package com.efulltech.efupay.efucard.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class MediaPlayerService implements MediaPlayer.OnPreparedListener {
    private MediaPlayer mMediaPlayer;


    public MediaPlayerService() {
        this.mMediaPlayer = new MediaPlayer();
    }

    public void playSong(String url) {
        Log.d("MEDIA", url);
//        url = "http://tinysong.com/wlFD";
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.media.MediaPlayer.OnPreparedListener#onPrepared(android.media
     * .MediaPlayer)
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

    }
}
