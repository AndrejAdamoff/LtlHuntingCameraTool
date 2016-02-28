package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.t.ltlhuntingcameratool.R;
import com.sun.mail.imap.IMAPFolder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;

public class IMAPListener extends Service {
    public IMAPListener() {
        }

    IMAPFolder folder=null;
    Store store;

    imap im=null;
    LinkedList<MessageBean> listMessages;
    ArrayList<String> attach;
    String To1, ToPWD1;
    Map<String, Integer> hm;
    ArrayList<String> spphone = new ArrayList<String>();
    ArrayList<String> sname = new ArrayList<String>();
    String smtpToMail, smtpToPwd, push; // = "remoteguard2013@gmail.com";
    SQLiteDatabase db;
   public static boolean idle = false;
    Runnable r,noop;
    MessageCountListener mcl;

    Thread t=null, tnoop=null;
    String action;
    int m,l=0,n = 0;
    boolean f,x;
    boolean stop;

    PowerManager pm;
    PowerManager.WakeLock wl;

   // AlarmManager alarmManager; PendingIntent pi1, pi2;


    @Override
    public void onCreate() {
        super.onCreate();

        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakelock");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        m = 0;
        stop = false;

 //       DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
 //       dpm.lockNow();


        //       Toast.makeText(getApplicationContext(), "Imap service is started", Toast.LENGTH_LONG).show();

    try { action = intent.getStringExtra("action");

        if (action.equals("noop")) {

            if (tnoop == null) {
                tnoop = new Thread(noop);
                tnoop.setDaemon(true);
                System.out.println("7777 new thread tnoop is created");
            } else {if (!t.isInterrupted()) t.interrupt();
                System.out.println("7777 thread t is interrupted");
            }
              tnoop.start();
        }

        if (action.equals("restart")) {   // при изменении/добавлении камеры

            l++;
      //      Toast.makeText(getApplicationContext(), "IMAP service " l="+l, Toast.LENGTH_LONG).show();

            System.out.println("7777 IMAP service restarted");

 /*           SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean online = sp.getBoolean("alwaysonline", false);
            if (online) {
                pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakelock");
                wl.acquire();
            }
*/
            // пересоздаём потоки:
            if(t == null){
                t = new Thread(r);
                t.setDaemon(true);
                System.out.println("7777 new thread t is created");
            } else { if (!t.isInterrupted()) {t.interrupt(); t = null;
                System.out.println("7777 old thread t is interrupted and deleted");}
                else if (t!=null) t = null;
                System.out.println("7777 restart thread t already was interrupted before");
                // пересоздаём поток:
                t = new Thread(r);
                t.setDaemon(true);
                System.out.println("7777 new thread t is created");
            }

            if (im != null) {
                System.out.println("7777 restart im here");
                // всё останавливаем, запускается само через stopSelf
                im.cancel(false);
                t.start();
                System.out.println("7777 t started again here");
 //       Toast.makeText(getApplicationContext(), "IMAP service restarted, stop previous im, autostart", Toast.LENGTH_LONG).show();

                // для остановки alarm создаётся точно такой же pi  и для него выполняется cancel:
                //        Intent intent2 = new Intent(IMAPListener.this, IMAPListener.class).putExtra("action", "restart");
                //        PendingIntent pi = PendingIntent.getService(IMAPListener.this, 0, intent2, 0);
                //        AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
                //        alarmManager.cancel(pi);
                //        pi.cancel();
            } else {
 // не должно сюда попадать! нет запуска alarm manager
 //       Toast.makeText(getApplicationContext(), "IMAP service restarted, create new im", Toast.LENGTH_LONG).show();
                System.out.println("7777 Popali kuda ne nuzhno");
                // запускаем с нуля

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean online = sp.getBoolean("alwaysOn", false);
                if (online) {
                    if (!wl.isHeld()) wl.acquire();
  //                  Toast.makeText(getApplicationContext(), "WakeLock activated ne nuzhno ", Toast.LENGTH_LONG).show();
                    System.out.println("7777 WakeLock activated ne nuzhno");}
                else {if (wl.isHeld()) wl.release();
  //                  Toast.makeText(getApplicationContext(), "WakeLock deactivated ne nuzhno", Toast.LENGTH_LONG).show();
                    System.out.println("7777 WakeLock deactivated ne nuzhno");}

                MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(this); // так передаётся контекст из mainactivity в myservice, иначе db не открывается
                db = dbHelper.getReadableDatabase();
                //    String push = "", smtpToMail = "";
                Cursor cur = db.query("smtp", new String[]{"push", "smtpToMail", "smtpToPassword"}, null, null, null, null, null);
                if (cur.moveToFirst()) {
                    do {
                        push = cur.getString(cur.getColumnIndex("push"));
                        smtpToMail = cur.getString(cur.getColumnIndex("smtpToMail"));
                        smtpToPwd = cur.getString(cur.getColumnIndex("smtpToPassword"));
                    } while (cur.moveToNext());
                }
                cur.close();
                db.close();
                if (push.equals("enabled")) {
                    System.out.println("7777 Starting im boot...");
                    im = new imap();
                    //     im.execute(smtpToMail, smtpToPwd);
                    //     idle = true;
                    im.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, smtpToMail, smtpToPwd);
                }

/*            try {  im.cancel(false);
            }
            catch (Exception e) {e.printStackTrace();
                System.out.println("7777 im cancelling exception");
            // значит im не был

            }

                t.start();
*/
            }
        }

        if (action.equals("wake")){

            if (wl.isHeld()) System.out.println("7777 WakeLock isHeld");
            else System.out.println("7777 WakeLock is NOT held");

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean online = sp.getBoolean("alwaysOn", false);
            if (online) {
                if (!wl.isHeld()) {wl.acquire();
   //                 Toast.makeText(getApplicationContext(), "WakeLock activated idle", Toast.LENGTH_LONG).show();
                    System.out.println("7777 WakeLock activated idle"); }
            }
            else {if (wl.isHeld()) wl.release();
   //             Toast.makeText(getApplicationContext(), "WakeLock deactivated idle", Toast.LENGTH_LONG).show();
                System.out.println("7777 WakeLock deactivated idle");}
        }

        if (action.equals("stopidle")) {

          // отключаем wake lock, если он запущен:
/*          if (wl.isHeld()) { wl.release(); wl = null;
        Toast.makeText(getApplicationContext(), "WakeLock deactivated stopidle", Toast.LENGTH_LONG).show();
              System.out.println("WakeLock deactivated stopidle");
          }
            else System.out.println("WakeLock is NOT deactivated stopidle");
*/

            // для остановки alarm создаётся точно такой же pi  и для него выполняется cancel:
        //    Intent intent2 = new Intent(IMAPListener.this, IMAPListener.class).putExtra("action", "restart");
            Intent intent2 = new Intent(IMAPListener.this, IMAPListener.class).putExtra("action", "noop");
            PendingIntent pi = PendingIntent.getService(IMAPListener.this, 0, intent2, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
            alarmManager.cancel(pi);
            pi.cancel();
            System.out.println("7777 Alarm manager cancelled stopidle");

    //        Toast.makeText(getApplicationContext(), "IMAP stopidle, alarm cancelled ", Toast.LENGTH_LONG).show();

            // пересоздаём потоки:
            if(t == null){
                t = new Thread(r);
                t.setDaemon(true);
                System.out.println("7777 new thread t is created");
            } else { // вычищаем старый потк и создаём новый:
                if (!t.isInterrupted()) {t.interrupt(); t = null;
                System.out.println("7777 old thread t is interrupted and deleted");}
            else if (t!=null) t = null;
                System.out.println("7777 restart thread t already was interrupted before");
                // пересоздаём поток:
                t = new Thread(r);
                t.setDaemon(true);
                System.out.println("7777 new thread t is created");
            }

            if (im != null) {
                // всё останавливаем, запускается само через stopSelf
                im.cancel(false);
                t.start();
             System.out.println("7777 thread t for stopping is started");}

            else {
 //               Toast.makeText(getApplicationContext(), "IMAP: нечего останавливать", Toast.LENGTH_LONG).show();
                System.out.println("7777 im was null");
                t = null;
                System.out.println("7777 thread t is nulled");
         //       if (wl!=null) {wl.release(); wl = null;}
            }
        }

        if (action.equals("startidleboot")) {  // от ServiceRestartReceiver
            // проверяем активирован ли push:
            MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(this); // так передаётся контекст из mainactivity в myservice, иначе db не открывается
            db = dbHelper.getReadableDatabase();
            //    String push = "", smtpToMail = "";
            Cursor cur = db.query("smtp", new String[]{"push", "smtpToMail", "smtpToPassword"}, null, null, null, null, null);
            if (cur.moveToFirst()) {
                do {
                    push = cur.getString(cur.getColumnIndex("push"));
                    smtpToMail = cur.getString(cur.getColumnIndex("smtpToMail"));
                    smtpToPwd = cur.getString(cur.getColumnIndex("smtpToPassword"));
                } while (cur.moveToNext());
            }
            cur.close();
            db.close();
            if (push.equals("enabled")) {

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean online = sp.getBoolean("alwaysOn", false);
                if (online) {
                if (!wl.isHeld()) {wl.acquire();
    //                Toast.makeText(getApplicationContext(), "WakeLock activated idleboot", Toast.LENGTH_LONG).show();
                    System.out.println("7777 WakeLock activated idleboot"); }
                }
                else {if (wl.isHeld()) wl.release();
   //                 Toast.makeText(getApplicationContext(), "WakeLock deactivated idleboot", Toast.LENGTH_LONG).show();
                    System.out.println("7777 WakeLock deactivated idleboot");}

                if (wl.isHeld()) System.out.println("7777 Checking after idleboot: WakeLock isHeld");
                  else System.out.println("7777 Checking after idleboot: WakeLock is NOT held");

            System.out.println("7777 Starting im idleboot...");

 //       Toast.makeText(getApplicationContext(), "IMAP startedboot", Toast.LENGTH_LONG).show();

                if(t == null){
                    t = new Thread(r);
                    t.setDaemon(true);
                    System.out.println("7777 new thread t is created");
                } else { t.interrupt();
                    System.out.println("7777 thread t is interrupted");}

                if (tnoop ==null) {
                    tnoop = new Thread(noop);
                    tnoop.setDaemon(true);
                    System.out.println("7777 new thread tnoop is created");
                } else {
                    tnoop.interrupt();
                    System.out.println("7777 thread tnoop is interrupted");}

                im = new imap();
                //     im.execute(smtpToMail, smtpToPwd);
                //     idle = true;
                im.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, smtpToMail, smtpToPwd);
            }
        }

        if (action.equals("startidle")) {

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean online = sp.getBoolean("alwaysOn", false);
            if (online) {
                if (!wl.isHeld()) {wl.acquire();
  //                  Toast.makeText(getApplicationContext(), "WakeLock activated startidle", Toast.LENGTH_LONG).show();
 //                   System.out.println("7777 WakeLock activated startidle");
                }
            }
            else {if (wl.isHeld()) wl.release();
 //               Toast.makeText(getApplicationContext(), "WakeLock deactivated startidle", Toast.LENGTH_LONG).show();
 //               System.out.println("7777 WakeLock deactivated startidle");
            }

            smtpToMail = intent.getStringExtra("smtpToMail");
            smtpToPwd = intent.getStringExtra("smtpToPwd");

            System.out.println("7777 IMAP startidle");

  //         Toast.makeText(getApplicationContext(), "IMAP startidle", Toast.LENGTH_LONG).show();

/*            try{alarmManager.cancel(pi1);} catch (Exception e) {e.printStackTrace();}

            Intent intent1 = new Intent(getApplicationContext(), IMAPListener.class).putExtra("action", "restart");
            pi1 = PendingIntent.getService(getApplicationContext(), 1, intent1, 0);
            alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 30000, 30000, pi1);
            System.out.println("AlarmManager1 set");
            alarmManager.cancel(pi1);
            System.out.println("AlarmManager1 cancelled");
*/
            im = new imap();
            //   im.execute(smtpToMail, smtpToPwd);
            System.out.println("7777 Starting im...");
            //      if (im.getStatus() != AsyncTask.Status.RUNNING)
            im.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, smtpToMail, smtpToPwd); // выполняется в своем экзекьютере, т.к. async IMAP и async updatedbMMS не могут выполняться в параллельно

        }
    } catch (Exception e) {e.printStackTrace(); stopSelf();}

    //    return super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
        }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       throw new UnsupportedOperationException("Not yet implemented");
    }

    // класс асинхронной задачи:
    public class imap extends AsyncTask<String, Void, Void> {

        Timer mTimer;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            // ----------- нотификации ---------------------------------
            if (spphone.size() > 0) {

                // пробуем открыть файл:
                try {
                    hm = new HashMap<String, Integer>();
                    FileInputStream fis = openFileInput("notif.ser"); // если файла нет, то он создаётся
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    hm = (HashMap) ois.readObject();
                    ois.close();
                    fis.close();
                    //         Toast.makeText(addCameraAsync.this, "File open", Toast.LENGTH_LONG).show();
                } catch (Exception f) { // если нет такого файла, создаём hm:
                    //         Toast.makeText(addCameraAsync.this, "No file notif", Toast.LENGTH_LONG).show();
                    try {
                        hm = new HashMap<String, Integer>();
                        //      hm.put(spphone.get(0), 0);
                        //   File fileOne=new File("notif");
                    } catch (Exception d) {
                        //                  Toast.makeText(addCameraAsync.this, "Photo download failed", Toast.LENGTH_LONG).show();
                    }
                }

                if (hm.size() == 0) {
                    try {
                        hm.put(spphone.get(0), 0);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Photo download failed", Toast.LENGTH_LONG).show();
                    }
                }

                //      Log.d(TAG, "hm.size=" + hm.size());
                //         Toast.makeText(getApplicationContext(), "hm.size=" + hm.size(), Toast.LENGTH_LONG).show();

                //     Set<Map.Entry<String, Integer>> set = hm.entrySet();
                // обновляем hash map:
                Map.Entry pair;
                Iterator iter;
                int p = 0;
                for (int i = 0; i < spphone.size(); i++) {
                    //    Log.d(TAG, "spphone= " + spphone.get(i));
                    //        Log.d(TAG, "hm.get(0)="+hm.get(0));
                    iter = hm.entrySet().iterator();
                    while (iter.hasNext()) {
                        pair = (Map.Entry) iter.next();
                        //        Log.d(TAG, "key=" + pair.getKey());
                        //         Toast.makeText(getApplicationContext(), "getKey" + pair.getKey(), Toast.LENGTH_LONG).show();
                        if (spphone.get(i).equals(pair.getKey())) {
                            Integer l = hm.get(spphone.get(i));
                            //         Integer l = (Integer) pair.getValue();
                            int n = l.intValue(); //hm.get(spphone.get(i));
                            n++;
                            hm.put(spphone.get(i), n);
                            //     pair.setValue(n);
                            //       Log.d(TAG, "n=" + n);

                            //         Toast.makeText(getApplicationContext(), "n=" + n, Toast.LENGTH_LONG).show();
                            p = 0;
                            break;
                            //                Toast.makeText(addCameraAsync.this, "не равен", Toast.LENGTH_LONG).show();
                        } else {                      // уже есть такой номер
                            //                Toast.makeText(addCameraAsync.this, "равен", Toast.LENGTH_LONG).show();
                            p = 1; // признак нового элемента
                        }
                    }
                    if (p == 1) hm.put(spphone.get(i), 1);
                }
                // сохраняем обновлённый hash map в файл:
                try {
                    //        File fileOne=new File("notif");
                    FileOutputStream fos = null;
                    try {
                        fos = openFileOutput("notif.ser", Context.MODE_PRIVATE);
                    } catch (Exception h) {
                        h.printStackTrace();
           //             Toast.makeText(getApplicationContext(), "fos exception", Toast.LENGTH_LONG).show();
                    }
                    ObjectOutputStream oos = new ObjectOutputStream(fos);

                    oos.writeObject(hm);
                    oos.flush();
                    oos.close();
                    fos.close();
                    //         Toast.makeText(addCameraAsync.this, "File saved", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    //         Toast.makeText(addCameraAsync.this, "File not saved", Toast.LENGTH_LONG).show();
                }

                 /*      //      Log.d(TAG, "j="+j);
                       if (!spphone.get(i).equals(hm.get(j))) {
                           t.add(spphone.get(i)); // запоминаем новое имя в t
                           s.add(j, 1); // 1 = число новых имён
                       }
                   }
               } */

  /*              Log.d(TAG, "t.size"+t.size());
                if (t.size()==0) {t.add(spphone.get(0)); s.add(0);}
                Log.d(TAG, "t(0)="+t.get(0));

                for (int i = 0; i < spphone.size(); i++) {
                //    for (int j = 0; j < t.size(); j++){
                     int j =0;
                        while (j < t.size()) {
                            Log.d(TAG, "j="+j);
                        if (!spphone.get(i).equals(t.get(j))) {
                            t.add(spphone.get(i)); // запоминаем новое имя в t
                            s.add(j, 1); // 1 = число новых имён
                        }
                        else {
                            Log.d(TAG, "j="+j);
                            int n = s.get(j);
                            n++;
                            Log.d(TAG, "n="+n);
                            s.add(j, n); } // инкременируем число для каждого имени
                    j++;
                        }
                }  // теперь в массиве t есть все отличающиеся телефоны
                // в массиве s - число сообщений от каждого телефона
*/
                int icon = R.drawable.ltl65104040; // Иконка для уведомления
                //    CharSequence tickerText = "message from hunting camera";
                long when = System.currentTimeMillis(); // системное время
                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext()); // Создаем экземпляр уведомления, и передаем ему наши параметры
                notification.setTicker("New hunting camera photo")
                        .setSmallIcon(icon)
                        .setWhen(when);
                //   .build();
                //         Context context = getApplicationContext();
                //         CharSequence contentTitle = "New smtp photo from"; // Текст заголовка уведомления при развернутой строке статуса
                //Настраиваем звук для уведомления и его закрытие после нажатия по нему пользователем:
                notification.setDefaults(Notification.DEFAULT_SOUND);
                notification.setAutoCancel(true);

                //     int n=0;
                Iterator iter2 = hm.entrySet().iterator();
                Map.Entry<String, Integer> pair2;

                //    for (int j = 0; j < hm.size(); j++) {
                int j = 0;
                while (iter2.hasNext()) {
//                Log.d(TAG, "j="+j);
                    pair2 = (Map.Entry) iter2.next();

           /*         for (int i = 0; i < spphone.size(); i++) {
                        if (spphone.get(i).equals(t.get(j))) n++;
                    }
                    */
                    //            cation.setContentText(sname.get(i) + " " + spphone.get(i));
                    //       notification.setContentText(Integer.toString(s.get(j))+spphone.get(j));

                    // определяем имя камеры по телефону:
                    MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(getApplicationContext());
                    db = dbHelper.getWritableDatabase();
                    String table = "";
                    Cursor cur = db.query("cameras", new String[]{"name", "pphone"}, "pphone=?", new String[]{pair2.getKey()}, null, null, null);
                    if (cur.moveToFirst()) {
                        do {
                            table = cur.getString(cur.getColumnIndex("name"));
                        } while (cur.moveToNext());
                    }
                    cur.close();
                    db.close();

                    notification.setContentTitle(pair2.getValue() + " new email photo(s) from: ").setContentText(table + " " + pair2.getKey());

                    //     n=0;
                    // интент для перехода в cameraView:
                    Intent intent1 = new Intent(getApplicationContext(), cameraView.class); // Создаем экземпляр Intent
                    //         intent1.putExtra("tname", sname.get(i)).putExtra("phone", spphone.get(i)).putExtra("type", "smtp");
                    intent1.putExtra("phone", pair2.getKey())
                            .putExtra("tname", table)
                            .putExtra("type", "smtp");
                    //     Toast.makeText(addCameraAsync.this, "Notification intent "+ pair2.getKey(), Toast.LENGTH_LONG).show();
                    //    a = 1;
                    // передача стека активностей в интенте:
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                    // Adds the back stack
                    stackBuilder.addParentStack(cameraView.class);
                    // Adds the Intent to the top of the stack
                    stackBuilder.addNextIntent(intent1);

                    //         PendingIntent contentIntent = PendingIntent.getActivity(addCameraAsync.this, 0, intent1, 0);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(j, PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.setContentIntent(resultPendingIntent);
                    // запуск уведомления:
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); // Создаем экземпляр менеджера уведомлений
                    mNotificationManager.notify(j, notification.build()); // запуск уведомления
                    j++;
                }
            }
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            System.out.println("7777 im onCancelled...");
 //           Toast.makeText(getApplicationContext(), "Imap listener cancelled", Toast.LENGTH_LONG).show();

            stop=false;

