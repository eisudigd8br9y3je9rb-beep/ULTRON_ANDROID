package com.ultron.assistant;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ultron.assistant.actions.AppLauncher;
import com.ultron.assistant.actions.PhoneActions;
import com.ultron.assistant.communication.CommunicationManager;
import com.ultron.assistant.core.CommandManager;
import com.ultron.assistant.voice.VoiceManager;
import com.ultron.assistant.voice.VoiceSpeaker;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private static final int CAMERA_REQUEST = 100;
    private static final int AUDIO_REQUEST = 101;

    private TextureView preview;
    private TextView status;

    private CameraDevice camera;
    private CameraCaptureSession cameraSession;

    private VoiceManager voiceManager;
    private VoiceSpeaker voiceSpeaker;
    private CommandManager commandManager;
    private AppLauncher appLauncher;
    private PhoneActions phoneActions;
    private CommunicationManager communicationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        commandManager = new CommandManager();
        appLauncher = new AppLauncher(this);
        phoneActions = new PhoneActions(this);
        communicationManager = new CommunicationManager(this);

        createUserInterface();
        createVoiceManager();
        voiceSpeaker = new VoiceSpeaker(this);
        requestRequiredPermissions();
    }

    private void createUserInterface() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 20, 20, 20);

        TextView title = new TextView(this);
        title.setText("ULTRON AI ASSISTANT");
        title.setTextSize(24);

        status = new TextView(this);
        status.setText("ULTRON ready");
        status.setTextSize(18);

        preview = new TextureView(this);

        Button rearCameraButton = new Button(this);
        rearCameraButton.setText("Open Rear Camera");

        Button frontCameraButton = new Button(this);
        frontCameraButton.setText("Open Front Camera");

        Button voiceButton = new Button(this);
        voiceButton.setText("Start Voice Command");

        Button youtubeButton = new Button(this);
        youtubeButton.setText("Open YouTube");

        Button settingsButton = new Button(this);
        settingsButton.setText("Open Settings");

        Button homeButton = new Button(this);
        homeButton.setText("Go Home");

        root.addView(title);
        root.addView(status);

        root.addView(
                preview,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                )
        );

        root.addView(rearCameraButton);
        root.addView(frontCameraButton);
        root.addView(voiceButton);
        root.addView(youtubeButton);
        root.addView(settingsButton);
        root.addView(homeButton);

        setContentView(root);

        rearCameraButton.setOnClickListener(
                v -> openCamera(true)
        );

        frontCameraButton.setOnClickListener(
                v -> openCamera(false)
        );

        voiceButton.setOnClickListener(
                v -> startVoice()
        );

        youtubeButton.setOnClickListener(
                v -> openYouTube()
        );

        settingsButton.setOnClickListener(
                v -> openSettings()
        );

        homeButton.setOnClickListener(
                v -> goHome()
        );
    }

    private void createVoiceManager() {

        voiceManager = new VoiceManager(
                this,
                new VoiceManager.VoiceCallback() {

                    @Override
                    public void onStatus(String voiceStatus) {
                        runOnUiThread(
                                () -> status.setText(voiceStatus)
                        );
                    }

                    @Override
                    public void onResult(String text) {
                        runOnUiThread(
                                () -> handleVoiceCommand(text)
                        );
                    }

                    @Override
                    public void onError(int errorCode) {
                        // Detailed error status is already shown by VoiceManager.
                        // Keep the useful message visible on screen.
                    }
                }
        );
    }

    private void requestRequiredPermissions() {

        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    CAMERA_REQUEST
            );
        }

        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.RECORD_AUDIO
                    },
                    AUDIO_REQUEST
            );
        }
    }

    private void startVoice() {

        if (checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.RECORD_AUDIO
                    },
                    AUDIO_REQUEST
            );

            status.setText(
                    "Microphone permission required"
            );

            return;
        }

        if (voiceManager == null) {
            createVoiceManager();
        }

        status.setText("Starting voice recognition...");
        voiceManager.startListening();
    }

    private void respond(String message) {

        status.setText(message);

        if (voiceSpeaker != null) {
            voiceSpeaker.speak(message);
        }
    }

    private void handleVoiceCommand(String command) {

        if (command == null || command.trim().isEmpty()) {
            status.setText("No voice command received");
            return;
        }

        status.setText("You said: " + command);

        CommandManager.CommandType commandType =
                commandManager.parseCommand(command);

        switch (commandType) {

            case GREETING:
                respond(
                        "Hello! I am ULTRON. How can I help you?"
                );
                break;

            case HOW_ARE_YOU:
                respond(
                        "I am working perfectly. How can I help you?"
                );
                break;

            case WHO_ARE_YOU:
                respond(
                        "I am ULTRON, your AI assistant."
                );
                break;

            case WHAT_IS_YOUR_NAME:
                respond(
                        "My name is ULTRON."
                );
                break;

            case OPEN_YOUTUBE:
                openYouTube();
                break;

            case OPEN_INSTAGRAM:
                openInstagram();
                break;

            case OPEN_PUBG:
                openPUBG();
                break;

            case OPEN_SETTINGS:
                openSettings();
                break;

            case GO_HOME:
                goHome();
                break;

            case SEARCH_GOOGLE:
                searchGoogle(command);
                break;

            case CALL:
                callNumber(command);
                break;

            case SMS:
                sendSms(command);
                break;

            case OPEN_CAMERA:
                status.setText("Opening rear camera...");
                openCamera(true);
                break;

            case UNKNOWN:
            default:
                status.setText(
                        "ULTRON did not understand: "
                                + command
                );
                break;
        }
    }

    private void openYouTube() {

        boolean opened = appLauncher.openYouTube();

        if (opened) {
            status.setText("Opening YouTube...");
        } else {
            status.setText("YouTube is not installed");
        }
    }

    private void openInstagram() {

        boolean opened = appLauncher.openInstagram();

        if (opened) {
            status.setText("Opening Instagram...");
        } else {
            status.setText("Instagram is not installed");
        }
    }

    private void openPUBG() {

        boolean opened = appLauncher.openPUBG();

        if (opened) {
            status.setText("Opening PUBG...");
        } else {
            status.setText("PUBG is not installed");
        }
    }

    private void openSettings() {

        status.setText("Opening Settings...");
        phoneActions.openSettings();
    }

    private void goHome() {

        status.setText("Going Home...");
        phoneActions.goHome();
    }

    private void searchGoogle(String command) {

        String query = extractSearchQuery(command);

        if (query.isEmpty()) {
            query = command;
        }

        status.setText(
                "Searching Google: " + query
        );

        phoneActions.searchGoogle(query);
    }

    private String extractSearchQuery(String command) {

        String query = command;

        query = query.replaceAll(
                "(?i)google",
                ""
        );

        query = query.replaceAll(
                "गूगल",
                ""
        );

        query = query.replaceAll(
                "(?i)search",
                ""
        );

        query = query.replaceAll(
                "सर्च",
                ""
        );

        query = query.replaceAll(
                "(?i)for",
                ""
        );

        return query.trim();
    }

    private void callNumber(String command) {

        String phoneNumber =
                extractPhoneNumber(command);

        if (phoneNumber.isEmpty()) {

            status.setText(
                    "No phone number found. Speak digits clearly."
            );

            return;
        }

        status.setText(
                "Opening dialer for: " + phoneNumber
        );

        communicationManager.openDialer(
                phoneNumber
        );
    }

    private void sendSms(String command) {

        String phoneNumber =
                extractPhoneNumber(command);

        if (phoneNumber.isEmpty()) {

            status.setText(
                    "No phone number found for SMS."
            );

            return;
        }

        String message =
                extractSmsMessage(command);

        status.setText(
                "Opening SMS for: " + phoneNumber
        );

        communicationManager.composeSms(
                phoneNumber,
                message
        );
    }

    private String extractPhoneNumber(String text) {

        if (text == null) {
            return "";
        }

        Pattern pattern =
                Pattern.compile(
                        "\\+?[0-9][0-9\\-\\s]{7,20}"
                );

        Matcher matcher =
                pattern.matcher(text);

        if (matcher.find()) {

            String number =
                    matcher.group();

            return number.replaceAll(
                    "[^0-9+]",
                    ""
            );
        }

        return "";
    }

    private String extractSmsMessage(String command) {

        if (command == null) {
            return "";
        }

        String message = command;

        message = message.replaceAll(
                "\\+?[0-9][0-9\\-\\s]{7,20}",
                ""
        );

        message = message.replaceAll(
                "(?i)sms",
                ""
        );

        message = message.replaceAll(
                "(?i)message",
                ""
        );

        message = message.replaceAll(
                "मैसेज",
                ""
        );

        return message.trim();
    }

    private void openCamera(boolean rear) {

        if (checkSelfPermission(
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    CAMERA_REQUEST
            );

            status.setText(
                    "Camera permission required"
            );

            return;
        }

        closeCamera();

        try {

            CameraManager manager =
                    (CameraManager)
                            getSystemService(
                                    CAMERA_SERVICE
                            );

            String selectedCamera = null;

            for (String id :
                    manager.getCameraIdList()) {

                android.hardware.camera2
                        .CameraCharacteristics info =
                        manager.getCameraCharacteristics(
                                id
                        );

                Integer facing =
                        info.get(
                                android.hardware.camera2
                                        .CameraCharacteristics
                                        .LENS_FACING
                        );

                if (rear
                        && facing != null
                        && facing ==
                        android.hardware.camera2
                                .CameraCharacteristics
                                .LENS_FACING_BACK) {

                    selectedCamera = id;
                    break;
                }

                if (!rear
                        && facing != null
                        && facing ==
                        android.hardware.camera2
                                .CameraCharacteristics
                                .LENS_FACING_FRONT) {

                    selectedCamera = id;
                    break;
                }
            }

            if (selectedCamera == null) {

                status.setText(
                        "Requested camera not available"
                );

                return;
            }

            final String cameraId =
                    selectedCamera;

            manager.openCamera(
                    cameraId,
                    new CameraDevice.StateCallback() {

                        @Override
                        public void onOpened(
                                CameraDevice device
                        ) {

                            camera = device;

                            runOnUiThread(
                                    () -> startPreview()
                            );
                        }

                        @Override
                        public void onDisconnected(
                                CameraDevice device
                        ) {

                            device.close();

                            if (camera == device) {
                                camera = null;
                            }
                        }

                        @Override
                        public void onError(
                                CameraDevice device,
                                int error
                        ) {

                            device.close();

                            if (camera == device) {
                                camera = null;
                            }

                            runOnUiThread(
                                    () -> status.setText(
                                            "Camera error: "
                                                    + error
                                    )
                            );
                        }
                    },
                    null
            );

        } catch (Exception e) {

            status.setText(
                    "Camera access failed"
            );
        }
    }

    private void startPreview() {

        if (camera == null) {

            status.setText(
                    "Camera not ready"
            );

            return;
        }

        if (!preview.isAvailable()) {

            preview.setSurfaceTextureListener(
                    new TextureView.SurfaceTextureListener() {

                        @Override
                        public void onSurfaceTextureAvailable(
                                SurfaceTexture surface,
                                int width,
                                int height
                        ) {

                            startPreview();
                        }

                        @Override
                        public void onSurfaceTextureSizeChanged(
                                SurfaceTexture surface,
                                int width,
                                int height
                        ) {
                        }

                        @Override
                        public boolean onSurfaceTextureDestroyed(
                                SurfaceTexture surface
                        ) {

                            return true;
                        }

                        @Override
                        public void onSurfaceTextureUpdated(
                                SurfaceTexture surface
                        ) {
                        }
                    }
            );

            status.setText(
                    "Waiting for camera preview..."
            );

            return;
        }

        try {

            SurfaceTexture texture =
                    preview.getSurfaceTexture();

            if (texture == null) {

                status.setText(
                        "Preview texture unavailable"
                );

                return;
            }

            texture.setDefaultBufferSize(
                    preview.getWidth(),
                    preview.getHeight()
            );

            Surface surface =
                    new Surface(texture);

            android.hardware.camera2
                    .CaptureRequest.Builder request =
                    camera.createCaptureRequest(
                            CameraDevice.TEMPLATE_PREVIEW
                    );

            request.addTarget(surface);

            camera.createCaptureSession(
                    Collections.singletonList(
                            surface
                    ),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(
                                CameraCaptureSession session
                        ) {

                            try {

                                cameraSession = session;

                                session.setRepeatingRequest(
                                        request.build(),
                                        null,
                                        null
                                );

                                runOnUiThread(
                                        () -> status.setText(
                                                "Camera preview ON"
                                        )
                                );

                            } catch (Exception e) {

                                runOnUiThread(
                                        () -> status.setText(
                                                "Preview failed"
                                        )
                                );
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                CameraCaptureSession session
                        ) {

                            runOnUiThread(
                                    () -> status.setText(
                                            "Camera configuration failed"
                                    )
                            );
                        }
                    },
                    null
            );

        } catch (Exception e) {

            status.setText(
                    "Preview error"
            );
        }
    }

    private void closeCamera() {

        if (cameraSession != null) {

            cameraSession.close();
            cameraSession = null;
        }

        if (camera != null) {

            camera.close();
            camera = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == AUDIO_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                status.setText(
                        "Microphone permission granted"
                );

            } else {

                status.setText(
                        "Microphone permission denied"
                );
            }
        }

        if (requestCode == CAMERA_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                status.setText(
                        "Camera permission granted"
                );

            } else {

                status.setText(
                        "Camera permission denied"
                );
            }
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

        closeCamera();

        if (voiceManager != null) {
            voiceManager.stopListening();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        closeCamera();

        if (voiceManager != null) {
            voiceManager.destroy();
            voiceManager = null;
        }

        if (voiceSpeaker != null) {
            voiceSpeaker.destroy();
            voiceSpeaker = null;
        }
    }
}
