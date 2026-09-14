package com.ultron.assistant;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import android.view.Surface;
import android.view.TextureView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ultron.assistant.actions.AppLauncher;
import com.ultron.assistant.actions.PhoneActions;
import com.ultron.assistant.communication.CommunicationManager;
import com.ultron.assistant.core.CommandManager;
import com.ultron.assistant.core.TechnicianKnowledge;
import com.ultron.assistant.memory.MemoryManager;
import com.ultron.assistant.memory.OwnerProfile;
import com.ultron.assistant.contacts.ContactManager;
import com.ultron.assistant.voice.VoiceManager;
import com.ultron.assistant.drone.DroneBridge;
import com.ultron.assistant.voice.VoiceSpeaker;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private static final int CAMERA_REQUEST = 100;
    private static final int CALL_REQUEST = 103;
    private static final int AUDIO_REQUEST = 101;
    private static final int REQUEST_CONTACTS = 104;

    private TextureView preview;
    private TextView status;

    private CameraDevice camera;
    private CameraCaptureSession cameraSession;

    private VoiceManager voiceManager;
    private VoiceSpeaker voiceSpeaker;
    private CommandManager commandManager;
    private TechnicianKnowledge technicianKnowledge;
    private MemoryManager memoryManager;
    private ContactManager contactManager;

    // ULTRON voice states
    private boolean ultronActive = true;
    private boolean ultronWaiting = false;
    private boolean continuousListening = true;
    private String lastUserCommand = "";
    private AppLauncher appLauncher;
    private PhoneActions phoneActions;
    private CommunicationManager communicationManager;
    private OwnerProfile ownerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        commandManager = new CommandManager();
        technicianKnowledge = new TechnicianKnowledge(this);
        memoryManager = new MemoryManager(this);
        contactManager = new ContactManager(this);
        appLauncher = new AppLauncher(this);
        phoneActions = new PhoneActions(this);
        communicationManager = new CommunicationManager(this);
        ownerProfile = new OwnerProfile(this);

        createUserInterface();
        createVoiceManager();
        voiceSpeaker = new VoiceSpeaker(this);
        requestRequiredPermissions();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private android.os.Handler dashboardHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private TextView systemData;
    private final Runnable dashboardUpdater = new Runnable() {
        @Override
        public void run() {
            updateDashboardData();
            dashboardHandler.postDelayed(this, 3000);
        }
    };

    private void createUserInterface() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(android.graphics.Color.rgb(3, 8, 14));

        android.widget.ScrollView scroll = new android.widget.ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(14), dp(14), dp(14), dp(24));

        TextView title = new TextView(this);
        title.setText("ULTRON");
        title.setTextSize(32);
        title.setGravity(android.view.Gravity.CENTER);
        title.setTextColor(android.graphics.Color.WHITE);
        title.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText("PERSONAL AI COMMAND CENTER");
        subtitle.setTextSize(13);
        subtitle.setGravity(android.view.Gravity.CENTER);
        subtitle.setTextColor(android.graphics.Color.LTGRAY);
        subtitle.setPadding(0, 0, 0, dp(10));

        TextView core = new TextView(this);
        core.setText("◉");
        core.setTextSize(78);
        core.setGravity(android.view.Gravity.CENTER);
        core.setTextColor(android.graphics.Color.WHITE);
        core.setBackground(makePanel(
                android.graphics.Color.rgb(8, 25, 38),
                android.graphics.Color.rgb(2, 10, 17)
        ));

        TextView coreLabel = new TextView(this);
        coreLabel.setText("AI CORE  •  STANDBY");
        coreLabel.setTextSize(14);
        coreLabel.setGravity(android.view.Gravity.CENTER);
        coreLabel.setTextColor(android.graphics.Color.LTGRAY);
        coreLabel.setPadding(0, dp(6), 0, dp(10));

        TextView statusPanel = new TextView(this);
        statusPanel.setText("●  ULTRON ONLINE");
        statusPanel.setTextSize(19);
        statusPanel.setGravity(android.view.Gravity.CENTER);
        statusPanel.setTextColor(android.graphics.Color.WHITE);
        statusPanel.setPadding(dp(10), dp(12), dp(10), dp(12));
        statusPanel.setBackground(makePanel(
                android.graphics.Color.rgb(10, 30, 43),
                android.graphics.Color.rgb(4, 13, 21)
        ));
        status = statusPanel;

        systemData = new TextView(this);
        systemData.setText(
                "SYSTEM DATA\n" +
                "Battery: --%     Brightness: --%\n" +
                "Network: CHECKING     Time: --"
        );
        systemData.setTextSize(15);
        systemData.setTextColor(android.graphics.Color.LTGRAY);
        systemData.setPadding(dp(14), dp(12), dp(14), dp(12));
        systemData.setBackground(makePanel(
                android.graphics.Color.rgb(7, 20, 30),
                android.graphics.Color.rgb(3, 10, 17)
        ));

        TextView info = new TextView(this);
        info.setText(
                "VOICE  •  READY\n" +
                "SYSTEM  •  ONLINE\n" +
                "AI CORE  •  STANDBY"
        );
        info.setTextSize(14);
        info.setGravity(android.view.Gravity.CENTER);
        info.setTextColor(android.graphics.Color.LTGRAY);
        info.setPadding(0, dp(12), 0, dp(12));

        preview = new TextureView(this);
        preview.setBackgroundColor(android.graphics.Color.rgb(5, 12, 19));

        TextView cameraLabel = new TextView(this);
        cameraLabel.setText("CAMERA / VISION");
        cameraLabel.setTextSize(12);
        cameraLabel.setGravity(android.view.Gravity.CENTER);
        cameraLabel.setTextColor(android.graphics.Color.LTGRAY);
        cameraLabel.setPadding(0, dp(5), 0, dp(8));

        Button activate = new Button(this);
        activate.setText("ACTIVATE");

        Button sleep = new Button(this);
        sleep.setText("SLEEP");

        Button rearCameraButton = new Button(this);
        rearCameraButton.setText("OPEN REAR CAMERA");

        Button frontCameraButton = new Button(this);
        frontCameraButton.setText("OPEN FRONT CAMERA");

        Button voiceButton = new Button(this);
        voiceButton.setText("START VOICE COMMAND");

        Button youtubeButton = new Button(this);
        youtubeButton.setText("OPEN YOUTUBE");

        Button settingsButton = new Button(this);
        settingsButton.setText("OPEN SETTINGS");

        Button homeButton = new Button(this);
        homeButton.setText("GO HOME");

        panel.addView(title);
        panel.addView(subtitle);
        panel.addView(core, new LinearLayout.LayoutParams(-1, dp(150)));
        panel.addView(coreLabel);
        panel.addView(statusPanel);
        panel.addView(systemData);
        panel.addView(info);
        panel.addView(cameraLabel);
        panel.addView(preview, new LinearLayout.LayoutParams(-1, dp(180)));

        panel.addView(activate);
        panel.addView(sleep);
        panel.addView(rearCameraButton);
        panel.addView(frontCameraButton);
        panel.addView(voiceButton);
        panel.addView(youtubeButton);
        panel.addView(settingsButton);
        panel.addView(homeButton);

        scroll.addView(panel);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);

        dashboardHandler.post(dashboardUpdater);

        activate.setOnClickListener(v -> {
            ultronActive = true;
            ultronWaiting = false;
            status.setText("●  ULTRON ACTIVE");
            startVoice();
        });

        sleep.setOnClickListener(v -> {
            ultronActive = false;
            ultronWaiting = true;
            if (voiceManager != null) voiceManager.stopListening();
            if (voiceSpeaker != null) voiceSpeaker.stop();
            status.setText("●  ULTRON SLEEPING");
        });

        rearCameraButton.setOnClickListener(v -> openCamera(false));
        frontCameraButton.setOnClickListener(v -> openCamera(true));
        voiceButton.setOnClickListener(v -> startVoice());
        youtubeButton.setOnClickListener(v -> openYouTube());
        settingsButton.setOnClickListener(v -> openSettings());
        homeButton.setOnClickListener(v -> goHome());
    }

    private android.graphics.drawable.GradientDrawable makePanel(int top, int bottom) {
        return new android.graphics.drawable.GradientDrawable(
                android.graphics.drawable.GradientDrawable.Orientation.TL_BR,
                new int[]{top, bottom}
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

        if (checkSelfPermission(
                Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.CALL_PHONE
                    },
                    CALL_REQUEST
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

        ultronActive = true;
        ultronWaiting = false;
        status.setText("Starting voice recognition...");
        voiceManager.startListening();
    }

    private String consumeLastUserCommand() {
        String command = lastUserCommand == null ? "" : lastUserCommand.trim();
        lastUserCommand = "";
        return command;
    }

    private void respond(String message) {

        status.setText(message);

        if (memoryManager != null
                && message != null
                && !message.trim().isEmpty()) {

            String userCommand = consumeLastUserCommand();

            if (!userCommand.isEmpty()) {
                memoryManager.addConversation(
                        userCommand,
                        message
                );
            }
        }

        if (voiceManager != null) {
            voiceManager.stopListening();
        }

        if (voiceSpeaker != null) {
            voiceSpeaker.speak(
                    message,
                    () -> runOnUiThread(() -> {
                        if (ultronActive && !ultronWaiting
                                && voiceManager != null) {
                            voiceManager.startListening();
                        }
                    })
            );
        }
    }

    private void handleDroneCommand(String command) {
        String c = command.toLowerCase().trim();

        boolean drone = c.contains("drone");
        boolean known = c.contains("take off") || c.contains("takeoff")
                || c.contains("forward") || c.contains("back")
                || c.contains("left") || c.contains("right")
                || c.contains("up") || c.contains("down")
                || c.contains("land") || c.contains("emergency stop")
                || c.equals("stop")
                || c.contains("drone battery")
                || c.contains("drone status");

        if (!drone && !known) return;

        new Thread(() -> {
            String result = DroneBridge.sendCommand(c);
            runOnUiThread(() -> respond(result));
        }).start();
    }

    private void handleVoiceCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            respond("I did not hear a command.");
            return;
        }

        handleDroneCommand(command);
        String droneCheck = command.toLowerCase().trim();
        if (droneCheck.contains("drone")
                || droneCheck.contains("take off") || droneCheck.contains("takeoff")
                || droneCheck.contains("forward") || droneCheck.contains("back")
                || droneCheck.contains("left") || droneCheck.contains("right")
                || droneCheck.contains("up") || droneCheck.contains("down")
                || droneCheck.contains("land") || droneCheck.contains("emergency stop")
                || droneCheck.equals("stop")
                || droneCheck.contains("drone battery")
                || droneCheck.contains("drone status")) {
            return;
        }


        if (command == null || command.trim().isEmpty()) {
            status.setText("No voice command received");
            return;
        }

        status.setText("You said: " + command);
        lastUserCommand = command.trim();

        CommandManager.CommandType commandType =
                commandManager.parseCommand(command);

        // Always allow ULTRON state commands
        if (commandType != CommandManager.CommandType.ULTRON_ON
                && commandType != CommandManager.CommandType.ULTRON_OFF
                && commandType != CommandManager.CommandType.ULTRON_WAIT
                && commandType != CommandManager.CommandType.ULTRON_WAKE) {

            if (!ultronActive) {
                status.setText("ULTRON is off. Press Start Voice Command to activate.");
                return;
            }

            if (ultronWaiting) {
                status.setText("ULTRON is waiting. Say ULTRON WAKE to continue.");
                return;
            }
        }

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

            case WHO_AM_I:
                respond("You are " + ownerProfile.getOwnerName() + ", my primary user.");
                break;

            case WHO_IS_YOUR_OWNER:
                respond("My primary user is " + ownerProfile.getOwnerName() + ".");
                break;

            case SET_OWNER_NAME:
                setOwnerName(command);
                break;

            case ULTRON_ON:
                ultronActive = true;
                ultronWaiting = false;
                respond("ULTRON is now active.");
                break;

            case ULTRON_OFF:
                ultronActive = false;
                ultronWaiting = false;
                if (voiceManager != null) {
                    voiceManager.stopListening();
                }

                if (voiceSpeaker != null) {
                    voiceSpeaker.stop();
                }

                status.setText("ULTRON is off. Press Start Voice Command to activate.");
                break;

            case ULTRON_WAIT:
                ultronWaiting = true;

                respond("ULTRON is waiting. Say ULTRON WAKE to continue.");
                break;

            case ULTRON_WAKE:
                ultronActive = true;
                ultronWaiting = false;

                respond("ULTRON is active again.");
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
                handleCallCommand(command);
                break;

            case SMS:
                handleSmsCommand(command);
                break;

            case OPEN_CAMERA:
                status.setText("Opening rear camera...");
                openCamera(true);
                break;

            case FLASHLIGHT_ON:
                setFlashlight(true);
                break;

            case FLASHLIGHT_OFF:
                setFlashlight(false);
                break;

            case VOLUME_UP:
                increaseVolume();
                break;

            case VOLUME_DOWN:
                decreaseVolume();
                break;

            case OPEN_WIFI_SETTINGS:
                openWifiSettings();
                break;

            case OPEN_BLUETOOTH_SETTINGS:
                openBluetoothSettings();
                break;

            case GET_TIME:
                tellCurrentTime();
                break;

            case GET_DATE:
                tellCurrentDate();
                break;

            case GET_BATTERY:
                tellBatteryLevel();
                break;

            case OPEN_ANY_APP:
                openRequestedApp(command);
                break;


            case OPEN_SECURITY_SETTINGS:
                openSecuritySettings();
                break;

            case BRIGHTNESS_UP:
                openBrightnessSettings();
                break;

            case BRIGHTNESS_DOWN:
                openBrightnessSettings();
                break;

            case SILENT_MODE:
                setRingerMode(AudioManager.RINGER_MODE_SILENT, "Silent mode enabled.");
                break;

            case RINGER_MODE:
                setRingerMode(AudioManager.RINGER_MODE_NORMAL, "Normal ringer mode enabled.");
                break;

            case VIBRATE_MODE:
                setRingerMode(AudioManager.RINGER_MODE_VIBRATE, "Vibrate mode enabled.");
                break;

            case OPEN_BATTERY_SAVER:
                openBatterySaverSettings();
                break;

            case OPEN_MOBILE_NETWORK_SETTINGS:
                openMobileNetworkSettings();
                break;

            case OPEN_AIRPLANE_MODE_SETTINGS:
                openAirplaneModeSettings();
                break;

            case OPEN_LOCATION_SETTINGS:
                openLocationSettings();
                break;

            case OPEN_NOTIFICATION_SETTINGS:
                openNotificationSettings();
                break;

            case OPEN_DIALER:
                openDialer();
                break;

            case OPEN_SMS_APP:
                openSmsApp();
                break;

            case OPEN_WHATSAPP:
                if (appLauncher != null && appLauncher.openWhatsApp()) {
                    respond("Opening WhatsApp.");
                } else {
                    respond("WhatsApp is not installed.");
                }
                break;

            case OPEN_PLAY_STORE:
                if (appLauncher != null && appLauncher.openPlayStore()) {
                    respond("Opening Google Play.");
                } else {
                    respond("I could not open Google Play.");
                }
                break;

            case OPEN_WHATSAPP_CHAT:
                handleWhatsAppNumberCommand(command);
                break;

            case WHATSAPP_MESSAGE:
                handleWhatsAppMessageCommand(command);
                break;

            case OPEN_MUSIC_APP:
                openMusicApp();
                break;

            case CHECK_WIFI_STATUS:
                tellWifiStatus();
                break;

            case GET_CHARGING_STATUS:
                tellChargingStatus();
                break;

            case OPEN_APP_SETTINGS:
                openAppSettings();
                break;

            case FOLLOW_UP:
                handleFollowUpCommand(command);
                break;

            case MEMORY_LAST_RESPONSE:
                String lastAnswer = memoryManager.getLastAssistant();
                if (lastAnswer == null || lastAnswer.trim().isEmpty()) {
                    respond("I do not have a previous answer yet.");
                } else {
                    respond(lastAnswer);
                }
                break;

            case MEMORY_CONTEXT:
                String recentContext = memoryManager.getRecentContext();
                if (recentContext == null || recentContext.trim().isEmpty()) {
                    respond("I do not have enough conversation context yet.");
                } else {
                    respond("Here is our recent conversation: " + recentContext);
                }
                break;

            case MEMORY_LAST:
                String lastUser = memoryManager.getLastUser();

                if (lastUser.isEmpty()) {
                    respond("I do not have any conversation memory yet.");
                } else {
                    respond("Your last question was: " + lastUser);
                }
                break;

            case MEMORY_CLEAR:
                memoryManager.clearMemory();
                lastUserCommand = "";
                respond("Conversation memory has been cleared.");
                break;

            case FEATURE_INFO:
                tellFeatures();
                break;


            case UNKNOWN:
            default:
                String knowledgeAnswer = technicianKnowledge.search(command);

                if (knowledgeAnswer != null && !knowledgeAnswer.trim().isEmpty()) {
                    respond(knowledgeAnswer);
                } else {
                    respond("Sorry, I did not understand your question. Please try again.");
                }
                break;
        }
    }





    private void tellFeatures() {
        String message =
                "मैं ULTRON हूँ। मेरे वर्तमान फीचर्स हैं: "
                + "YouTube, Instagram और PUBG खोलना। "
                + "Settings और Camera खोलना। "
                + "Google पर search करना। "
                + "Call और SMS करना। "
                + "Torch control करना। "
                + "Volume control करना। "
                + "WiFi और Bluetooth settings खोलना। "
                + "Time, Date और Battery की जानकारी देना। "
                + "Security, Network, Location और Notification settings खोलना। "
                + "और अन्य installed apps खोलने की कोशिश करना।";

        respond(message);
    }

    private void openSecuritySettings() {
        try {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_SECURITY_SETTINGS
            ));
            respond("Opening security settings.");
        } catch (Exception e) {
            respond("Sorry, I could not open security settings.");
        }
    }

    private void openBrightnessSettings() {
        try {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_DISPLAY_SETTINGS
            ));
            respond("Opening display settings.");
        } catch (Exception e) {
            respond("Sorry, I could not open display settings.");
        }
    }

    private void setRingerMode(int mode, String message) {
        AudioManager audioManager =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (audioManager == null) {
            respond("Sorry, I could not control ringer mode.");
            return;
        }

        try {
            audioManager.setRingerMode(mode);
            respond(message);
        } catch (Exception e) {
            respond("Sorry, I could not change ringer mode.");
        }
    }

    private void openBatterySaverSettings() {
        try {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_BATTERY_SAVER_SETTINGS
            ));
            respond("Opening battery saver settings.");
        } catch (Exception e) {
            respond("Sorry, I could not open battery saver.");
        }
    }

    private void openMobileNetworkSettings() {
        try {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_WIRELESS_SETTINGS
            ));
            respond("Opening mobile network settings.");
        } catch (Exception e) {
            respond("Sorry, I could not open mobile network settings.");
        }
    }

    private void openAirplaneModeSettings() {
        try {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_AIRPLANE_MODE_SETTINGS
            ));
            respond("Opening airplane mode settings.");
        } catch (Exception e) {
            respond("Sorry, I could not open airplane mode settings.");
        }
    }

    private void openLocationSettings() {
        try {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
            ));
            respond("Opening location settings.");
        } catch (Exception e) {
            respond("Sorry, I could not open location settings.");
        }
    }

    private void openNotificationSettings() {
        try {
            startActivity(new Intent(
                    android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
            ));
            respond("Opening notification settings.");
        } catch (Exception e) {
            respond("Sorry, I could not open notification settings.");
        }
    }

    private void openDialer() {
        try {
            startActivity(new Intent(Intent.ACTION_DIAL));
            respond("Opening dialer.");
        } catch (Exception e) {
            respond("Sorry, I could not open dialer.");
        }
    }

    private void openSmsApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
            startActivity(intent);
            respond("Opening messaging app.");
        } catch (Exception e) {
            respond("Sorry, I could not open messaging app.");
        }
    }

    private void openMusicApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_MUSIC);
            startActivity(intent);
            respond("Opening music app.");
        } catch (Exception e) {
            respond("Sorry, I could not open music app.");
        }
    }

    private void tellWifiStatus() {
        try {
            android.net.ConnectivityManager cm =
                    (android.net.ConnectivityManager)
                            getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm == null) {
                respond("Sorry, I could not check Wi-Fi.");
                return;
            }

            android.net.Network network = cm.getActiveNetwork();

            android.net.NetworkCapabilities capabilities =
                    network == null
                            ? null
                            : cm.getNetworkCapabilities(network);

            boolean connected =
                    capabilities != null &&
                    capabilities.hasTransport(
                            android.net.NetworkCapabilities.TRANSPORT_WIFI
                    );

            respond(connected
                    ? "Wi-Fi is connected."
                    : "Wi-Fi is not connected.");

        } catch (Exception e) {
            respond("Sorry, I could not check Wi-Fi status.");
        }
    }

    private void tellChargingStatus() {
        IntentFilter filter =
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        Intent batteryStatus =
                registerReceiver(null, filter);

        if (batteryStatus == null) {
            respond("Sorry, I could not check charging status.");
            return;
        }

        int batteryState =
                batteryStatus.getIntExtra(
                        BatteryManager.EXTRA_STATUS,
                        -1
                );

        if (batteryState ==
                BatteryManager.BATTERY_STATUS_CHARGING) {
            respond("Your phone is charging.");
        } else if (batteryState ==
                BatteryManager.BATTERY_STATUS_FULL) {
            respond("Battery is fully charged.");
        } else {
            respond("Your phone is not charging.");
        }
    }

    private void openAppSettings() {
        try {
            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_APPLICATION_DETAILS_SETTINGS
            );

            intent.setData(
                    android.net.Uri.parse(
                            "package:" + getPackageName()
                    )
            );

            startActivity(intent);
            respond("Opening ULTRON app settings.");

        } catch (Exception e) {
            respond("Sorry, I could not open app settings.");
        }
    }

    private void openRequestedApp(String command) {

        String text = command.toLowerCase().trim();

        if (text.contains("youtube") || text.contains("यूट्यूब")) {
            boolean opened = appLauncher.openYouTube();
            if (opened) respond("Opening YouTube.");
            else respond("Sorry, I could not open YouTube.");
            return;
        }

        if (text.contains("instagram") || text.contains("इंस्टाग्राम")) {
            boolean opened = appLauncher.openInstagram();
            if (opened) respond("Opening Instagram.");
            else respond("Instagram is not installed.");
            return;
        }

        if (text.contains("pubg")) {
            boolean opened = appLauncher.openPUBG();
            if (opened) respond("Opening PUBG.");
            else respond("PUBG is not installed.");
            return;
        }

        if (text.contains("chrome")
                || text.contains("क्रोम")) {

            boolean opened =
                    appLauncher.openChrome();

            if (opened) {
                respond("Opening Chrome.");
            } else {
                respond("Chrome is not installed.");
            }

            return;
        }

        if (text.contains("whatsapp")
                || text.contains("व्हाट्सएप")
                || text.contains("वॉट्सऐप")) {

            boolean opened =
                    appLauncher.openWhatsApp();

            if (opened) {
                respond("Opening WhatsApp.");
            } else {
                respond("WhatsApp is not installed.");
            }

            return;
        }

        respond(
                "Sorry, I do not know which app to open yet."
        );
    }

    private void setOwnerName(String command) {

        String name = extractOwnerName(command);

        if (name.isEmpty()) {
            respond("Sorry, I did not catch the name. Please say: my name is <your name>.");
            return;
        }

        ownerProfile.setOwnerName(name);

        respond("Okay, I will remember your name as " + name + ".");
    }

    private String extractOwnerName(String command) {

        if (command == null) {
            return "";
        }

        String raw = command.trim();

        // English: "my name is <name>"
        Matcher englishMatcher =
                Pattern.compile("(?i)my name is\\s+(.+)").matcher(raw);

        if (englishMatcher.find()) {
            return cleanExtractedName(englishMatcher.group(1));
        }

        // Hinglish (Latin script): "mera naam <name> hai"
        Matcher hinglishMatcher =
                Pattern.compile("(?i)mera naam\\s+(.+?)\\s+hai").matcher(raw);

        if (hinglishMatcher.find()) {
            return cleanExtractedName(hinglishMatcher.group(1));
        }

        // Hindi (Devanagari script): "मेरा नाम <name> है"
        Matcher hindiMatcher =
                Pattern.compile("मेरा नाम\\s+(.+?)\\s+है").matcher(raw);

        if (hindiMatcher.find()) {
            return cleanExtractedName(hindiMatcher.group(1));
        }

        return "";
    }

    private String cleanExtractedName(String name) {

        if (name == null) {
            return "";
        }

        String cleaned = name.trim();

        cleaned = cleaned.replaceAll("[.!?,]+$", "");

        return cleaned.trim();
    }


    private void openWifiSettings() {

        try {
            Intent intent =
                    new Intent(
                            android.provider.Settings.ACTION_WIFI_SETTINGS
                    );

            startActivity(intent);

            respond("Opening Wi-Fi settings.");

        } catch (Exception e) {
            respond("Sorry, I could not open Wi-Fi settings.");
        }
    }

    private void openBluetoothSettings() {

        try {
            Intent intent =
                    new Intent(
                            android.provider.Settings.ACTION_BLUETOOTH_SETTINGS
                    );

            startActivity(intent);

            respond("Opening Bluetooth settings.");

        } catch (Exception e) {
            respond("Sorry, I could not open Bluetooth settings.");
        }
    }


    private void increaseVolume() {

        AudioManager audioManager =
                (AudioManager) getSystemService(
                        Context.AUDIO_SERVICE
                );

        if (audioManager != null) {

            audioManager.adjustVolume(
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI
            );

            respond("Increasing volume.");

        } else {
            respond("Sorry, I could not control the volume.");
        }
    }

    private void decreaseVolume() {

        AudioManager audioManager =
                (AudioManager) getSystemService(
                        Context.AUDIO_SERVICE
                );

        if (audioManager != null) {

            audioManager.adjustVolume(
                    AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_SHOW_UI
            );

            respond("Decreasing volume.");

        } else {
            respond("Sorry, I could not control the volume.");
        }
    }

    private void tellCurrentTime() {

        java.text.SimpleDateFormat format =
                new java.text.SimpleDateFormat(
                        "hh:mm a",
                        java.util.Locale.getDefault()
                );

        String time =
                format.format(new java.util.Date());

        respond("The current time is " + time);
    }

    private void tellCurrentDate() {

        java.text.SimpleDateFormat format =
                new java.text.SimpleDateFormat(
                        "EEEE, dd MMMM yyyy",
                        java.util.Locale.getDefault()
                );

        String date =
                format.format(new java.util.Date());

        respond("Today is " + date);
    }

    private void tellBatteryLevel() {

        IntentFilter filter =
                new IntentFilter(
                        Intent.ACTION_BATTERY_CHANGED
                );

        Intent batteryStatus =
                registerReceiver(null, filter);

        if (batteryStatus == null) {
            respond("Sorry, I could not check the battery level.");
            return;
        }

        int level =
                batteryStatus.getIntExtra(
                        BatteryManager.EXTRA_LEVEL,
                        -1
                );

        int scale =
                batteryStatus.getIntExtra(
                        BatteryManager.EXTRA_SCALE,
                        -1
                );

        if (level < 0 || scale <= 0) {
            respond("Sorry, I could not read the battery level.");
            return;
        }

        int batteryPercent =
                Math.round(
                        level * 100f / scale
                );

        respond(
                "Battery level is "
                        + batteryPercent
                        + " percent."
        );
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
                "Calling: " + phoneNumber
        );

        boolean called =
                communicationManager.call(phoneNumber);

        if (!called) {
            status.setText(
                    "Call permission required or call failed"
            );
        }
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
        if (text == null) return "";

        Pattern pattern = Pattern.compile(
                "(?<!\\d)(\\+?\\d[\\d\\s-]{6,}\\d)(?!\\d)"
        );

        Matcher matcher = pattern.matcher(text);

        if (!matcher.find()) return "";

        String number = matcher.group(1).replaceAll("[\\s-]", "");

        if (number.startsWith("+")) {
            String digits = number.substring(1);
            if (digits.length() >= 7 && digits.length() <= 15) {
                return "+" + digits;
            }
            return "";
        }

        String digits = number.replaceAll("[^0-9]", "");

        if (digits.length() >= 7 && digits.length() <= 15) {
            return digits;
        }

        return "";
    }

    private String extractMessageAfterNumber(String command) {
        if (command == null) return "";

        Matcher matcher = Pattern.compile(
                "(?<!\\d)(\\+?\\d[\\d\\s-]{6,}\\d)(?!\\d)"
        ).matcher(command);

        if (!matcher.find()) return "";

        String message = command.substring(matcher.end()).trim();

        message = message
                .replaceFirst("(?i)^\\s*(message|msg|sms)\\s*", "")
                .replaceFirst("(?i)^\\s*send\\s*", "")
                .replaceFirst("^\\s*(मैसेज|संदेश|एसएमएस)\\s*", "")
                .replaceFirst("^\\s*(भेजो|भेज)\\s*", "")
                .trim();

        return message;
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


    private void setFlashlight(boolean enabled) {

        if (checkSelfPermission(
                Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    CAMERA_REQUEST
            );

            respond("Camera permission is required for flashlight.");
            return;
        }

        try {

            CameraManager manager =
                    (CameraManager) getSystemService(
                            CAMERA_SERVICE
                    );

            if (manager == null) {
                respond("Flashlight is not available.");
                return;
            }

            String selectedCamera = null;

            for (String id : manager.getCameraIdList()) {

                android.hardware.camera2
                        .CameraCharacteristics info =
                        manager.getCameraCharacteristics(id);

                Boolean flashAvailable =
                        info.get(
                                android.hardware.camera2
                                        .CameraCharacteristics
                                        .FLASH_INFO_AVAILABLE
                        );

                Integer facing =
                        info.get(
                                android.hardware.camera2
                                        .CameraCharacteristics
                                        .LENS_FACING
                        );

                if (Boolean.TRUE.equals(flashAvailable)
                        && facing != null
                        && facing ==
                        android.hardware.camera2
                                .CameraCharacteristics
                                .LENS_FACING_BACK) {

                    selectedCamera = id;
                    break;
                }
            }

            if (selectedCamera == null) {
                respond("This device does not have a flashlight.");
                return;
            }

            manager.setTorchMode(
                    selectedCamera,
                    enabled
            );

            if (enabled) {
                respond("Flashlight turned on.");
            } else {
                respond("Flashlight turned off.");
            }

        } catch (Exception e) {
            respond("Sorry, I could not control the flashlight.");
        }
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

        if (requestCode == CALL_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                status.setText(
                        "Call permission granted"
                );

            } else {

                status.setText(
                        "Call permission denied"
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
        dashboardHandler.removeCallbacks(dashboardUpdater);


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

    private void requestContactsPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT >= 23
                && checkSelfPermission(
                android.Manifest.permission.READ_CONTACTS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            android.Manifest.permission.READ_CONTACTS
                    },
                    REQUEST_CONTACTS
            );

            respond("Contacts permission is required to find a contact.");
        }
    }


    private void handleCallCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            respond("Please tell me a contact name or phone number.");
            return;
        }

        String original = command.trim();
        String number = extractPhoneNumber(original);

        if (!number.isEmpty()) {
            openDialerWithNumber(number);
            return;
        }

        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CONTACTS
            );
            respond("I need contacts permission to find that person.");
            return;
        }

        String name = original
                .replaceAll("(?i)\\bplease\\b", "")
                .replaceAll("(?i)\\bcall\\b", "")
                .replaceAll("(?i)\\bphone\\b", "")
                .replaceAll("(?i)\\bdial\\b", "")
                .replace("कॉल", "")
                .replace("फोन", "")
                .replace("डायल", "")
                .replace("करो", "")
                .replace("करना है", "")
                .trim();

        if (name.isEmpty()) {
            respond("Please tell me the contact name.");
            return;
        }

        String contactNumber = contactManager.findPhoneNumber(name);

        if (contactNumber.isEmpty()) {
            respond("I could not find that contact.");
        } else {
            openDialerWithNumber(contactNumber);
        }
    }



    private void openDialerWithNumber(String number) {

        try {
            android.content.Intent intent =
                    new android.content.Intent(
                            android.content.Intent.ACTION_CALL);

            intent.setData(
                    android.net.Uri.parse(
                            "tel:" + android.net.Uri.encode(number)
                    )
            );

            startActivity(intent);
            respond("Calling now.");
        } catch (Exception e) {
            respond("I could not open the phone dialer.");
        }
    }


    private void handleWhatsAppNumberCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            respond("Please tell me the WhatsApp number.");
            return;
        }

        String number = extractPhoneNumber(command);

        if (number.isEmpty()) {
            respond("Please tell me a valid WhatsApp number.");
            return;
        }

        if (appLauncher != null
                && appLauncher.openWhatsAppChat(number, "")) {
            respond("WhatsApp chat opened. Please review before sending.");
        } else {
            respond("I could not open that WhatsApp chat.");
        }
    }

    private void handleFollowUpCommand(String command) {

        String current = command == null ? "" : command.trim();

        if (current.isEmpty()) {
            respond("Please ask your follow-up question.");
            return;
        }

        String previous = memoryManager == null
                ? ""
                : memoryManager.getLastUser();

        if (previous == null || previous.trim().isEmpty()) {
            respond("I need your previous question to understand this follow-up.");
            return;
        }

        String combined = previous.trim() + " " + current;

        String knowledgeAnswer = technicianKnowledge.search(combined);

        if (knowledgeAnswer != null && !knowledgeAnswer.trim().isEmpty()) {
            respond(knowledgeAnswer);
        } else {
            respond("I understand this is a follow-up, but I need a little more detail.");
        }
    }


    private void handleWhatsAppMessageCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            respond("Please tell me the WhatsApp number and message.");
            return;
        }

        String number = extractPhoneNumber(command);

        if (number.isEmpty()) {
            respond("Please tell me a valid WhatsApp number.");
            return;
        }

        String message = extractMessageAfterNumber(command);

        if (message.isEmpty()) {
            respond("Please tell me the message after the phone number.");
            return;
        }

        if (appLauncher != null
                && appLauncher.openWhatsAppChat(number, message)) {
            respond("WhatsApp chat opened. Review the message before sending.");
        } else {
            respond("I could not open that WhatsApp chat.");
        }
    }


    private void handleSmsCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            respond("Please tell me the contact or phone number.");
            return;
        }

        String original = command.trim();
        String number = extractPhoneNumber(original);

        if (!number.isEmpty()) {
            String message = extractMessageAfterNumber(original);
            openSmsComposer(number, message);
            return;
        }

        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CONTACTS
            );
            respond("I need contacts permission to find that person.");
            return;
        }

        String name = original
                .replaceAll("(?i)send\\s+(a\\s+)?(sms|message)\\s*", "")
                .replaceAll("(?i)text\\s*", "")
                .replaceAll("(?i)\\bmessage\\b.*$", "")
                .replace("मैसेज", "")
                .replace("एसएमएस", "")
                .replace("भेजो", "")
                .replace("भेज", "")
                .replace("करो", "")
                .trim();

        if (name.isEmpty()) {
            respond("Please tell me the contact name.");
            return;
        }

        String contactNumber = contactManager.findPhoneNumber(name);

        if (contactNumber.isEmpty()) {
            respond("I could not find that contact.");
        } else {
            openSmsComposer(contactNumber, "");
        }
    }



    private void openSmsComposer(String number, String body) {

        try {
            android.content.Intent intent =
                    new android.content.Intent(
                            android.content.Intent.ACTION_SENDTO);

            intent.setData(
                    android.net.Uri.parse(
                            "smsto:" + android.net.Uri.encode(number)
                    )
            );

            if (body != null && !body.isEmpty()) {
                intent.putExtra("sms_body", body);
            }

            startActivity(intent);

            respond("SMS composer opened. Review the message and send it yourself.");
        } catch (Exception e) {
            respond("I could not open the SMS composer.");
        }
    }





    // =========================================================
    // ULTRON DOUBLE-CLAP ACTIVATION
    // Two quick claps activate ULTRON.
    // Works while the ULTRON activity is open.
    // =========================================================

    private android.media.AudioRecord clapRecorder;
    private Thread clapThread;
    private volatile boolean clapRunning = false;
    private long lastClapTime = 0L;
    private int clapCount = 0;

    private void startDoubleClapDetector() {
        if (clapRunning) return;

        try {
            int sampleRate = 16000;
            int minBuffer = android.media.AudioRecord.getMinBufferSize(
                    sampleRate,
                    android.media.AudioFormat.CHANNEL_IN_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT
            );

            if (minBuffer <= 0) return;

            clapRecorder = new android.media.AudioRecord(
                    android.media.MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    android.media.AudioFormat.CHANNEL_IN_MONO,
                    android.media.AudioFormat.ENCODING_PCM_16BIT,
                    Math.max(minBuffer * 2, 4096)
            );

            clapRunning = true;

            clapThread = new Thread(() -> {
                short[] buffer = new short[1024];

                try {
                    clapRecorder.startRecording();

                    while (clapRunning && !isFinishing()) {
                        int read = clapRecorder.read(buffer, 0, buffer.length);

                        if (read <= 0) continue;

                        long energy = 0;

                        for (int i = 0; i < read; i++) {
                            energy += Math.abs((int) buffer[i]);
                        }

                        long average = energy / read;
                        long now = System.currentTimeMillis();

                        // Strong short sound = possible clap
                        if (average > 3500) {

                            if (now - lastClapTime > 120
                                    && now - lastClapTime < 1000) {

                                clapCount++;

                                if (clapCount >= 2) {
                                    clapCount = 0;
                                    lastClapTime = 0;

                                    runOnUiThread(
                                            this::activateFromDoubleClap
                                    );

                                } else {
                                    lastClapTime = now;
                                }

                            } else if (now - lastClapTime >= 1000) {
                                clapCount = 1;
                                lastClapTime = now;
                            }
                        }
                    }

                } catch (Exception ignored) {
                }
            }, "ULTRON-ClapDetector");

            clapThread.start();

        } catch (Exception ignored) {
        }
    }

    private void stopDoubleClapDetector() {
        clapRunning = false;

        if (clapRecorder != null) {
            try {
                clapRecorder.stop();
            } catch (Exception ignored) {
            }

            try {
                clapRecorder.release();
            } catch (Exception ignored) {
            }

            clapRecorder = null;
        }

        clapThread = null;
        clapCount = 0;
        lastClapTime = 0L;
    }

    private void activateFromDoubleClap() {
        try {
            if (voiceManager != null) {
                voiceManager.stopListening();
            }
        } catch (Exception ignored) {
        }

        ultronActive = true;
        ultronWaiting = false;

        respond(
                "हाँ " + ownerProfile.getOwnerName() + ", I'm ready. Welcome to ULTRON Assistant. "
                        + "बताइए, आपकी क्या सेवा करूँ?"
        );
    }

    // =========================================================
    // BETTER BASIC CONVERSATION
    // =========================================================

    private String getNaturalConversationReply(String command) {

        if (command == null) return "";

        String t = command.toLowerCase(java.util.Locale.getDefault()).trim();

        if (t.equals("hello")
                || t.equals("hi")
                || t.contains("hello ultron")
                || t.contains("हेलो")
                || t.contains("नमस्ते")
                || t.contains("सलाम")) {

            return "हाँ " + ownerProfile.getOwnerName() + ", मैं यहाँ हूँ। बताइए, आपकी क्या सेवा करूँ?";
        }

        if (t.contains("how are you")
                || t.contains("कैसे हो")
                || t.contains("कैसी हो")) {

            return "मैं बढ़िया हूँ " + ownerProfile.getOwnerName() + " और आपके काम के लिए तैयार हूँ।";
        }

        if (t.contains("thank you")
                || t.contains("thanks")
                || t.contains("धन्यवाद")
                || t.contains("शुक्रिया")) {

            return "आपका स्वागत है " + ownerProfile.getOwnerName() + "।";
        }

        if (t.contains("good morning")
                || t.contains("सुप्रभात")) {

            return "Good morning " + ownerProfile.getOwnerName() + ". मैं तैयार हूँ। आज क्या करना है?";
        }

        if (t.contains("good night")
                || t.contains("शुभ रात्रि")) {

            return "Good night " + ownerProfile.getOwnerName() + ". जब जरूरत हो, ULTRON तैयार मिलेगा।";
        }

        if (t.contains("what can you do")
                || t.contains("तुम क्या कर सकते हो")
                || t.contains("अपने फीचर बताओ")) {

            return "मैं voice commands समझ सकता हूँ, apps खोल सकता हूँ, "
                    + "call और SMS कर सकता हूँ, Google search कर सकता हूँ, "
                    + "camera और phone settings संभाल सकता हूँ और AC technician "
                    + "knowledge में आपकी मदद कर सकता हूँ।";
        }

        return "";
    }

}
