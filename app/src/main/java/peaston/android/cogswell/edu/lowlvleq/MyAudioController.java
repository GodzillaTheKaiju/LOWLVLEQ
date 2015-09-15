package peaston.android.cogswell.edu.lowlvleq;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.player.AudioController;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;

import java.util.Arrays;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;


/**
 * Created by peaston on 4/21/2015.
 */
public class MyAudioController extends Activity implements AudioController{

    static
    {
        System.loadLibrary("SuperpoweredExample");
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //////////////SUPERPOWERED INIT//////////////
        // Get the device's sample rate and buffer size to enable low-latency Android audio output, if available.
        String samplerateString = null, buffersizeString = null;
        if (Build.VERSION.SDK_INT >= 17) {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            samplerateString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            buffersizeString = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        }
        if (samplerateString == null) samplerateString = "44100";
        if (buffersizeString == null) buffersizeString = "512";

        AssetFileDescriptor fd0 = getResources().openRawResourceFd(R.raw.lycka), fd1 = getResources().openRawResourceFd(R.raw.nuyorica);

        //args:path to the APK file, offset and length of the two
        // resource files, sample rate, audio buffer size.


        //////////////SUPERPOWERED ASSETS TO_DO.make spotify stream to here/////////////////////
        // Files under res/raw are not compressed, just copied into the APK. Get the offset and length to know where our files are located.


    /*Crossfaders(old)*/
        //            /////SP Crossfaders/////////
        //            final SeekBar crossfader = (SeekBar)findViewById(R.id.crossFader);
        //            crossfader.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        //
        //                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //                    onCrossfader(progress);
        //                }
        //
        //                public void onStartTrackingTouch(SeekBar seekBar) {}
        //                public void onStopTrackingTouch(SeekBar seekBar) {}
        //            });

    }






    private static final String PLAYER_TAG = "Player_tag";
    private AudioTrack mAudioTrack;

    public MyAudioController() {

    }



    //<editor-fold desc="Spotify Audio Player">
    @Override
    public void start()
    {

        Log.d(PLAYER_TAG, "start");
        // Happens only once - when Player is created.
        // This method should contain all set-up related
        // logic such as starting a thread
    }


    @Override
    public void onAudioResumed()
    {
        Log.d(PLAYER_TAG, "onAudioResumed");
        //Called when audio playback should be resumed
        // (e.g. the user has pressed the play button after having paused).
    }
    @Override
    public void stop()
    {
        Log.d(PLAYER_TAG, "stop");
        //Called when the controller is stopped by the Player. Happens only once
        // - when Player is destroyed. This method should contain all
        // tear-down related logic such as stopping the thread.
    }
    @Override
    public void onAudioPaused()
    {
        Log.d(PLAYER_TAG, "onAudioPaused");
//Called when audio playback should be paused (e.g. the user has pressed the pause button).
// This can be used to pause audio playback and prevent buffered audio from being played.
    }
    @Override
    public void onAudioFlush()
    {
        Log.d(PLAYER_TAG, "onAudioFlush");
        if(mAudioTrack!= null)
            mAudioTrack.flush();
        //Called when the Player has flushed the audio buffer.
        // Any buffered frames waiting to be played should be discarded.
        // This method is synchronous and therefore blocking, so any
        // long-running operations will affect playback quality.
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onAudioDataDelivered(short[] frames, int numFrames, int sampleRate, int channels)
    {


        ///////////////SUPERPOWERED C++ SEND////////////////

        float[]pcm = myFilters_process(frames); //filters pcm

        if (mAudioTrack == null ){
            int intSize = android.media.AudioTrack.getMinBufferSize(
                    sampleRate,
                    channels,
                    AudioFormat.ENCODING_PCM_16BIT) * 2; //performance
            Log.d(PLAYER_TAG, "AUDIO DELIVERED CREATING TRACK " + intSize);
            mAudioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    intSize,
                    AudioTrack.MODE_STREAM);

            mAudioTrack.play();

        }
        mAudioTrack.write(pcm, 0, numFrames, AudioTrack.WRITE_BLOCKING);
        int written = mAudioTrack.write(pcm, 0, numFrames, AudioTrack.WRITE_BLOCKING)/channels;

        mAudioTrack.play();

        ////Called whenever Player receives audio data. This method is synchronous and therefore blocking. Any long running operations will affect playback quality.
        //        Parameters:
        //        frames - 16-bit PCM data. The buffer contains numFrames frames, whereby each frame contains the data for a single audio channel.
        //        numFrames - Number of frames in frames buffer.
        //            sampleRate - Sample rate in Hz (such as 22050, 44100 or 48000).
        //            channels - Number of channels (1 = mono, 2 = stereo).
        //        Returns:
        //        The number of frames that the application accepted.
        return written;
    }

    /*faderfx(old)*/
    //            ///////fx fader events///////////
    //            final SeekBar fxfader = (SeekBar)findViewById(R.id.fxFader);
    //            fxfader.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

    //                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    //                    onFxValue(progress);
    //                }

    //                public void onStartTrackingTouch(SeekBar seekBar) {
    //                    onFxValue(seekBar.getProgress());
    //                }

    //                public void onStopTrackingTouch(SeekBar seekBar) {
    //                    onFxOff();
    //                }
    //            });

    //            ////// fx select event///////
    //            final RadioGroup group = (RadioGroup)findViewById(R.id.radioGroup1);
    //            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
    //                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
    //                    RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
    //                    onFxSelect(radioGroup.indexOfChild(checkedRadioButton));
    //                }
    //            });






    private native float[] myFilters_process(short[] input);


    //</editor-fold>
}
