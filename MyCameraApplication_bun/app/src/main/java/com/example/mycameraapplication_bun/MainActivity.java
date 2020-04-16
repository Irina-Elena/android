package com.example.mycameraapplication_bun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_RESULT = 1000;
    private static final int STORAGE_REQUEST_RESULT = 1001;
    private static final int STATE_PREVIEW = 1000;
    private static final int STATE_WAIT_LOCK = 1001;
    private int captureState = STATE_PREVIEW;
    private TextureView textureView;
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Toast.makeText(MainActivity.this, "TextureView is available", Toast.LENGTH_SHORT).show();
            setupCamera(width,height);
            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback cameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            startPreview();
            //Toast.makeText(MainActivity.this, "We have access to the camera!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    };
    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;
    private String cameraId;
    private Size imageSize;
    private ImageReader imageReader;
    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            backgroundHandler.post(new ImageSaver(reader.acquireLatestImage()));

        }
    };
    private class ImageSaver implements Runnable {
        private final Image image;

        public ImageSaver(Image img) {
            image = img;
        }

        @Override
        public void run() {
            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(fileName);
                fileOutputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                image.close();
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private Button takePhotoButton;
    private CameraCaptureSession cameraCaptureSession;
    private CameraCaptureSession.CaptureCallback cameraCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult captureResult) {
            switch (captureState) {
                case STATE_PREVIEW:
                    // do nothing
                    break;
                case STATE_WAIT_LOCK:
                    captureState = STATE_PREVIEW;
                    Integer afState = captureResult.get(CaptureResult.CONTROL_AF_STATE);
                    if(afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED ||
                            afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED) {
                        Toast.makeText(MainActivity.this, "AF Locked!", Toast.LENGTH_SHORT).show();
                        startStillCaptureRequest();
                    }
                    break;
            }
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            process(result);
        }
    };
    private CaptureRequest.Builder captureRequestBuilder;
    private File photoFolder;
    private String fileName;
    private int totalRotation;

    private static SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createPhotoFolder();

        textureView = (TextureView) findViewById(R.id.textureView);
        takePhotoButton = (Button) findViewById(R.id.cameraButton);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockFocus();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        startBackgroundThread();

        if(textureView.isAvailable()) {
            setupCamera(textureView.getWidth(),textureView.getHeight());
            connectCamera();
        } else { //tell me when the textureView is available, by calling the listener
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    protected void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void closeCamera() {
        if(cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);
            int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
            totalRotation = sesnorToDeviceRotation(cameraCharacteristics, deviceOrientation);
            int rotatedWidth = width;
            int rotatedHeight = height;
            if (totalRotation == 90 || totalRotation == 270) {
                rotatedWidth = height;
                rotatedHeight = width;
            }
            imageSize = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG)[0];
            imageReader = ImageReader.newInstance(imageSize.getWidth(),imageSize.getHeight(), ImageFormat.JPEG,1);
            imageReader.setOnImageAvailableListener(onImageAvailableListener,backgroundHandler);
            //            if(cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
//                continue;
//            }

//                cameraId = camId;
//                return;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(cameraId,cameraDeviceStateCallback,backgroundHandler);
                } else {
                    if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "We need access to the camera", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_RESULT);
                }
            } else {
                cameraManager.openCamera(cameraId,cameraDeviceStateCallback,backgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_REQUEST_RESULT) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Access to camera denied!",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == STORAGE_REQUEST_RESULT) {
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Access to photoFolder denied!",Toast.LENGTH_SHORT).show();
            } else {
                try {
                    createPhotoFilename();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startPreview() {
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(textureView.getWidth(),textureView.getMeasuredHeight());
        Surface previewSurface = new Surface(surfaceTexture);

        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, imageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            cameraCaptureSession = session;
                            try {
                                cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(),
                                        null,backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(MainActivity.this, "Unable to setup the preview", Toast.LENGTH_SHORT).show();
                        }
                    },null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startStillCaptureRequest() {
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequestBuilder.addTarget(imageReader.getSurface());
            //captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, totalRotation);
            //captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, );

            CameraCaptureSession.CaptureCallback stillCaptureCallback = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);

                    try {
                        createPhotoFilename();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            cameraCaptureSession.capture(captureRequestBuilder.build(), stillCaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private void startBackgroundThread() {
        backgroundHandlerThread = new HandlerThread("CameraApp");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
    }

    private void stopBackgroundThread() {
        backgroundHandlerThread.quitSafely();
        try {
            backgroundHandlerThread.join();
            backgroundHandlerThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int sesnorToDeviceRotation(CameraCharacteristics camCharacteristics, int deviceOrientation) {
        int sensorOrientation = camCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return (sensorOrientation + deviceOrientation + 360) % 360;
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        for(Size option: choices) {
            if(option.getHeight() == option.getWidth() * height/width && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }
        return bigEnough.get(0);
    }

    private void createPhotoFolder() {
        File photoFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        photoFolder = new File(photoFile,"CameraAppPhotos");
        if(!photoFolder.exists()) {
            photoFolder.mkdirs();
        }
    }

    private File createPhotoFilename() throws IOException {
        String timestamp = new SimpleDateFormat("yyytMMdd_HHmmss").format(new Date());
        String prepend = "PICTURE_"+timestamp+"_";
        File pictureFile = File.createTempFile(prepend,".jpg", photoFolder);
        fileName = pictureFile.getAbsolutePath();
        return pictureFile;
    }

    private void lockFocus() {
        captureState = STATE_WAIT_LOCK;
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
        try {
            cameraCaptureSession.capture(captureRequestBuilder.build(),cameraCaptureCallback,backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private  void checkStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    createPhotoFilename();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs access to the photoFolder!", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_RESULT);
            }
        } else {
            try {
                createPhotoFilename();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
