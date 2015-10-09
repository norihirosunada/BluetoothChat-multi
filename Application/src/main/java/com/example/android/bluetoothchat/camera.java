package com.example.android.bluetoothchat;

import android.app.Activity;

import android.hardware.Camera;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

/**
 * Created by norihirosunada on 15/09/26.
 */


public class camera extends Activity {
    private Camera myCamera;

        private SurfaceHolder.Callback mSurfaceListener =
            new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                myCamera.open();
                    try {
                        myCamera.setPreviewDisplay(holder);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            public void surfaceDestroyed(SurfaceHolder holder) {
                // TODO Auto-generated method stub
                myCamera.release();
                myCamera = null;
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                int height) {
                    // TODO Auto-generated method stub
                    Camera.Parameters parameters = myCamera.getParameters();
                    parameters.setPreviewSize(width, height);
                    myCamera.setParameters(parameters);
                    myCamera.startPreview();
                }
        };

            /** Called when the activity is first created. */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.cameraview);

            SurfaceView mySurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
            SurfaceHolder holder = mySurfaceView.getHolder();
            holder.addCallback(mSurfaceListener);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }


}