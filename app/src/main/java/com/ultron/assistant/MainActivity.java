
package com.ultron.assistant;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.content.Intent;
import java.util.ArrayList;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;

public class MainActivity extends Activity {

    private static final int CAMERA_REQUEST = 100;
    private TextureView preview;
    private TextView status;
    private CameraDevice camera;
    private SpeechRecognizer speechRecognizer;
    private CameraCaptureSession cameraSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("ULTRON - Camera");
        title.setTextSize(24);

        status = new TextView(this);
        status.setText("Camera ready");

        preview = new TextureView(this);

        Button rear = new Button(this);
        rear.setText("Open Rear Camera");

        Button front = new Button(this);
        front.setText("Open Front Camera");
        Button voice = new Button(this);
        voice.setText("🎤 Start Voice Command");

        root.addView(title);
        root.addView(status);
        root.addView(
                preview,
                new LinearLayout.LayoutParams(-1, 0, 1)
        );
        root.addView(rear);
        root.addView(front);
        root.addView(voice);

        setContentView(root);

        rear.setOnClickListener(v -> openCamera(true));
        front.setOnClickListener(v -> openCamera(false));
        voice.setOnClickListener(v -> { status.setText("Voice button clicked"); startVoice(); });

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST
            );
        }
    }
    private void startVoice() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 101);
            return;
        }


        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                java.util.Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak now");

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {
                status.setText("Listening...");
            }

            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}

            @Override public void onError(int error) {
                status.setText("Voice error: " + error);
            }

            @Override public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    status.setText("You said: " + matches.get(0));
                }
            }

            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });

        speechRecognizer.startListening(intent);
    }


    private void openCamera(boolean rear) {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST
            );
            return;
        }
            if (cameraSession != null) {
                cameraSession.close();
                cameraSession = null;
            }

            if (camera != null) {
                camera.close();
                camera = null;
            }

        try {
            CameraManager manager =
                    (CameraManager) getSystemService(CAMERA_SERVICE);

            String selectedCamera = null;

            for (String id : manager.getCameraIdList()) {
                android.hardware.camera2.CameraCharacteristics info =
                        manager.getCameraCharacteristics(id);

                Integer facing = info.get(
                        android.hardware.camera2.CameraCharacteristics.LENS_FACING
                );

                if (rear &&
                        facing != null &&
                        facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK) {
                    selectedCamera = id;
                    break;
                }

                if (!rear &&
                        facing != null &&
                        facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_FRONT) {
                    selectedCamera = id;
                    break;
                }
            }

            if (selectedCamera == null) {
                status.setText("Camera not available");
                return;
            }

            manager.openCamera(
                    selectedCamera,
                    new CameraDevice.StateCallback() {

                        @Override
                        public void onOpened(CameraDevice device) {
                            camera = device;
                            startPreview();
                        }

                        @Override
                        public void onDisconnected(CameraDevice device) {
                            device.close();
                            camera = null;
                        }

                        @Override
                        public void onError(CameraDevice device, int error) {
                            device.close();
                            camera = null;
                            status.setText("Camera error");
                        }
                    },
                    null
            );

        } catch (Exception e) {
            status.setText("Camera access failed");
        }
    }

    private void startPreview() {
        if (camera == null || !preview.isAvailable()) {
            status.setText("Preview not ready");
            return;
        }

        try {
            SurfaceTexture texture = preview.getSurfaceTexture();
            Surface surface = new Surface(texture);

            android.hardware.camera2.CaptureRequest.Builder request =
                    camera.createCaptureRequest(
                            CameraDevice.TEMPLATE_PREVIEW
                    );

            request.addTarget(surface);

            camera.createCaptureSession(
                    Collections.singletonList(surface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(
                                CameraCaptureSession session) {
                            try {
                                  cameraSession = session;
                                session.setRepeatingRequest(
                                        request.build(),
                                        null,
                                        null
                                );

                                status.setText("Camera preview ON");

                            } catch (Exception e) {
                                status.setText("Preview failed");
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                CameraCaptureSession session) {
                            status.setText(
                                    "Camera configuration failed"
                            );
                        }
                    },
                    null
            );

        } catch (Exception e) {
            status.setText("Preview error");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (camera != null) {
            camera.close();
            camera = null;
        }
          if (cameraSession != null) {
              cameraSession.close();
              cameraSession = null;
          }
    }
}
