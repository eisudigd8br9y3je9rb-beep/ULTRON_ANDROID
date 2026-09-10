package com.ultron.assistant.contacts;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactManager {

    private final Context context;

    public ContactManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public String findPhoneNumber(String spokenName) {

        if (spokenName == null) {
            return "";
        }

        String name = spokenName.trim();

        if (name.isEmpty()) {
            return "";
        }

        try {

            Cursor cursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    },
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?",
                    new String[]{"%" + name + "%"},
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            );

            if (cursor == null) {
                return "";
            }

            try {

                if (cursor.moveToFirst()) {

                    int numberIndex = cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    );

                    if (numberIndex >= 0) {

                        String number = cursor.getString(numberIndex);

                        if (number != null && !number.trim().isEmpty()) {
                            return number.trim();
                        }
                    }
                }

            } finally {
                cursor.close();
            }

        } catch (Exception ignored) {
        }

        return "";
    }
}
