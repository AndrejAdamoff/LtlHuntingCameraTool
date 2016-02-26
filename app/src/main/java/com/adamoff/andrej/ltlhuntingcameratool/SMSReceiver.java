package com.adamoff.andrej.ltlhuntingcameratool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class SMSReceiver extends BroadcastReceiver {
    public SMSReceiver() { }

    private Context mContext;
    private Intent mIntent;
    String action;

    @Override
    // Retrieve SMS
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        mIntent = intent;

        if (intent != null) {
            action = intent.getAction();

        //     else {};

        if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {

            String address = "null", str = "";

            android.telephony.SmsMessage[] msgs = getMessagesFromIntent(mIntent);
            if (msgs != null) {
                for (int i = 0; i < msgs.length; i++) {
                    address = msgs[i].getDisplayOriginatingAddress();
                    //     str += msgs[i].getMessageBody().toString();
                    //     str += "\n";
                }
            }

            //     String phone = cameraView.getPhone();
            //  if (address.equals(phone)){ //
            //      };

            // запуск сервиса, который запускает диалоговое окно
            Intent sintent = new Intent(context, HuntingCameraService.class);
            sintent.putExtra("phone", address);
            sintent.putExtra("type", "SMS");
            //
            context.startService(sintent);

            //  cameraView a = new cameraView();
            //   new cameraView().startservice();

        }
    }
        //     throw new UnsupportedOperationException("Not yet implemented");
    }

    public android.telephony.SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        android.telephony.SmsMessage[] msgs = new android.telephony.SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = android.telephony.SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }
}

