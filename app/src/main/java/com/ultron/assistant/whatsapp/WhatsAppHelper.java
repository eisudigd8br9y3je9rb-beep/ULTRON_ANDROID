package com.ultron.assistant.whatsapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class WhatsAppHelper {

    private final Context context;

    public WhatsAppHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean openWhatsApp() {
        return openPackage("com.whatsapp")
                || openPackage("com.whatsapp.w4b");
    }

    public boolean openWhatsAppBusiness() {
        return openPackage("com.whatsapp.w4b");
    }

    public boolean openChat(String number, String message) {
        if (number == null || number.trim().isEmpty()) return false;

        String clean = number.replaceAll("[^0-9]", "");

        if (clean.length() < 7 || clean.length() > 15) {
            return false;
        }

        String url = "https://wa.me/" + clean;

        if (message != null && !message.trim().isEmpty()) {
            url += "?text=" + Uri.encode(message.trim());
        }

        try {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
            );

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean openPackage(String packageName) {
        try {
            Intent intent =
                    context.getPackageManager()
                            .getLaunchIntentForPackage(packageName);

            if (intent == null) return false;

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
