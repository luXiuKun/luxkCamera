package com.luxk.luxkcamera;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.luxk.luxkcamera.camera.CameraGLView;
import com.luxk.luxkcamera.record.MediaAudioEncoder;
import com.luxk.luxkcamera.record.MediaEncoder;
import com.luxk.luxkcamera.record.MediaMuxerWrapper;
import com.luxk.luxkcamera.record.MediaVideoEncoder;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    private MediaMuxerWrapper mMuxer;
    private MediaVideoEncoder mMediaVideoEncoder;
    private MediaAudioEncoder mMediaAudioEncoder;
    private ImageView mIvCamera,mIvSave;
    private CameraGLView cameraGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraGLView = findViewById(R.id.cameraView);
        //cameraGLView.onResume();
        mIvCamera = findViewById(R.id.iv_startCamera);
        mIvSave = findViewById(R.id.iv_saveVideo);
        mIvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMuxer == null) {
                    startRecording();
                    mIvCamera.setBackgroundResource(R.mipmap.pause_video);
                } else if (mMediaVideoEncoder != null && !mMediaVideoEncoder.getState()) {
                    mMediaVideoEncoder.pause();
                    mMediaAudioEncoder.pause();
                    mIvCamera.setBackgroundResource(R.mipmap.start_video);
                } else if (mMediaVideoEncoder != null && mMediaVideoEncoder.getState()) {
                    mMediaVideoEncoder.resume();
                    mMediaAudioEncoder.resume();
                    mIvCamera.setBackgroundResource(R.mipmap.pause_video);
                }
            }
        });

        mIvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
                mIvCamera.setBackgroundResource(R.mipmap.start_video);
            }
        });
    }

    private void stopRecord() {
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;

        }
    }

    private void startRecording() {
        try {
            String savePath = getPath(
                    System.currentTimeMillis() + ".mp4");
            mMuxer = new MediaMuxerWrapper(
                    savePath);    // if you record audio only, ".m4a" is also OK.
            if (true) {
                // for video capturing
                mMediaVideoEncoder = new MediaVideoEncoder(mMuxer,
                        mMediaEncoderListener, cameraGLView.getVideoWidth(),
                        cameraGLView.getVideoHeight());
            }
            if (true) {
                // for audio capturing
                mMediaAudioEncoder = new MediaAudioEncoder(mMuxer,
                        mMediaEncoderListener);
            }
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {

        }
    }

    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener =
            new MediaEncoder.MediaEncoderListener() {
                @Override
                public void onPrepared(final MediaEncoder encoder) {
                    if (encoder instanceof MediaVideoEncoder) {
                        cameraGLView.setVideoEncoder((MediaVideoEncoder) encoder);
                    }
                }

                @Override
                public void onStopped(final MediaEncoder encoder) {
                    if (encoder instanceof MediaVideoEncoder) {
                        cameraGLView.setVideoEncoder(null);
                    }
                }
            };


    public static String getPath(String fileName) {
        String p = getBaseFolder();
        File f = new File(p);
        if (!f.exists() && !f.mkdirs()) {
            return getBaseFolder() + fileName;
        }
        return p + fileName;
    }

    public static String getBaseFolder() {
        String baseFolder = Environment.getExternalStorageDirectory() + "/LuxkVideo/";
        File f = new File(baseFolder);
        if (!f.exists()) {
            boolean b = f.mkdirs();
            if (!b) {
                baseFolder = CommApplication.getInstance().getExternalFilesDir(null).getAbsolutePath() + "/";
            }
        }
        return baseFolder;
    }

    @Override
    public void onBackPressed() {
        stopRecord();
        super.onBackPressed();
    }
}
