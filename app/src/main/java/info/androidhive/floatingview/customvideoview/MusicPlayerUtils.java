package info.androidhive.floatingview.customvideoview;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by advanz101 on 21/9/17.
 */

public class MusicPlayerUtils {
    MediaPlayer mediaPlayer;

    private void initMediaPlayer(String audioPath) {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();//new MediaPlayer instance

        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopMedia();
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                playMedia();
            }
        });
        // mediaPlayer.setOnBufferingUpdateListener(this);
        // mediaPlayer.setOnSeekCompleteListener(this);
         mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
             @Override
             public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                 return false;
             }
         });
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();


        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(audioPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }


    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void skipToNext(String audioPath) {
        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer(audioPath);
    }


    private void skipToPrevious(String audioPath) {
        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer(audioPath);
    }

}