/*            try {
                t.interrupt();
//            t = null;
          //      mTimer.cancel();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("7777 thread interrupt exception...");
            }
*/
            if (action.equals("stopidle")){
                t = null;
                System.out.println("7777 thread t is nulled");
                tnoop = null;
                System.out.println("7777 thread tnoop is nulled");
            }

            //    if (f && m<2) stopSelf(); // переподключаемся к IMAP 2 раза
            if (f) { f=false; m=0;
                // нотификация о невозможности подключения:
                long when = System.currentTimeMillis(); // системное время
                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext()); // Создаем экземпляр уведомления, и передаем ему наши параметры
                notification.setTicker(getString(R.string.imap_ticker))
                        .setSmallIcon(R.drawable.ltl65104040)// Иконка для уведомления)
                        .setWhen(when);
                //Настраиваем звук для уведомления и его закрытие после нажатия по нему пользователем:
                notification.setDefaults(Notification.DEFAULT_SOUND);
                notification.setAutoCancel(true);
                notification.setContentTitle(getString(R.string.imap_title));
                notification.setContentText(getString(R.string.imap_text));
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); // Создаем экземпляр менеджера уведомлений
                mNotificationManager.notify(99, notification.build()); // запуск уведомления
            }
            if (x){ x=false;
                System.out.println("7777 restarting service");
    //    Toast.makeText(getApplicationContext(), "IMAP samootval, folder or store was suddenly closed", Toast.LENGTH_LONG).show();

                stopSelf();   // sendBroadcast(new Intent("YouWillNeverKillMe"));
            }

            if (action.equals("restart")){

     //   Toast.makeText(getApplicationContext(), "IMAP onCancel cause:restart", Toast.LENGTH_LONG).show();
                System.out.println("7777 stopping service");
                stopSelf();   // sendBroadcast(new Intent("YouWillNeverKillMe"));
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            System.out.println("7777 doInBackground...");

            //         Properties props = System.getProperties();
   //         Session session = Session.getDefaultInstance(props, null);
   //         Store store = null;
   //         Folder inbox = null;

            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getInstance(props, null);

            try {
            //    IMAPStore imapStore = (IMAPStore) session.getStore("imaps");

                store = session.getStore();
                String imap = "imap." + params[0].substring(1 + params[0].indexOf("@"));
                store.connect(imap, 993, params[0], params[1]);

                folder = (IMAPFolder) store.getFolder("Inbox");
         //           folder.open(IMAPFolder.READ_WRITE);

            } catch (MessagingException me){
                System.out.println("7777 folder exection 1..."); // возникает когда баланс 0
                im.cancel(false);
                if (wl.isHeld())  f = true; // нотифицируем о потере связи только, если wakelock вкл.
          //      m++;
               }
            if (im.isCancelled()) return null;

          try {folder.open(IMAPFolder.READ_WRITE);} catch (MessagingException m){m.printStackTrace();}
            updateSMTP();
          try {folder.close(false);} catch (MessagingException m) {m.printStackTrace();}
            publishProgress();

          r = new Runnable() {
              @Override
              public void run() {

                  try {
                      stop = true;

                      folder.getMessageCount(); // чтобы выйти из idle
                //      im.cancel(false);
              /*        folder.removeMessageCountListener(mcl);
                      System.out.println("7777 folder closing...");
                      //  folder.close(false);
                      folder.close(false);
                      System.out.println("7777 folder closed");
                      store.close();
                      */
         //     im.cancel(false);
                  } catch (Exception e) {
                      System.out.println("7777 folder can not be closed...");
                  }

            //    t.interrupt();
              }
          };

  /*            mTimer = new Timer();
              mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try
                        {
                            folder.doCommand(new IMAPFolder.ProtocolCommand() {
                                public Object doCommand(IMAPProtocol p)
                                        throws ProtocolException {
                                    p.simpleCommand("NOOP", null);
                                    System.out.println("7777 NOOP");
                                    return null;
                                }
                            });
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                  }, 1000*60*1000, 1000*60*1000);
*/
            noop = new Runnable() {
                @Override
                public void run() {
                   try {
                       folder.getMessageCount();
                       n++;
                       System.out.println("7777 число алармов n= "+n);
                      /*  folder.doCommand(new IMAPFolder.ProtocolCommand() {
                            public Object doCommand(IMAPProtocol p)
                                    throws ProtocolException {
                                p.simpleCommand("NOOP", null);
                                System.out.println("7777 NOOP");
                                return null;
                            }
                        });
                        */
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("7777 NOOP messaging exception");
                        // рестартуем сервис:
                        im.cancel(true);
                        x=true;
                    }
                    tnoop.interrupt();
                }
            };

            while (!im.isCancelled()) {
                try {

                    if (!folder.isOpen()) {
                       try { folder.open(IMAPFolder.READ_WRITE);
                           System.out.println("7777 folder open");}
                       catch (MessagingException mmm){
                           System.out.println("7777 folder open messaging exception");
                           im.cancel(true);
                           x=true; // по этому флагу рестартует сервис
                           }

                    mcl = new MessageCountListener() {
                            public void messagesAdded(MessageCountEvent arg0) {
                                System.out.println("7777 New message was added");
                                updateSMTP();
                                if (im.isCancelled()) x =true; //return null;
                                publishProgress();
                            }
                            @Override
                            public void messagesRemoved(MessageCountEvent arg0) {
                            }
                        };
                        folder.addMessageCountListener(mcl);
                        System.out.println("7777 Listener added");
                      }
                    try  {
                        NotificationManager mNotificationManager1 = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); // Создаем экземпляр менеджера уведомлений
                        mNotificationManager1.cancel(99);  // удаление уведомления
                        mNotificationManager1.cancel(9999);
                        } catch (Exception e) {e.printStackTrace();}

                    System.out.println("7777 folder idling...");
                    folder.idle();
                    System.out.println("7777 idle done...");
                    if (!stop) if (!folder.isOpen() || !store.isConnected()) {im.cancel(true); x=true;}  // самоотвал, рестарт сервиса через х
                } catch (Exception e) { System.out.println("7777 Messaging exception");
                    im.cancel(true);
                    try {folder.close(false);
                        System.out.println("7777 folder closed messaging exception");
                        // restart service?
                        //action = "restart";
                    //    stopSelf();

                } catch (Exception mm) {
                    System.out.println("7777 folder close messaging exception");
                }
                    // если папка отвалилась, пробуем рестартовать сервис
                    //stopSelf();
                    x=true;
                }
            }
            // перед выходом из doinBackgroung закрываем папки:
            try {
                stop = true;
                //      im.cancel(false);
                folder.removeMessageCountListener(mcl);
                System.out.println("7777 folder closing...");
                //  folder.close(false);
                folder.close(false);
                System.out.println("7777 folder closed");
                store.close();
                //     im.cancel(false);
            } catch (Exception e) {
                System.out.println("7777 folder can not be closed...");
            }
             return null;
        }
    }


  //         }

