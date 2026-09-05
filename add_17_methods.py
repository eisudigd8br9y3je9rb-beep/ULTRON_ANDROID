from pathlib import Path

p = Path("app/src/main/java/com/ultron/assistant/MainActivity.java")
s = p.read_text()

marker = "    private void openRequestedApp(String command) {"

if marker not in s:
    raise SystemExit("ERROR: Marker not found.")

if "private void openSecuritySettings()" in s:
    raise SystemExit("ERROR: Methods already exist.")

methods = r'''
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
'''

s = s.replace(marker, methods + "\n" + marker, 1)
p.write_text(s)

print("SUCCESS: 17 feature methods added safely.")

