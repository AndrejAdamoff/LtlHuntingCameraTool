package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.t.ltlhuntingcameratool.R;
//import com.sun.mail.imap.IdleManager;

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
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;


public class addCameraAsync extends Activity {

    String TAG = "tag123";

    String recreate, type, pphone, name, aphone, aphoneold, pphoneold, smtpToMail, smtpToPwd, smtpToPassword, smtpFromMail, action, smtp, push, mms, sms, smtprefresh, mmsrefresh;
    PendingIntent pi;
    int m;
    LinkedList<MessageBean> listMessages;
    ArrayList<String> attach;
    updateMMS updateMMS;
String To1, ToPWD1;
 //   public static int a; // флажок для нотификаций
 //   public static ArrayList<String> t; // перечень активных нотификаций
 //   public static ArrayList<ArrayList> s; //
    Map<String, Integer> hm;
    ArrayList<String> spphone = new ArrayList<String>();
    ArrayList<String> sname = new ArrayList<String>();

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); // запрещаем поворот экрана. Действует в пределах активити.

          listMessages = new LinkedList<>();
  //      hm = new HashMap<String, Integer>();

        // получаем данные из интента:



        intent = getIntent();
        type = intent.getStringExtra("type");  if (type==null) type = "acorn"; // Preference defaultValue doesn't work
        action = intent.getStringExtra("action");
        if (action.equals("add") || action.equals("edit")) {
            pphone = intent.getStringExtra("phone");
            name = intent.getStringExtra("tname");
            smtp = intent.getStringExtra("smtp");
     //       if (smtp.equals("enabled")) {
                smtpToMail = intent.getStringExtra("smtpToMail");
                smtpToPwd = intent.getStringExtra("smtpToPwd");
                smtpFromMail = intent.getStringExtra("smtpFromMail");
                push = intent.getStringExtra("push");
                smtprefresh = intent.getStringExtra("smtprefresh");
     //       }
            mms = intent.getStringExtra("mms");
            sms = intent.getStringExtra("sms");
            mmsrefresh = intent.getStringExtra("mmsrefresh");

   //         Toast.makeText(addCameraAsync.this, "smtp "+ smtp, Toast.LENGTH_LONG).show();
   //         Toast.makeText(addCameraAsync.this, "push "+ push, Toast.LENGTH_LONG).show();

            if (mms.equals("enabled")) {
            setContentView(R.layout.activity_add_camera_async_mms);} // запускается прогресс-бар mms
            else if (smtp.equals("enabled")) setContentView(R.layout.activity_add_camera_async_smtp); // запускается прогресс-бар smtp

    //        ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar34);
    //        progress.getIndeterminateDrawable().setColorFilter(0xffffffff, android.graphics.PorterDuff.Mode.MULTIPLY);

            //       Toast.makeText(addCameraAsync.this, smtpToMail+smtpToPwd+smtpFromMail, Toast.LENGTH_LONG).show();

     //       pi = intent.getParcelableExtra("pendingIntent");

     //       Toast.makeText(addCameraAsync.this, "pphone="+pphone, Toast.LENGTH_LONG).show();

            try {aphone = "a" + (pphone.substring(1));}
            catch (Exception e) {phonedialog1(); } // пустой номер
        }

  //      t=new ArrayList<String>();
  //      t.clear();
        // Toast.makeText(this, "action: "+action, Toast.LENGTH_LONG).show();
        // запускаем асинхронную задачу:
           updateMMS = new updateMMS();
           updateMMS.execute(action);

     //   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
     //   finish();
    }

   // класс асинхронной задачи:
 public class updateMMS extends AsyncTask<String, Void, Void> {

       SQLiteDatabase db;
       int g = 0; //  флажок об ошибке в тел.номере
       int NumberOfPages = 0;
       //      LinkedList<MessageBean> listMessages;
       //  public ArrayList<String> spphone, sname;
       //    public ArrayList<String> t; // перечень активных нотификаций
       List<Integer> s; // число непрочит.сообщений в каждой нотификации
       String from;

       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           // запускаем прогресс-бар?
       }

       @Override
       protected void onProgressUpdate(Void... values) {
           super.onProgressUpdate(values);
           setContentView(R.layout.activity_add_camera_async_smtp); // запускается прогресс-бар smtp
       }

       @Override
       protected Void doInBackground(String... action) {

           if (action[0].equals("edit")) {
               pphoneold = intent.getStringExtra("pphoneold");
               aphoneold = "a" + pphoneold.substring(1);

               // сначала удаляем камеру:
               //    MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(getBaseContext()); // без этого нет контекста, когда приложение закрыто
               db = MainActivity.dbHelper.getWritableDatabase();
               // удаляем таблицу камеры
               String SQLstatement = "DROP TABLE IF EXISTS " + aphoneold;
               db.execSQL(SQLstatement);

               // пытаемся удалить камеру из всех таблиц:

      //         if(type.equals("acorn")){
               // удаление строки из таблицы acorn:
               try {
                   db.delete("acorn", "pphone=?", new String[]{String.valueOf(pphoneold)});
               } catch (Exception e) {
               }
         //      ;
         //      db.close();
               //}

        //       if(type.equals("sifar")){
                   // удаление строки из таблицы sifar:
                   try {
                       db.delete("sifar", "pphone=?", new String[]{String.valueOf(pphoneold)});
                   } catch (Exception e) {
                   }
                   ;
        //           db.close();
        //       }
        //       if(type.equals("other")){
                   // удаление строки из таблицы other:
                   try {
                       db.delete("other", "pphone=?", new String[]{String.valueOf(pphoneold)});
                   } catch (Exception e) {
                   }
                   ;
                   db.close();
           //}
               // вычищаем камеру из файла нотификаций:

               HashMap<String, Boolean> notif;   // = new HashMap<String, Boolean>();
               try {
                   FileInputStream fis = openFileInput("notif.ser");
                   ObjectInputStream ois = new ObjectInputStream(fis);
                   notif = (HashMap<String, Boolean>) ois.readObject();
                   ois.close();
                   fis.close();
                   if (notif.size() != 0) notif.remove(pphoneold);

                   FileOutputStream fos = openFileOutput("notif.ser", Context.MODE_PRIVATE);
                   ObjectOutputStream oos = new ObjectOutputStream(fos);
                   oos.writeObject(notif);
                   oos.flush();
                   oos.close();
                   fos.close();
               } catch (Exception h) {
               }
           }

           if (action[0].equals("add") || action[0].equals("edit")) {

           // обновляем таблицу SMTP:
               //   MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(getBaseContext()); // без этого нет контекста, когда приложение закрыто
               db = MainActivity.dbHelper.getWritableDatabase();
               ContentValues cv1 = new ContentValues();
               cv1.put("smtpToMail", smtpToMail);
               cv1.put("smtpToPassword", smtpToPwd);
               cv1.put("push", push);
               db.update("smtp", cv1, null, null);

           // ---------- рестартуем IMAP listener, чтобы он взял новые параметры --------------------------
               startService(new Intent(addCameraAsync.this, IMAPListener.class).putExtra("action", "restart"));

               // создаём отдельную таблицу для новой камеры:
               // а name - это имя таблицы = имя камеры
               // создаём SQL statement для создания таблицы:
               String SQLStatement = "create table " + aphone + " ("
                       + "_id integer primary key autoincrement," + "camtype text," + "path text," + "name text," + "time text," + "mmsid text," + "id text" + ");";
           // создаём таблицу с именем aphone для камеры в БД:
               try {
                   db.execSQL(SQLStatement);
               } catch (Exception e) {
                   //     Toast.makeText(this, "Номер введён неправильно или уже используется", Toast.LENGTH_LONG).show();
                   updateMMS.cancel(false);
                   g = 1; //
               }
               if (isCancelled()) return null;
               if (g == 0) {
                   if (type.equals("acorn")) {
                   // прописываем тип камеры
                       ContentValues cv0 = new ContentValues();
                       cv0.put("camtype", "acorn");
                       cv0.put("name", name);
                       db.insert(aphone, null, cv0);
System.out.println("888 type insert "+aphone);
                       ContentValues cv = new ContentValues();
                       cv.put("name", name);
                       cv.put("pphone", pphone);
                   // ставим дефолтные параметры камеры, изменяемые через SMS:
                       cv.put("mode", "camera"); // camera
                       cv.put("fotosize", "5MP"); // 5MP
                       cv.put("videosize", "halfHD"); // halfHD
                       cv.put("fotonumber", "1"); // 1 foto
                       cv.put("videolength", "5"); // 5 сек
                       cv.put("delayMin", "1");
                       cv.put("delaySec", "0"); // 1 min
                       cv.put("serial", ""); // выкл
                       cv.put("sense", "Normal"); // Normal
                       cv.put("lapse", "Off"); //  выкл
                       cv.put("lapseHH", "0"); //  выкл
                       cv.put("lapseMM", "0"); //  выкл
                       cv.put("lapseSS", "0"); //  выкл
                       cv.put("sidePIR", "On"); //  вкл
                       cv.put("MMStype", "VGA"); // VGA
                       cv.put("MMSlimit", "0"); // no limit

                       cv.put("timer", "Off"); // выкл
                       cv.put("timeronHH", "0");
                       cv.put("timeronMin", "0");
                       cv.put("timeroffHH", "0");
                       cv.put("timeroffMin", "0");

                       cv.put("timer2", "Off"); // выкл
                       cv.put("timer2onHH", "0");
                       cv.put("timer2onMin", "0");
                       cv.put("timer2offHH", "0");
                       cv.put("timer2offMin", "0");

                       cv.put("SMScontrol", "0"); // каждые 10 мин
                       cv.put("phone2orEmail", "");
                       cv.put("phone3orEmail", "");
                       cv.put("Email", "");
                       cv.put("smtpToMail", smtpToMail);
                       cv.put("smtpFromMail", smtpFromMail);
                       cv.put("smtpToPassword", smtpToPwd);
                       cv.put("smtp", smtp);
                       cv.put("sms", sms);
                       cv.put("push", push);
                       cv.put("mms", mms);
                       cv.put("smtprefresh", smtprefresh);
                       cv.put("mmsrefresh", mmsrefresh);

                       // прописываем камеру в таблице "acorn":
                       db.insert("acorn", null, cv); // где rowID - это номер полученной строки
                       // заносим в таблицу инф-ю об MMS:
                       if (mms.equals("enabled")) fillDbWithMMS(pphone, name);
                       // загружаем smtp фото:
                       if (smtp.equals("enabled")) {
                           if (mms.equals("enabled"))
                               publishProgress(); // нужно поменять прогресс-бар
                           fillDbWithSMTP(smtpToMail, smtpToPwd, smtpFromMail);
                       }
                       if (db.isOpen()) db.close();
                   }

                   if (type.equals("sifar")) {
                   // прописываем тип камеры
                       ContentValues cv0 = new ContentValues();
                       cv0.put("camtype", "sifar");
                       cv0.put("name", name);
                       db.insert(aphone, null, cv0);

                       ContentValues cv = new ContentValues();
                       cv.put("name", name);
                       cv.put("pphone", pphone);
                       // ставим дефолтные параметры камеры, изменяемые через SMS:
                       cv.put("mode", "camera"); // camera
                       cv.put("fotosize", "12MP"); // 12MP
                       cv.put("videosize", "720"); // 720p
                       cv.put("fotonumber", "2"); // 2 foto
                       cv.put("videolength", "10"); // 10 сек
                       cv.put("delay","off");  //выкл
                       cv.put("delayH", "0");
                       cv.put("delayMin", "0");
                       cv.put("delaySec", "0"); // 1 min
                       cv.put("serial", ""); // выкл
                       cv.put("sense", "high"); // Высокая
                       cv.put("lapse", "off"); //  выкл
                       cv.put("lapseHH", "0"); //  выкл
                       cv.put("lapseMM", "0"); //  выкл
                       cv.put("lapseSS", "0"); //  выкл
                       cv.put("MMSlimit", "0"); // no limit
                       cv.put("timer", "off"); // выкл
                       cv.put("timeronHH", "0");
                       cv.put("timeronMin", "0");
                       cv.put("timeroffHH", "0");
                       cv.put("timeroffMin", "0");
                       cv.put("SMScontrol", "upontrigger"); //
                       cv.put("phone1", "");
                       cv.put("phone2", "");
                       cv.put("phone3", "");
                       cv.put("phone4", "");
                       cv.put("email1", "");
                       cv.put("email2", "");
                       cv.put("email3", "");
                       cv.put("email4", "");
                       cv.put("zoom", "1");
                       cv.put("log", "off");
                       cv.put("sendlog", "phones"); // send to phone

                       cv.put("smtpToMail", smtpToMail);
                       cv.put("smtpFromMail", smtpFromMail);
                       cv.put("smtpToPassword", smtpToPwd);
                       cv.put("smtp", smtp);
                       cv.put("sms", sms);
                       cv.put("push", push);
                       cv.put("mms", mms);
                       cv.put("smtprefresh", smtprefresh);
                       cv.put("mmsrefresh", mmsrefresh);

                       // прописываем камеру в таблице "sifar":
                       db.insert("sifar", null, cv); // где rowID - это номер полученной строки
                       // заносим в таблицу инф-ю об MMS:
                       if (mms.equals("enabled")) fillDbWithMMS(pphone, name);
                       // загружаем smtp фото:
                       if (smtp.equals("enabled")) {
                           if (mms.equals("enabled"))
                               publishProgress(); // нужно поменять прогресс-бар
                           fillDbWithSMTP(smtpToMail, smtpToPwd, smtpFromMail);
                       }
                       if (db.isOpen()) db.close();
                   }
                   if (type.equals("other")) {
                   // прописываем тип камеры
                       ContentValues cv0 = new ContentValues();
                       cv0.put("camtype", "other");
                       cv0.put("name", name);
                       db.insert(aphone, null, cv0);

                       ContentValues cv = new ContentValues();
                       cv.put("name", name);
                       cv.put("pphone", pphone);
// дефолтные параметры камеры
                       cv.put("getphoto", "");
                       cv.put("command1", "");
                       cv.put("command2", "");
                       cv.put("command3", "");
                       cv.put("command4", "");
                       cv.put("command5", "");
                       cv.put("command6", "");
                       cv.put("command7", "");
                       cv.put("command8", "");
                       cv.put("command9", "");
                       cv.put("command10", "");

                       cv.put("value1", "");
                       cv.put("value2", "");
                       cv.put("value3", "");
                       cv.put("value4", "");
                       cv.put("value5", "");
                       cv.put("value6", "");
                       cv.put("value7", "");
                       cv.put("value8", "");
                       cv.put("value9", "");
                       cv.put("value10", "");

                       cv.put("sms1", "");
                       cv.put("sms2", "");
                       cv.put("sms3", "");
                       cv.put("sms4", "");
                       cv.put("sms5", "");
                       cv.put("sms6", "");
                       cv.put("sms7", "");
                       cv.put("sms8", "");
                       cv.put("sms9", "");
                       cv.put("sms10", "");

                       cv.put("phone1", "");
                       cv.put("phone2", "");
                       cv.put("phone3", "");
                       cv.put("phone4", "");
                       cv.put("smsbgnphone", "");
                       cv.put("smsendphone", "");
                       cv.put("mail1", "");
                       cv.put("mail2", "");
                       cv.put("mail3", "");
                       cv.put("mail4", "");
                       cv.put("smsbgnmail", "");
                       cv.put("smsendmail", "");

                       cv.put("smtpToMail", smtpToMail);
                       cv.put("smtpFromMail", smtpFromMail);
                       cv.put("smtpToPassword", smtpToPwd);
                       cv.put("smtp", smtp);
                       cv.put("sms", sms);
                       cv.put("push", push);
                       cv.put("mms", mms);
                       cv.put("smtprefresh", smtprefresh);
                       cv.put("mmsrefresh", mmsrefresh);


                   // прописываем камеру в таблице "other":
                       db.insert("other", null, cv); // где rowID - это номер полученной строки
                       // заносим в таблицу инф-ю об MMS:
                       if (mms.equals("enabled")) fillDbWithMMS(pphone, name);
                       // загружаем smtp фото:
                       if (smtp.equals("enabled")) {
                           if (mms.equals("enabled"))
                               publishProgress(); // нужно поменять прогресс-бар
                           fillDbWithSMTP(smtpToMail, smtpToPwd, smtpFromMail);
                       }
                       if (db.isOpen()) db.close();
                   }
               }
           }

           if (action[0].equals("update")) {
               // загрузка непрочитанных сообщений, определение отправителя, определение phone и nam камеры по отправителю

               // находим в db имя и пароль входящего e-mail:
               MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(getBaseContext()); // без этого нет контекста, когда приложение закрыто
               db = dbHelper.getWritableDatabase();
               String columns[] = {"smtpToMail", "smtpToPassword"};
               // читаем строку таблицы с помощью объекта cursor
               Cursor cursor = db.query("smtp", columns, null, null, null, null, null);
               if (cursor.moveToFirst()) {
                   do {
                       smtpToMail = cursor.getString(cursor.getColumnIndex("smtpToMail"));
                       smtpToPassword = cursor.getString(cursor.getColumnIndex("smtpToPassword"));
                       //      if (smtpToMail != null & smtpToPassword != null & smtpToMail != "" & smtpToPassword != "")
                       //          break;
                   }
                   while (cursor.moveToNext());
               }

               // загружаем непрочитанные сообщения:
               Properties props = System.getProperties();
               Session session = Session.getDefaultInstance(props, null);
               Store store = null;
               try {
                   store = session.getStore("imaps");
               } catch (
                       NoSuchProviderException e
                       )

               {
                   e.printStackTrace();
               }

               if (store != null)

               {
                   try {

                       String imap = "imap." + smtpToMail.substring(1 + smtpToMail.indexOf("@"));
                       //     store.connect("imap.gmail.com", smtpToMail, smtpToPassword);
                       store.connect(imap, 993, smtpToMail, smtpToPassword);

                       Folder inbox = store.getFolder("Inbox");
                       inbox.open(Folder.READ_WRITE);
                       // ищем непрочитанные сообщения:
                       Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                       if (messages.length > 0) {
                           inbox.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
                           attach = new ArrayList<String>();

                           // заполняем массив с содержимым текстовых сообщений, сохраняем аттачменты и +:
                           //         LinkedList<MessageBean> listMessages = getPart(messages, attachments);

                           // listMessages содержит все сведения о сообщении
                           listMessages = getPart(messages, attach);
                       }   // если нет новых сообщений
                       else listMessages.clear();
                       //          inbox.setFlags(messages, new Flags(Flags.Flag.SEEN), true);
                       inbox.close(false);
                       store.close();

                   } catch (MessagingException e) {
                       e.printStackTrace();
                       updateMMS.cancel(false);
                   } catch (IOException e) {
                       e.printStackTrace();
                       updateMMS.cancel(false);
                   }
               }

               //        cancel(false);
               //         if (a==1) cancel(false);
               if (isCancelled()) return null;
// определяем от каких камер пришли сообщения:

               spphone.clear();
               sname.clear();
               //          t=new ArrayList<String>();
               //           t.clear();
               //       t.add(0,"");
               //    s=new ArrayList<String>();
               //            s = new ArrayList<>();
               //            s.clear();

               Iterator<MessageBean> it = listMessages.iterator();
               // перебираем по очереди сообщения:
               for (int i = 0; i < listMessages.size(); i++) {

                   MessageBean mes = it.next();
                   //       smtpFromMail = mes.getFrom();
                   from = mes.getFrom();
                   // выделяем адрес из скобок <>
//from.indexOf ("<");

                   //   if (smtpToMail.contains("gmail"))
                   from = from.substring(from.indexOf("<") + 1, from.indexOf(">"));


                   //        Cursor cur = db.query("cameras",new String[]{"name","pphone"}, "smtpFromMail=?",new String[]{from},null,null,null);
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
                                       long rowId = db.insert(aphone, null, cv);
                                   }
                               }
                               break;
                           }
                       }
                       while (cur.moveToNext());
                   }
                   cur.close();
               }
           }

           return null;
       }

       @Override
       protected void onPostExecute(Void aVoid) {
           super.onPostExecute(aVoid);

           //  Toast.makeText(addCameraAsync.this, from, Toast.LENGTH_LONG).show();

           // запускаем Camera View:
           if (action.equals("add") || action.equals("edit")) {

               if (smtp.equals("enabled"))
                   Toast.makeText(addCameraAsync.this, "Найдено " + m + " новых Email фото", Toast.LENGTH_LONG).show();

               //     Intent intent1 = new Intent(addCameraAsync.this, MainActivity.class);
               Intent intent1 = new Intent(addCameraAsync.this, cameraView.class);
               intent1.putExtra("phone", pphone)
                       .putExtra("tname", name);
               //     intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(intent1);
               finish();
           }

           if (action.equals("update")) {

               if (spphone.size() > 0) {

                   // заполняем массив t: сортируем принятые сообщения по телефонам:
                   // записываем первый элемент:
                   //             t.add(0, spphone.get(0));
                   // проверка, есть ли другие телефоны:

                   //              Log.d(TAG, "hm.size="+hm.size());
                   // часто выскакивает исключение: отсутствует spphone.get(0) ПРОВЕРИТЬ!
                   // при плохом соединении с Интернет не загружаются картинки?

                   // пробуем открыть файл:
                   try {
                       //   File toRead=new File("notif.ser");
                       hm = new HashMap<String, Integer>();
                       FileInputStream fis = openFileInput("notif.ser");
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
                           Toast.makeText(addCameraAsync.this, "Photo download failed", Toast.LENGTH_LONG).show();
                       }
                   }

                   Log.d(TAG, "hm.size=" + hm.size());
                   Toast.makeText(addCameraAsync.this, "hm.size=" + hm.size(), Toast.LENGTH_LONG).show();

                   //     Set<Map.Entry<String, Integer>> set = hm.entrySet();
                   // обновляем hash map:
                   Map.Entry pair;
                   Iterator iter;
                   int p = 0;
                   for (int i = 0; i < spphone.size(); i++) {
                       Log.d(TAG, "spphone= " + spphone.get(i));
                       //        Log.d(TAG, "hm.get(0)="+hm.get(0));
                       iter = hm.entrySet().iterator();
                       while (iter.hasNext()) {
                           pair = (Map.Entry) iter.next();
                           Log.d(TAG, "key=" + pair.getKey());
                           Toast.makeText(addCameraAsync.this, "getKey" + pair.getKey(), Toast.LENGTH_LONG).show();
                           if (spphone.get(i).equals(pair.getKey())) {
                               Integer l = hm.get(spphone.get(i));
                               //         Integer l = (Integer) pair.getValue();
                               int n = l.intValue(); //hm.get(spphone.get(i));
                               n++;
                               hm.put(spphone.get(i), n);
                               //     pair.setValue(n);
                               Log.d(TAG, "n=" + n);

                               Toast.makeText(addCameraAsync.this, "n=" + n, Toast.LENGTH_LONG).show();
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
                           Toast.makeText(addCameraAsync.this, "fos exception", Toast.LENGTH_LONG).show();
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
                   notification.setTicker(getString(R.string.notif_ticker))
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
                       Log.d(TAG, "j=" + j);
                       pair2 = (Map.Entry) iter2.next();

           /*         for (int i = 0; i < spphone.size(); i++) {
                        if (spphone.get(i).equals(t.get(j))) n++;
                    }
                    */
                       //            notification.setContentText(sname.get(i) + " " + spphone.get(i));
                       //       notification.setContentText(Integer.toString(s.get(j))+spphone.get(j));

                       // определяем имя камеры по телефону:
                       MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(addCameraAsync.this);
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

                       notification.setContentTitle(pair2.getValue() + " " + getString(R.string.notif_title) + " ").setContentText(table + " " + pair2.getKey());

                       //     n=0;
                       // интент для перехода в cameraView:
                       Intent intent1 = new Intent(addCameraAsync.this, cameraView.class); // Создаем экземпляр Intent
                       //         intent1.putExtra("tname", sname.get(i)).putExtra("phone", spphone.get(i)).putExtra("type", "smtp");
                       intent1.putExtra("phone", pair2.getKey())
                               .putExtra("tname", table)
                               .putExtra("type", "smtp");
                       //     Toast.makeText(addCameraAsync.this, "Notification intent "+ pair2.getKey(), Toast.LENGTH_LONG).show();
                       //    a = 1;
                       // передача стека активностей в интенте:
                       TaskStackBuilder stackBuilder = TaskStackBuilder.create(addCameraAsync.this);
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
               finish();
           }
       }

       @Override
       protected void onCancelled() {
           super.onCancelled();

           //       Toast.makeText(addCameraAsync.this, "Error connecting e-mail. Please check login", Toast.LENGTH_LONG).show();
           //       Toast.makeText(addCameraAsync.this, To1+" "+ ToPWD1, Toast.LENGTH_LONG).show();

           if (action.equals("add") || action.equals("edit")) { // отправляем интент в addCamera:
               if (g == 1) {
                   phonedialog2();
               } // при повторе номера
               else {
                   Toast.makeText(addCameraAsync.this, R.string.wronglogin, Toast.LENGTH_LONG).show();

                   Intent intent1 = new Intent(); // .putExtra("error", g);
//            try {
//                pi.send(addCameraAsync.this, 2, intent1);
//            } catch (PendingIntent.CanceledException e) {
//                Toast toast = Toast.makeText(addCameraAsync.this, "Зависли?", Toast.LENGTH_LONG);
//            }
                   Intent intent2 = new Intent(addCameraAsync.this, MainActivity.class);
                   //        try {
                   //            pi.send(addCameraAsync.this, 2, intent1);
                   //        } catch (PendingIntent.CanceledException e) {
                   //            Toast toast = Toast.makeText(addCameraAsync.this, "Зависли?", Toast.LENGTH_LONG);
                   //            intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                   intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent2);
                   finish();
               }
           }
           if (action.equals("update")) {
               Toast.makeText(addCameraAsync.this, "onCancelled " + smtpToMail + " " + smtpToPassword, Toast.LENGTH_LONG).show();
               finish();
           }
       }

       protected void fillDbWithMMS(String number, String name) { // метод первоначального заполнения БД
           // метод находит и считывает инфо по MMS для заданного номера из mmssms.db и записывает данные в нашу БД db.
           // открываем нашу БД:
           //    SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();

           // ищем MMS в БД mmssms.db
           // приложенные файлы нах-ся в директории
           // /data/data/com.android.providers.telephony/app_parts, где app_parts - имя файла, н-р, PART_1262693698357
           // БД состоит из след.таблиц:
           // таблица pdu:
           // содержит MMS title, MMS reception time, _id - уникальный идентификатор MMS, sender number и др.
           // таблица part:
           // содержит путь к приложенному файлу = _data, тип файла = CT, mid = _id  для таблицы pdu
           //
           // будем искать все MMS сообщения с определённого номера:
           // номер (address) находится здесь: content://mms/ххх/addr, где xxx - _id MMS
           //
           // поэтому нужно для каждого id проверить addr на совпадение с заданным номером.

System.out.println("8888 Number "+number);
           // получаем значения имеющихся id, для чего читаем столбец id таблицы pdu:
           String[] projection = {"_id", "date"};
           try {
               Cursor cur = getContentResolver().query(Uri.parse("content://mms/"), projection, null, null, null); // читаем только один столбец projection = "_id"
               if (cur.moveToLast()) {
                   do {
                       String Id = cur.getString(cur.getColumnIndex("_id")); //из БД получаем id одной MMS
                       String date = cur.getString(cur.getColumnIndex("date"));
                       // String Id = "159";
                       // для этого MMS id проверяем тел.номер:
                       // Cursor curaddr = getContentResolver().query(Uri.parse("content://mms/Id/addr"), null, null, null, null);
                       String selectionAdd = new String("msg_id=" + Id);
                 //      String selectionAdd = new String("_id=" + Id);
                       String uriStr = MessageFormat.format("content://mms/{0}/addr", Id);
                       Uri uriAddress = Uri.parse(uriStr);
                       String col[] = {"address"};
                       Cursor curaddr = getContentResolver().query(uriAddress, col, selectionAdd, null, null);

                       if (curaddr.moveToFirst()) {
                   int x =0;
                           do {
                               String num = curaddr.getString(curaddr.getColumnIndex("address"));
                               if (num.equals(number)) {  // сравнение строк произв-ся через equals, не через ==
                                   x++;
                                   System.out.println("777! Found "+x+" mms");


/*
  // -----------Samsung:

                                   Cursor cur3 = getContentResolver().query(Uri.parse("content://mms/part"), null, "msg_id=" + Id, null, null); // берём только для своего msg_id

                                   // Cursor cur3 = getContentResolver().query(Uri.parse("content://mms/part"), null, null, null, null); // читаем все столбцы
                                   cur3.moveToFirst();
                                   do {
                                       int f = cur3.getColumnCount();
                                       //         String [] q = cur.getColumnNames();
                                       //         System.out.println("1234: "+q);
                                       int qq = 0;
                                       do {
                                           String w = cur3.getColumnName(qq);
                                           System.out.println("# "+qq+" addCameraAsync content/mms name: " + w);
                                           System.out.println("value: " + cur3.getString(cur3.getColumnIndex(w))); // содержимое столбца

                                           //     catch (Exception e){};
                                           qq++;
                                       }
                                       while (qq < f);

                                   }  while (cur3.moveToNext());
                                   cur3.close();
//---------------------------------


*/


                                   //нашли нужный MMS id и сразу находим путь к файлу:
                         String selectionPart = "mid=" + Id;
           //            String selectionPart = "id=" + Id;
          System.out.println("Found mid: "+Id);

       //                            String selectionPart = "_id=" + Id;
                                   String[] column = {"_data", "cl", "_id"}; // , "date"};
                                   Uri uri = Uri.parse("content://mms/part");
                                   // читаем все? столбцы для данного Id
                           //        Cursor curdata = getContentResolver().query(uri, column, selectionPart, null, null);
                                   Cursor curdata = getContentResolver().query(uri, null, selectionPart, null, null);
                                   //    if (curdata.moveToFirst()) {
                                       curdata.moveToFirst();
                                       do {
                                     try   {   String dat = curdata.getString(curdata.getColumnIndex("_data"));
                                          //     String cl = curdata.getString(curdata.getColumnIndex("cl"));
                                               String _id = curdata.getString(curdata.getColumnIndex("_id"));
       //   System.out.println("Found _data: "+dat);
                                           //   String date = "0"; //curdata.getString(curdata.getColumnIndex("date"));        // получаем время приёма MMS
                                     //      if (dat != null & cl !=null) { // пропускаем пустые поля таблицы
                                               if (dat != null) {
                                  System.out.println("Found _data: " + dat);
                                               // пишем в поля БД камер:
                                               // New value for one column
                                               ContentValues cv = new ContentValues();
                                               cv.put("mmsid", Id);
                                               cv.put("id", _id);
                                         //      cv.put("id", "10");
                                               cv.put("time", date);
                                               cv.put("path", dat);
                                               // вставляем новую строку в таблицу:
                                               long rowId = db.insert(aphone, null, cv);

                                           }
                                       }  catch (Exception e) {};
                                       }
                                       while (curdata.moveToNext());
                                       //      if (curdata != null) {
                                       //     curdata.close();
                                       //      }
                             //      }
                                   curdata.close();
                               } else  System.out.println("777! Found "+x+" mms");

                           }
                           while (curaddr.moveToNext());
                           // здесь закончили перебор всех mms_id

                           //    if (curaddr != null) {
                           //       curaddr.close();
                           //   }
                       }
                       curaddr.close();
                   }
                   while (cur.moveToPrevious()); // передвигаем курсор к следующей строке

                   //     if (cur != null) {
                   //        cur.close();
                   //    }
               }

               cur.close();
               // закрываем нашу БД
               //        db.close();
           } catch (Exception e) {
               e.printStackTrace();
               // здесь можно вывести сообщение пользователю что MMS не удалось загрузить
           }
       }

       private void fillDbWithSMTP(String To, String ToPWD, String From) {
           //  To1 = To;
           //   ToPWD1 = ToPWD;

//            сделать проверку fromMail!

           Properties props = System.getProperties();
           Session session = Session.getDefaultInstance(props, null);
           Store store = null;
           try {
               store = session.getStore("imaps");

               if (store != null)

               {
                   //  store.connect("imap.gmail.com", To, ToPWD);
                   String imap = "imap." + To.substring(1 + To.indexOf("@"));
                   //     store.connect("imap.gmail.com", smtpToMail, smtpToPassword);
                   store.connect(imap, 993, To, ToPWD);
               System.out.println("9999 store connected");

                   //    Folder inbox = store.getFolder("Inbox");
                   Folder inbox = store.getFolder("Inbox");
                   inbox.open(Folder.READ_WRITE);
               System.out.println("9999 folder open");

                   // находим все сообщения
                   Message[] messages = inbox.getMessages();  //.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

                   if (messages.length > 0) {
                       String from = "";
                       //  int j = 0;
                       // в массив messages1 записываем сообщения только с заданного адреса
                       int len = 0;
                       //        from0 = messages[0].getFrom()[0].toString();
                       for (int i = 0; i < messages.length; i++) {
                           try {
                               from = messages[i].getFrom()[0].toString();

                               //        Log.d("tag", "From:"+from.substring(from.indexOf("<") + 1, from.indexOf(">")));
                               //        Log.d("tag", smtpFromMail);
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                          System.out.println("9999! message from " + from);
                           //            if (smtpToMail.contains("gmail")) {
                           String fr = "";
                           try {
                                fr = from.substring(from.indexOf("<") + 1, from.indexOf(">")); // because "from" address can doesn't contain simbols <>
                           } catch (Exception e){
                               fr = from;  // "from" address without brackets < >
                               e.printStackTrace();}
                           if (!smtpFromMail.equals(fr)) {
                               messages[i] = null; // обнуляем сообщение с чужого адреса
                               //  j++;
                           } else len++; // оставляем сообщения только со своего адреса
                       }

                       Message[] messages1 = new Message[len];
              //         Log.d("tag", "Messages1 created");
                       int k = 0;
                       for (int i = 0; i < messages.length; i++) {
                           if (messages[i] == null) {
                           } else {
                               messages1[k] = messages[i]; // rewriting to new array messages1
                               k++;
                               Log.d("tag", "Messages0 populated");
                           }
                       }
             //          System.out.println("9999 messages1 length " + messages1.length);
                       // заполняем массив с содержимым текстовых сообщений, сохраняем аттачменты и +:
                       // listMessages содержит все сведения о сообщении
                       attach = new ArrayList<String>();
                       listMessages = getPart(messages1, attach);
                       m = listMessages.size();

                       inbox.setFlags(messages1, new Flags(Flags.Flag.SEEN), true);
                       inbox.close(false);
                       store.close();

                   } // если нет новых сообщений
                   else listMessages.clear();
               }
           }    //   catch (MessagingException e) {
           catch (Exception e) {
               e.printStackTrace();
       //        System.out.println("9999 Cto-to ne to ");
               updateMMS.cancel(false);
           }

           if (isCancelled()) return;

           // ---------------- записываем данные в нашу БД------------------------
           Iterator<MessageBean> it = listMessages.iterator();
           // перебираем по очереди сообщения:
           for (int i = 0; i < listMessages.size(); i++) {
               MessageBean mes = it.next();
               // записываем путь к аттачменту:
              if (mes.getAttachments().size() > 0) {
                   for (int j = 0; j < mes.getAttachments().size(); j++) {
                       ContentValues cv = new ContentValues();
                       String date = mes.getDateSent();
                       cv.put("time", date);
                       String dat = mes.getAttachments().get(j);
                       cv.put("path", dat);
                       cv.put("id", "smtp"); // признак того, что это не ммс и нужно открывать фото не по mms id, а по path
                       // вставляем новую строку в таблицу:
                       if (!db.isOpen()) db = MainActivity.dbHelper.getWritableDatabase();
                       db.insert(aphone, null, cv);
                       db.close();
                   }
               }
           }

       }
   }
       //----------------------- вспомогательные классы и методы ------------------------------------------
       // проверка типа сообщений
       private LinkedList<MessageBean> getPart (Message[] messages, ArrayList <String> attachments) throws MessagingException , IOException {
           LinkedList<MessageBean> listMessages = new LinkedList<MessageBean>();
           SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
           //  MessageBean message = null;
           for (int i =0; i< messages.length; i++) {
     //          System.out.println("9999 here 1 ");
               attachments.clear();
     //          System.out.println("9999 here 1.5 ");
//               messages[i].isMimeType("text/plain");
     //          System.out.println("9999 here 1.6 ");
  /*             if (messages[i].isMimeType("text/plain")) {
     //              System.out.println("9999 here 2 ");
                   MessageBean message = new MessageBean(messages[i].getMessageNumber(), MimeUtility.decodeText(messages[i].getSubject()), messages[i].getFrom()[0].toString(), null, String.valueOf((messages[i].getSentDate().getTime())/1000), (String)messages[i].getContent(), false, null);
                   listMessages.add(message);
                   //        m++;
               } else
     */
               if (messages[i].isMimeType("multipart/*")) {
     //              System.out.println("9999 here 3 ");
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
        //                       System.out.println("9999 here 4 ");
                               attachments.add(saveFile(MimeUtility.decodeText(part.getFileName()), part.getInputStream()));
                           }
                       }
                       // if (message != null) message.setAttachments(attachments);
                       //  message.setAttachments(attachments);
                   }
                   if (message != null) message.setAttachments(attachments);
                   listMessages.add(message);
               }
     //          System.out.println("9999 here 5 ");
           }
           //   m = listMessages.size();
           //       n = attachments.size();

           return listMessages;
       }

       public String saveFile(String filename, InputStream input) throws FileNotFoundException, IOException {
           String path = filename;
           File file = new File(addCameraAsync.this.getFilesDir(), path);
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
           return addCameraAsync.this.getFilesDir() +"/"+path;
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


    protected void phonedialog1() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.wrongnumber);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
    }
    protected void phonedialog2() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.samenumber);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
    //    super.onBackPressed();
        quitdialog();

    }

    protected  void quitdialog () {
        //     AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Dialog));
        //    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this, AlertDialog.THEME_TRADITIONAL);
    //    quitDialog.setTitle(R.string.exit);
        quitDialog.setMessage(R.string.mmswarning);

        quitDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                startActivity(new Intent(addCameraAsync.this, MainActivity.class));

                finish();
            }
        });

        quitDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }
}