//            if (keepAlive.isAlive()) {
//                keepAlive.interrupt();
//            }

/*           try {
                folder.close(false);
             //     processedFolder.close(false);
             //     invalidFolder.close(false);
            } catch (MessagingException e) {
                System.out.println("Error closing all the folders:");
                System.out.println(e.toString());
            }
*/
        //    return null;


  /*  public void keepAliveRunner(){
         while (!keepAlive.isInterrupted()) {

            try {
                Thread.sleep(60 * 1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                folder.doCommand(new IMAPFolder.ProtocolCommand() {
                    public Object doCommand(IMAPProtocol p)
                            throws ProtocolException {
                        p.simpleCommand("NOOP", null);
                        System.out.println("7777 NOOP");
                        return null;
                    }
                });
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
    */

    protected void updateSMTP () {

        listMessages = new LinkedList<>();

        String from, aphone;
        int m;

        try {        // находим все сообщения
            Message[] messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            attach = new ArrayList<String>();

            listMessages = getPart(messages, attach);

            //        m = listMessages.size();
            //     m = messages.length;
            //      m = inbox.getMessageCount();

            folder.setFlags(messages, new Flags(Flags.Flag.SEEN), true);

        }    //   catch (MessagingException e) {
        catch (Exception e) {
            e.printStackTrace();
            im.cancel(false);
        }
        if (im.isCancelled()) return;

        // определяем от каких камер пришли сообщения:

        spphone.clear();
        sname.clear();

        Iterator<MessageBean> it = listMessages.iterator();
        // перебираем по очереди сообщения:
        for (int i = 0; i < listMessages.size(); i++) {
             MessageBean mes = it.next();

             from = mes.getFrom();
            // выделяем адрес из скобок <>
            if (smtpToMail.contains("gmail")) from =  from.substring(from.indexOf ("<")+1, from.indexOf (">"));

            //        Cursor cur = db.query("cameras",new String[]{"name","pphone"}, "smtpFromMail=?",new String[]{from},null,null,null);
            MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(this); // так передаётся контекст из mainactivity в myservice, иначе db не открывается
            db = dbHelper.getWritableDatabase();

   //         db = MainActivity.dbHelper.getWritableDatabase();
            Cursor cur = db.query("cameras", new String[]{"name", "pphone", "smtpFromMail"}, null, null, null, null, null);
            if (cur.moveToFirst()) {
                do {
                    if (from.equals(cur.getString(cur.getColumnIndex("smtpFromMail")))) {
                        sname.add(cur.getString(cur.getColumnIndex("name")));
                        //    t.add(cur.getString(cur.getColumnIndex("name")));
                        spphone.add(cur.getString(cur.getColumnIndex("pphone")));
                        // сразу записываем путь к аттачменту:
                        aphone = "a" + (spphone.get(i).substring(1));
                        if (mes.getAttachments().size() > 0) {
                            for (int j = 0; j < mes.getAttachments().size(); j++) {
                                ContentValues cv = new ContentValues();
                                String date = mes.getDateSent();
                                cv.put("time", date);
                                String dat = mes.getAttachments().get(j);
                                cv.put("path", dat);
                                // вставляем новую строку в таблицу:
                                db.insert(aphone, null, cv);
                            }
                        }
                        break;
                    }
                }
                while (cur.moveToNext());
            } cur.close();
            db.close();
        }
    }

