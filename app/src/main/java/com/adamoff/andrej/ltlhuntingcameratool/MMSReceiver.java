package com.adamoff.andrej.ltlhuntingcameratool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class MMSReceiver extends BroadcastReceiver {
    public MMSReceiver() {
    }
    String incomingNumber;
    private final String DEBUG_TAG = getClass().getSimpleName().toString();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (action.equals("android.provider.Telephony.WAP_PUSH_RECEIVED") && type.equals("application/vnd.wap.mms-message")) {
System.out.println("8888 MMS: MMSReceiver");
                Bundle bundle = intent.getExtras();

                //   Log.d(DEBUG_TAG, "bundle " + bundle);
                SmsMessage[] msgs = null;
                String str = "";
                int contactId = -1;
                String address;

                if (bundle != null) {

                    byte[] buffer = bundle.getByteArray("data");
                    //    Log.d(DEBUG_TAG, "buffer " + buffer);
                    incomingNumber = new String(buffer);
                    int indx = incomingNumber.indexOf("/TYPE");
                    if (indx > 0 && (indx - 15) > 0) {
                        int newIndx = indx - 15;
                        incomingNumber = incomingNumber.substring(newIndx, indx);
                        indx = incomingNumber.indexOf("+");
                        if (indx > 0) {
                            incomingNumber = incomingNumber.substring(indx);

                        //    Log.d(DEBUG_TAG, "Mobile Number: " + incomingNumber);
                        //    Toast.makeText(context, incomingNumber, Toast.LENGTH_LONG).show();

                         //   Log.d(DEBUG_TAG, "Mobile Number: " + incomingNumber);
                            // запуск сервиса, который запускает диалоговое окно
 System.out.println("8888 MMS: incomingnumber "+incomingNumber);
                            Intent sintent = new Intent(context, HuntingCameraService.class);
                            sintent.putExtra("phone", incomingNumber);
                            sintent.putExtra("type", "MMS");
                            //
                           context.startService(sintent);
                        }
                    }
                }
            }
        }
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
  //      throw new UnsupportedOperationException("Not yet implemented");
    }


    protected void showNotification(int contactId, String message) {
        //Display notification...
     //   Toast.makeText(context, incomingNumber, Toast.LENGTH_LONG).show();
    }

}
