package com.ultron.assistant.communication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.Locale;

public class CommunicationManager {

    private final Context context;

    public CommunicationManager(Context context) {
        this.context = context;
    }

    public String findContactNumber(String contactName) {

        if (contactName == null
                || contactName.trim().isEmpty()) {
            return "";
        }

        if (context.checkSelfPermission(
                android.Manifest.permission.READ_CONTACTS
        ) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }

        String target =
                normalize(contactName);

        Cursor cursor = null;

        try {

            cursor =
                    context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                            },
                            null,
                            null,
                            null
                    );

            if (cursor == null) {
                return "";
            }

            String partialNumber = "";

            while (cursor.moveToNext()) {

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                )
                        );

                String number =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                        );

                if (name == null
                        || number == null) {
                    continue;
                }

                String normalizedName =
                        normalize(name);

                if (normalizedName.equals(target)) {
                    return cleanNumber(number);
                }

                if (normalizedName.contains(target)
                        || target.contains(normalizedName)) {

                    if (partialNumber.isEmpty()) {
                        partialNumber =
                                cleanNumber(number);
                    }
                }
            }

            return partialNumber;

        } catch (Exception e) {
            return "";

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean call(String phoneNumber) {

        if (phoneNumber == null
                || phoneNumber.trim().isEmpty()) {
            return false;
        }

        if (context.checkSelfPermission(
                android.Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_CALL,
                            Uri.parse(
                                    "tel:"
                                            + Uri.encode(phoneNumber)
                            )
                    );

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(intent);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean callContact(String contactName) {

        String number =
                findContactNumber(contactName);

        if (number.isEmpty()) {
            return false;
        }

        return call(number);
    }

    public void openDialer(String phoneNumber) {

        try {

            Intent intent;

            if (phoneNumber == null
                    || phoneNumber.trim().isEmpty()) {

                intent =
                        new Intent(
                                Intent.ACTION_DIAL
                        );

            } else {

                intent =
                        new Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse(
                                        "tel:"
                                                + Uri.encode(phoneNumber)
                                )
                        );
            }

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(intent);

        } catch (Exception ignored) {
        }
    }

    public void composeSms(
            String phoneNumber,
            String message
    ) {

        if (phoneNumber == null
                || phoneNumber.trim().isEmpty()) {
            return;
        }

        try {

            Intent intent =
                    new Intent(
                            Intent.ACTION_SENDTO,
                            Uri.parse(
                                    "smsto:"
                                            + Uri.encode(phoneNumber)
                            )
                    );

            if (message != null) {

                intent.putExtra(
                        "sms_body",
                        message
                );
            }

            intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
            );

            context.startActivity(intent);

        } catch (Exception ignored) {
        }
    }

    public boolean composeSmsToContact(
            String contactName,
            String message
    ) {

        String number =
                findContactNumber(contactName);

        if (number.isEmpty()) {
            return false;
        }

        composeSms(
                number,
                message
        );

        return true;
    }

    private String cleanNumber(String number) {

        if (number == null) {
            return "";
        }

        return number.replaceAll(
                "[^0-9+]",
                ""
        );
    }

    private String normalize(String text) {

        if (text == null) {
            return "";
        }

        return text
                .toLowerCase(Locale.ROOT)
                .replaceAll(
                        "[^a-z0-9\\u0900-\\u097F]",
                        ""
                )
                .trim();
    }
}
