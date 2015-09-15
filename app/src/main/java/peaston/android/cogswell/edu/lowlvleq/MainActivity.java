package peaston.android.cogswell.edu.lowlvleq;

import android.app.Activity;
import android.widget.MediaController.MediaPlayerControl;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Build;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;


import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;


import java.io.IOException;


//TASKS:
//Get Spotify song, send it to your cpp class as a short[];
//Implement 1 filter
//Implement the vertical seekbar
//implement a cascade of filters with normalizing
//Interface

public class MainActivity extends Activity implements PlayerNotificationCallback, ConnectionStateCallback {
    static
    {
        System.loadLibrary("SuperpoweredExample");
    }
    private MyAudioController myAudioController;
    private static final String CLIENT_ID = "ea3479e0f2464105bdc683bcdd872af2";
    private static final String REDIRECT_URI = "my-first-android-eq://callback";
    //private static PCMGrab PCMGrab = new PCMGrab();

       private Player mPlayer; //We need a spotify player to pull our audio

    private int[] frequencies;
    private static final int REQUEST_CODE = 1337;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            myFilters(getPackageResourcePath());
            setContentView(R.layout.equalizer_interface);
            if (savedInstanceState == null) {
                //////////////////////////////////////////
                ///////////////SPOTIFY LOGIN//////////////
                //////////////////////////////////////////
                AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                        AuthenticationResponse.Type.TOKEN,
                        REDIRECT_URI);
                builder.setScopes(new String[]{"user-read-private", "streaming"});
                AuthenticationRequest request = builder.build();

                AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request); //send to Spotify Client

            final TextView t1 = (TextView)findViewById((R.id.f1));


            final VerticalSeekBar filt1 = (VerticalSeekBar)findViewById(R.id.seekbar_1);
                filt1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        setFilt(progress, 1, .7f);

                    }
                    public void onStartTrackingTouch(SeekBar seekBar) {}
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });


//            final VerticalSeekBar filt2 = (VerticalSeekBar)findViewById(R.id.f2);
//                filt2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        onFilt2(progress);
//                    }
//                    public void onStartTrackingTouch(SeekBar seekBar) {}
//                    public void onStopTrackingTouch(SeekBar seekBar) {}
//            });
//                final VerticalSeekBar filt3 = (VerticalSeekBar)findViewById(R.id.f3);
//                filt3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        onFilt3(progress);
//                    }
//                    public void onStartTrackingTouch(SeekBar seekBar) {}
//                    public void onStopTrackingTouch(SeekBar seekBar) {}
//                });
//                final VerticalSeekBar filt4 = (VerticalSeekBar)findViewById(R.id.f4);
//                filt4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        onFilt4(progress);
//                    }
//                    public void onStartTrackingTouch(SeekBar seekBar) {}
//                    public void onStopTrackingTouch(SeekBar seekBar) {}
//                });
//                final VerticalSeekBar filt5 = (VerticalSeekBar)findViewById(R.id.f5);
//                filt5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        onFilt5(progress);
//                    }
//                    public void onStartTrackingTouch(SeekBar seekBar) {}
//                    public void onStopTrackingTouch(SeekBar seekBar) {}
//                });
//                final VerticalSeekBar filt6 = (VerticalSeekBar)findViewById(R.id.f6);
//                filt6.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        onFilt6(progress);
//                    }
//                    public void onStartTrackingTouch(SeekBar seekBar) {}
//                    public void onStopTrackingTouch(SeekBar seekBar) {}
//                });
//                final VerticalSeekBar filt7 = (VerticalSeekBar)findViewById(R.id.f7);
//                filt7.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        onFilt7(progress);
//                    }
//                    public void onStartTrackingTouch(SeekBar seekBar) {}
//                    public void onStopTrackingTouch(SeekBar seekBar) {}
//                });
//                final VerticalSeekBar filt8 = (VerticalSeekBar)findViewById(R.id.f8);
//                filt8.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                    @Override
//                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                        onFilt8(progress);
//                    }
//                    public void onStartTrackingTouch(SeekBar seekBar) {}
//                    public void onStopTrackingTouch(SeekBar seekBar) {}
//                });


            }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) { //openLoginActivity calls this
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) { //Called after logging in
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                com.spotify.sdk.android.player.Config playerConfig =
                        new com.spotify.sdk.android.player.Config(this, response.getAccessToken(), CLIENT_ID);

                myAudioController = new MyAudioController();

                Player.Builder builder = new Player.Builder(playerConfig);
                builder.setAudioController(myAudioController);

                mPlayer = Spotify.getPlayer(builder, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addPlayerNotificationCallback(MainActivity.this);
                        mPlayer.queue("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");



                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }


    //<editor-fold desc="Spotify_Logs">
    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);


    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }
    //</editor-fold>

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
/*sp members(old)*/
    //private native void SuperpoweredExample(String apkPath, long[] offsetAndLength);
    //private native void onCrossfader(int value);
    //private native void onFxSelect(int value);
    //private native void onFxValue(int value);
    //private native void onPlayPause(boolean play);
    //private native void onFxOff();
private native void setFilt(int vol, int filtNum, float q);
private native void myFilters(String apkPath);



}
