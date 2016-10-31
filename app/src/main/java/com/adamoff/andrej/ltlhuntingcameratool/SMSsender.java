package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSsender extends Service {
    public void onCreate() {
        super.onCreate();
        //   Log.d(LOG_TAG, "MyService onCreate");
        // es = Executors.newFixedThreadPool(1);
        //someRes = new Object();
    }
    String phoneNumber;
    BroadcastReceiver rec;

    public int onStartCommand(Intent intent, int flags, int startId) {
        //   Log.d(LOG_TAG, "MyService onStartCommand");
        //   int time = intent.getIntExtra("time", 1);
        //  MyRun mr = new MyRun(time, startId);
        // es.execute(mr);
        //  Intent intent1 = getIntent();
        try {phoneNumber = intent.getStringExtra("number");}
        catch (Exception e){return 0;}
        String message = intent.getStringExtra("text");
        //   sndSMS("0654519797", "Ира!");
        sndSMS(phoneNumber, message);
//        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    public void sndSMS(String phoneNumber, final String message) {
        String SENT="SMS_SENT";
        String DELIVERED="SMS_DELIVERED";

        PendingIntent sentPI= PendingIntent.getBroadcast(this,0,
                new Intent(SENT),0);

        PendingIntent deliveredPI= PendingIntent.getBroadcast(this,0,
                new Intent(DELIVERED),0);

//---когда SMS отправлено---
        rec = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1){
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(),"SMS sent",Toast.LENGTH_LONG).show();
                        stopSelf();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(),"Generic failure. Check your balance",Toast.LENGTH_SHORT).show();
                        stopSelf();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(),"No service", Toast.LENGTH_SHORT).show();
                        stopSelf();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(),"Null PDU", Toast.LENGTH_SHORT).show();
                        stopSelf();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(),"Radio off", Toast.LENGTH_SHORT).show();
                        stopSelf();
                        break;
                }
            }
        };
        registerReceiver(rec, new IntentFilter(SENT));

/* //---когда SMS доставлено---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1){
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(),"SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(),"SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        },new IntentFilter(DELIVERED)); */

        SmsManager sms = SmsManager.getDefault();
     //   sms.sendTextMessage(phoneNumber,null, message, sentPI, deliveredPI);

    try    {sms.sendTextMessage(phoneNumber,null, message, sentPI, null); }
    catch (Exception e){
        Toast.makeText(getApplicationContext(), "Can not send SMS", Toast.LENGTH_LONG).show();
    }
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber); // phone number to send
        values.put("date", System.currentTimeMillis()+"");
        values.put("read", "1"); // if you want to mark is as unread set to 0
        values.put("type", "2"); // 2 means sent message
        values.put("body", message);
        Uri uri = Uri.parse("content://sms/");
        Uri rowUri = getBaseContext().getContentResolver().insert(uri,values);

      /*  try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(),
                    "SMS sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS not sent", Toast.LENGTH_LONG).show();
        } */
    }

    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(rec);
    }
}