// проверка типа сообщений
private LinkedList<MessageBean> getPart (Message[] messages, ArrayList < String > attachments)throws MessagingException, IOException {
        LinkedList<MessageBean> listMessages = new LinkedList<MessageBean>();
        SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        //  MessageBean message = null;
        for (int i =0; i< messages.length; i++) {
        attachments.clear();
        if (messages[i].isMimeType("text/plain")) {
        MessageBean message = new MessageBean(messages[i].getMessageNumber(), MimeUtility.decodeText(messages[i].getSubject()), messages[i].getFrom()[0].toString(), null, String.valueOf((messages[i].getSentDate().getTime())/1000), (String)messages[i].getContent(), false, null);
        listMessages.add(message);
        //        m++;
        } else if (messages[i].isMimeType("multipart/*")) {
        Multipart mp = (Multipart)messages[i].getContent();
        //    MessageBean message = null;
        MessageBean message = new MessageBean(messages[i].getMessageNumber(), messages[i].getSubject(), messages[i].getFrom()[0].toString(), null, String.valueOf((messages[i].getSentDate().getTime())/1000), null, false, null);
        for (int j = 0; j < mp.getCount(); j++) {
        Part part = mp.getBodyPart(j);
        if ((part.getFileName() == null || part.getFileName() == "") && part.isMimeType("text/plain")) {
        //         message = new MessageBean(messages[i].getMessageNumber(), messages[i].getSubject(), messages[i].getFrom()[0].toString(), null, f.format(messages[i].getSentDate()), (String) part.getContent(), false, null);
        } else if (part.getFileName() != null || part.getFileName() != "") {
        if ((part.getDisposition() != null) && Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()))
        {
        attachments.add(saveFile(MimeUtility.decodeText(part.getFileName()), part.getInputStream()));
        }
        }
        // if (message != null) message.setAttachments(attachments);
        //  message.setAttachments(attachments);
        }
        if (message != null) message.setAttachments(attachments);
        listMessages.add(message);
        }
        }
