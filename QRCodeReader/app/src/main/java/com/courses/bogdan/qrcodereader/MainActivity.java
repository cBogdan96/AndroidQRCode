package com.courses.bogdan.qrcodereader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SurfaceView cameraView; // display preview frames caputred by camera
    private TextView barcodeInfo; // display qr code content
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource; //  fetch a stream of images from the device's camera and display on SurfaceView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // apply layout  you created

        //get references to  the widgets(a component of an interface,
        // that enables a user to perform a function or access a service) defined in the layout
        cameraView = (SurfaceView)findViewById(R.id.camera_view);
        barcodeInfo = (TextView)findViewById(R.id.code_info);


        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
        //camera source need a barcodeDetector
        cameraSource = new CameraSource.Builder(this,barcodeDetector).setRequestedPreviewSize(640,640).build();
        /* set a callback function
          (A callback function is a function which is:
               passed as an argument to another function, and,
               is invoked after some kind of event.
           Once its parent function completes, the function passed as an argument is then called.
           )
            so  you  will know when you can start drawing the preview frames

        */
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(cameraView.getHolder()); //start drawing the preview frames
                }catch (IOException e){
                    Log.e("Camera Source",e.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop(); //stop drawing the preview frames
            }
        });
        // define what should we do with barcode Detector
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems(); // more memory efficent

                if(barcodes.size() !=0){
                    barcodeInfo.post(new Runnable() {
                        @Override
                        public void run() {
                            /*
                                you should embed the call to the setText method inside a call to the post method of the TextView,
                                 because receiveDetections does not run on the UI thread. Failing to do so will lead to a runtime error.
                             */
                            barcodeInfo.setText(barcodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });




    }
}
