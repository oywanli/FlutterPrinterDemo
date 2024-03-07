package com.dspread.flutter_printer_qpos;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.dspread.flutter_printer_qpos.flutter_printer_qpos.R;

public class AlertSoundPlayer {
    public static final int MSG_BATTERY_LOW = 98;
    public static final int MSG_NO_PAPER = 99;
    public static final int MSG_OVERHEAT = 100;
    private static final int MSG_STOP = 111;
    private AudioManager mAudioMgr;
    private Context mContext;
    private Handler mSoundHandler;
    private int mSoundId;
    private SoundPool mSoundPool;
    private HandlerThread mHandlerThread;
    private int mSystemVolume = 0;
    private int mCount = 0;
    private int mTargetCount = 0;

    public AlertSoundPlayer(Context context) {
        this.mContext = context;
        if (mHandlerThread == null || !mHandlerThread.isAlive()) {
            mHandlerThread = new HandlerThread("sound");
        }
    }

    public void init() {
        this.mHandlerThread.start();
        this.mAudioMgr = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()).build();
        } else {
            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                //TRACE.d("init;onLoadComplete; alert sound player sampleId=" + sampleId + ", status" + status);
            }
        });
        mSoundId = mSoundPool.load(mContext, R.raw.alert, 1);
//        TipUtil.d("init; alert sound player mSoundId=" + mSoundId);
        this.mSoundHandler = new Handler(this.mHandlerThread.getLooper()) { // from class: com.action.printerservice.AlertSoundPlayer.1
            @Override // android.os.Handler
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                TipUtil.d("init; alert sound player what=" + msg.what);
                switch (msg.what) {
                    case MSG_BATTERY_LOW:
                        if (AlertSoundPlayer.this.mCount < AlertSoundPlayer.this.mTargetCount) {
                            AlertSoundPlayer.this.playSound(1);
                            return;
                        } else {
                            AlertSoundPlayer.this.mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, AlertSoundPlayer.this.mSystemVolume, AudioManager.FLAG_PLAY_SOUND);
                            return;
                        }
                    case MSG_NO_PAPER:
                        if (AlertSoundPlayer.this.mCount < AlertSoundPlayer.this.mTargetCount) {
                            AlertSoundPlayer.this.playSound(3);
                            AlertSoundPlayer.this.mSoundHandler.sendEmptyMessageDelayed(MSG_NO_PAPER, 7000L);
                            return;
                        }
                        AlertSoundPlayer.this.mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, AlertSoundPlayer.this.mSystemVolume, AudioManager.FLAG_PLAY_SOUND);
                        return;
                    case MSG_OVERHEAT:
                        if (AlertSoundPlayer.this.mCount < AlertSoundPlayer.this.mTargetCount) {
                            AlertSoundPlayer.this.playSound(5);
                            AlertSoundPlayer.this.mSoundHandler.sendEmptyMessageDelayed(MSG_OVERHEAT, 7000L);
                            return;
                        }
                        AlertSoundPlayer.this.mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, AlertSoundPlayer.this.mSystemVolume, AudioManager.FLAG_PLAY_SOUND);
                        return;
                    case MSG_STOP:
                        AlertSoundPlayer.this.mAudioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, AlertSoundPlayer.this.mSystemVolume, AudioManager.FLAG_PLAY_SOUND);
                        AlertSoundPlayer.this.mSoundPool.stop(AlertSoundPlayer.this.mSoundId);
                        return;
                    default:
                        return;
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playSound(int loop) {
//        TipUtil.d("playSound; alert sound player loop=" + loop);
        AudioManager audioManager = this.mAudioMgr;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_PLAY_SOUND);
//        TipUtil.d("playSound; alert sound player mSoundId=" + mSoundId);
        this.mSoundPool.play(this.mSoundId, 1.0f, 1.0f, 1, loop - 1, 1.0f);
        this.mCount++;
    }

    public void play(int type, int count) {
        //TRACE.d("play; alert sound player type=" + type + ", count=" + count);
        this.mCount = 0;
        this.mTargetCount = count;
        this.mSystemVolume = this.mAudioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        this.mSoundHandler.removeMessages(MSG_NO_PAPER);
        this.mSoundHandler.removeMessages(MSG_OVERHEAT);
        this.mSoundHandler.removeMessages(MSG_BATTERY_LOW);
        this.mSoundHandler.obtainMessage(type).sendToTarget();
    }

    public void stop() {
//        TipUtil.d("stop; alert sound player");
        this.mSoundHandler.removeMessages(MSG_NO_PAPER);
        this.mSoundHandler.removeMessages(MSG_OVERHEAT);
        this.mSoundHandler.removeMessages(MSG_STOP);
        this.mSoundHandler.obtainMessage(MSG_STOP).sendToTarget();
    }

    public void release() {
//        TipUtil.d("release; alert sound player");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            this.mHandlerThread.quitSafely();
        }
        if (mSoundPool != null) {
            mSoundPool.unload(mSoundId);
            mSoundPool.release();
        }
    }
}