//        m = listMessages.size();
        //       n = attachments.size();

        return listMessages;
        }


public String saveFile(String filename, InputStream input) throws FileNotFoundException, IOException {
        String path = filename;
        //    File file = new File(Environment.getExternalStorageDirectory().toString(), path);
        File file = new File(this.getFilesDir(), path);
        //    for (int i = 0; file.exists(); i++) {
        //        file = new File(filename + i);
        //    }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        BufferedInputStream bis = new BufferedInputStream(input);
        int aByte;
        while ((aByte = bis.read()) != -1) {
        bos.write(aByte);
        }
        bos.flush();
        bos.close();
        bis.close();
        return this.getFilesDir()+"/"+path;
        }

    public class MessageBean implements Serializable {
        private String subject;
        private String from;
        private String to;
        private String dateSent;

        private String content;
        private boolean isNew;
        private int msgId;
        private ArrayList<String> attachments;


        public MessageBean(int msgId, String subject, String from, String to, String dateSent, String content, boolean isNew, ArrayList<String> attachments) {
            this.subject = subject;
            this.from = from;
            this.to = to;
            this.dateSent = dateSent;
            this.content = content;
            this.isNew = isNew;
            this.msgId = msgId;
            this.attachments = attachments;

        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getDateSent() {
            return dateSent;
        }

        public void setDateSent(String dateSent) {
            this.dateSent = dateSent;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public boolean isNew() {
            return isNew;
        }

        public void setNew(boolean aNew) {
            isNew = aNew;
        }

        public int getMsgId() {
            return msgId;
        }

        public void setMsgId(int msgId) {
            this.msgId = msgId;
        }

        public ArrayList<String> getAttachments() {
            return attachments;
        }

        public void setAttachments(ArrayList<String> attachments) {
            this.attachments = new ArrayList<String>(attachments);
        }
    }


    @Override
    public void onDestroy() {

/*      if (wl!=null)  {
          if (wl.isHeld()) { wl.release(); wl=null;
          }
          Toast.makeText(getApplicationContext(), "WakeLock deactivated onDestroy", Toast.LENGTH_LONG).show();
          System.out.println("7777 WakeLock deactivated onDestroy");}
*/
      sendBroadcast(new Intent("YouWillNeverKillMe"));

      super.onDestroy();
    }

    protected  void warning () {
        //     AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Dialog));
        //    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this, AlertDialog.THEME_TRADITIONAL);
        //    quitDialog.setTitle(R.string.exit);
        quitDialog.setMessage("Can not connect to IMAP folder. Check your balance");

        quitDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                        //              startService(new Intent(updatedbWithSmtp.this, IMAPListener.class).putExtra("action", "startidle").putExtra("smtpToMail", smtpToMail).putExtra("smtpToPwd", smtpToPassword));
                   }

        });

        quitDialog.show();
    }
}


