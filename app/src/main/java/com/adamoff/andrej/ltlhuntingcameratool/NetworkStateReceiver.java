package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.t.ltlhuntingcameratool.R;

// ------------ класс поверка состояния подключения к интернет ---------------------
public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkStateReceiver";
    SQLiteDatabase db;
    String smtpToMail, smtpToPwd, push;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.d(TAG, "Network connectivity change");

        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

      //      if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
              if (ni != null && ni.isConnected()) {
       //       Log.i(TAG, "Network " + ni.getTypeName() + " connected");
                // проверяем активирован ли push:
                MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(context); // так передаётся контекст из mainactivity в myservice, иначе db не открывается
                db = dbHelper.getReadableDatabase();
                //    String push = "", smtpToMail = "";
                Cursor cur = db.query("smtp", new String[]{"push", "smtpToMail", "smtpToPassword"}, null, null, null, null, null);
                if (cur.moveToFirst()) {
           //         do {
                        smtpToMail = cur.getString(cur.getColumnIndex("smtpToMail"));
                        smtpToPwd = cur.getString(cur.getColumnIndex("smtpToPassword"));
                        push = cur.getString(cur.getColumnIndex("push"));
           //             if (push!=null) break;
            //        } while (cur.moveToNext());
                }
                cur.close();
                db.close();
         //       Toast.makeText(context, "Network State Receiver: "+smtpToMail+" "+smtpToPwd, Toast.LENGTH_LONG).show();

                if (push.equals("enabled")) {
             // проверяем нужно ли запускать alarm. Для этого проверяем существует ли уже pending intent:
            //    if (PendingIntent.getService(context, 0, new Intent(context, IMAPListener.class).putExtra("action", "restart"), PendingIntent.FLAG_NO_CREATE) == null) {
                if (PendingIntent.getService(context, 0, new Intent(context, IMAPListener.class).putExtra("action", "noop"), PendingIntent.FLAG_NO_CREATE) == null) {
               //   Intent intent2 = new Intent(context, IMAPListener.class).putExtra("action", "restart");
                    Intent intent2 = new Intent(context, IMAPListener.class).putExtra("action", "noop");
                    PendingIntent pi = PendingIntent.getService(context, 0, intent2, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Service.ALARM_SERVICE);
                 //   alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 60*60*1000, 60*60*1000, pi);
                    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 4*60*1000, 4*60*1000, pi);
                    System.out.println("7777 NetworkStateReceiver: Alarm manager set");
//                    Toast.makeText(context, "Network State Receiver: alarm manager set", Toast.LENGTH_LONG).show();

                }
                    context.startService(new Intent(context, IMAPListener.class).putExtra("action", "startidle").putExtra("smtpToMail", smtpToMail).putExtra("smtpToPwd", smtpToPwd));
                  //  Intent intent2 = new Intent(context, IMAPListener.class).putExtra("action", "restart");
                    System.out.println("7777 NetworkStateReceiver: start idle");
//                    Toast.makeText(context, "Network State Receiver: startidle "+smtpToMail+" "+smtpToPwd, Toast.LENGTH_LONG).show();

                }
                //else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                //    Log.d(TAG, "There's no network connectivity");
               // }
            } else {
                context.startService(new Intent(context, IMAPListener.class).putExtra("action", "stopidle"));

                  SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                  boolean online = sp.getBoolean("alwaysonline", false);
                  if (online) {     // запускаем нотификации только, если активирован wakelock
                      long when = System.currentTimeMillis(); // системное время
                      NotificationCompat.Builder notification = new NotificationCompat.Builder(context); // Создаем экземпляр уведомления, и передаем ему наши параметры
                      notification.setTicker(context.getString(R.string.imap_ticker))
                              .setSmallIcon(R.drawable.ltl65104040)// Иконка для уведомления)
                              .setWhen(when);
                      //Настраиваем звук для уведомления и его закрытие после нажатия по нему пользователем:
                      notification.setDefaults(Notification.DEFAULT_SOUND);
                      notification.setAutoCancel(true);
                      notification.setContentTitle(context.getString(R.string.imap_title));
                      notification.setContentText(context.getString(R.string.imap_text));
                      NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); // Создаем экземпляр менеджера уведомлений
                      mNotificationManager.notify(9999, notification.build()); // запуск уведомления
                  }
            }
        }
    }
}
