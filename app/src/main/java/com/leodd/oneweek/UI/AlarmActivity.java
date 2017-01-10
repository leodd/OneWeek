package com.leodd.oneweek.UI;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.leodd.oneweek.R;
import com.leodd.oneweek.Utils.CalendarDate;
import com.leodd.oneweek.Utils.ScreenLockUtil;

/**
 * Created by leodd on 2017/1/3.
 */

public class AlarmActivity extends SupportDismissActivity {

    public static final String ARG_CONTENT = "content";
    public static final String ARG_VIBRATE = "vibrate";

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private boolean isVibrateOnly;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final View decorView = getWindow().getDecorView();
        int flag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if(Build.VERSION.SDK_INT >= 19) {
            flag |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        if(Build.VERSION.SDK_INT >= 16) {
            flag |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        decorView.setSystemUiVisibility(flag);

//        if(android.os.Build.VERSION.SDK_INT <= 19) {
//            KeyguardManager manager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
//            KeyguardManager.KeyguardLock lock = manager.newKeyguardLock("abc");
//            lock.disableKeyguard();
//        }

        final LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.alarm_activity, null);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        setContentView(view);

        setBaseLayout((ViewGroup) findViewById(R.id.alarm_activity_base_layout));

        String content = getIntent().getStringExtra(ARG_CONTENT);
        CalendarDate current = new CalendarDate();

        TextView contentTextView = (TextView) findViewById(R.id.alarm_activity_content);
        TextView timeTextView = (TextView) findViewById(R.id.alarm_activity_time);

        contentTextView.setText(content);
        timeTextView.setText(current.getTimeString());

        isVibrateOnly = getIntent().getBooleanExtra(ARG_VIBRATE, false);

        Log.e("alarm", "pass");

        startAlarm();
    }

    private void startAlarm() {
        mediaPlayer = new MediaPlayer();

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = { 1000, 200, 200, 200 };
        vibrator.vibrate(pattern, 0);

        if(!isVibrateOnly) {
            try {
                mediaPlayer.setVolume(0.8f, 0.8f);
                mediaPlayer.setDataSource(this,
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (Exception e) {
                mediaPlayer.release();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onDestroy() {
        try {
            if (vibrator != null)
                vibrator.cancel();
        } catch (Exception e) {

        }
        try {
            mediaPlayer.stop();
        } catch (Exception e) {

        }
        try {
            mediaPlayer.release();
        } catch (Exception e) {

        }

        ScreenLockUtil.releaseCpuLock();

        super.onDestroy();
    }
}
