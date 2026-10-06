package com.ultron.assistant.core;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallTriggerListener extends PhoneStateListener {

    public interface CallListener {
        void onIncomingCall(String phoneNumber);
    }

    private final CallListener listener;

    public CallTriggerListener(CallListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        if (state == TelephonyManager.CALL_STATE_RINGING) {
            if (listener != null) {
                listener.onIncomingCall(
                        phoneNumber == null ? "Unknown" : phoneNumber
                );
            }
        }
    }
}
