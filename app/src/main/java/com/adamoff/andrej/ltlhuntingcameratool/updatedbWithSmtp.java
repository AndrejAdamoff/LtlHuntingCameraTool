package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.example.t.ltlhuntingcameratool.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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


public class updatedbWithSmtp extends Activity {

    String pphone, name, aphone;
    String smtpToMail = "", smtpToPassword = "", smtpFromMail = "", push="";
    PendingIntent pi;
    Intent intent;
    Count count;
    TextView text;
    LinkedList<Count.MessageBean> listMessages;
    ArrayList<String> attach;
    BroadcastReceiver br;

    Folder inbox;
    Store store;
    Session session;

    boolean b,p;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatesmsandsmtp); // прогресс-бар
     //   text = (TextView)findViewById(R.id.txtviewcam);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); // запрещаем поворот экрана. Действует в пределах активити.

        intent = getIntent();
        pphone = intent.getStringExtra("phone");
        name = intent.getStringExtra("tname");

        aphone = "a" + (pphone.substring(1));

  b = false;
        p=true;

        db = MainActivity.dbHelper.getWritableDatabase();
        Cursor cursor2 = db.query("smtp", new String[]{"smtpToMail", "smtpToPassword", "push"}, null, null, null, null, null);
        if (cursor2.moveToFirst()) {
            do {
                smtpToMail = cursor2.getString(cursor2.getColumnIndex("smtpToMail"));
                smtpToPassword = cursor2.getString(cursor2.getColumnIndex("smtpToPassword"));
                push = cursor2.getString(cursor2.getColumnIndex("push"));
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        db.close();

      if (push.equals("enabled")) {

       //     getApplicationContext().startService(new Intent(updatedbWithSmtp.this, IMAPListener.class).putExtra("action", "closeidle"));
      //      System.out.println("7777 close idle intent sent");

  /*          br = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    System.out.println("7777 received triggered");
                    count = new Count();
                    count.execute(aphone, pphone);
                }
            };
    try {registerReceiver(br, new IntentFilter("idle closed"));}
    catch (Exception e) {e.printStackTrace();}
*/
        }
 //     else
      {

        // запускаем асинхронную задачу:
        count = new Count();
        count.execute(aphone, pphone);
       }
    }

    public class Count extends AsyncTask<String, Integer, Void> {

        // метод находит и считывает инфо по
        // MMS для заданного номера из mmssms.db и записывает данные а нашу БД db.
        // открываем нашу БД:
        int n; // флажок, указывающий, есть ли уже mms в нашей БД

        String flag="";
        int m;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {unregisterReceiver(br);}
            catch (Exception e) {e.printStackTrace();}

      //      Toast.makeText(updatedbWithSmtp.this, "Start...", Toast.LENGTH_LONG).show();
        }

   /*     @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
          if (value[0].intValue() ==0) {text.setText("Searching new photos in email box ...");}
  //          if (value[0].intValue() ==0) {text.setText("flag= "+flag);}
            else {
                if (value[0].intValue() ==1) {text.setText("Reading data...");}
            }
        }

        */
        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);

            if (flag.equals("enabled"))
                Toast.makeText(updatedbWithSmtp.this, getString (R.string.smtp_toast1)+" " + m + " " + getString(R.string.smtp_toast2), Toast.LENGTH_LONG).show();

            // --------------- включаем IMAPListener ----------------------------------
            if (push.equals("enabled")) {
                try {unregisterReceiver(br);} catch (Exception e ) {e.printStackTrace();}
             //   startService(new Intent(updatedbWithSmtp.this, IMAPListener.class).putExtra("action", "startidle").putExtra("smtpToMail", smtpToMail).putExtra("smtpToPwd", smtpToPassword));
            }

     //       if (p) {   // можно выходить
            Intent intent1 = new Intent();
            intent1.putExtra("tname", name);
            intent1.putExtra("phone", pphone);
            //     intent1.putExtra("NumberOfPages", s.intValue());

            if (intent.getBooleanExtra("flagM", true)) {
                intent1.setClass(getApplicationContext(), updatedbWithMMS.class);
            } else {intent1.setClass(updatedbWithSmtp.this, cameraView.class);
                   intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);}

            startActivity(intent1);

            finish();
        }

        @Override
        protected void onCancelled(Void v) {
            super.onCancelled();
        Toast.makeText(updatedbWithSmtp.this, R.string.smtp_toast, Toast.LENGTH_LONG).show();
 //       Toast.makeText(updatedbWithSmtp.this, from0, Toast.LENGTH_LONG).show();
 /*           long when = System.currentTimeMillis(); // системное время
            NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext()); // Создаем экземпляр уведомления, и передаем ему наши параметры
            notification.setTicker("Can not connect to email box")
                    .setSmallIcon(R.drawable.ltl65104040)// Иконка для уведомления)
                    .setWhen(when);
            //Настраиваем звук для уведомления и его закрытие после нажатия по нему пользователем:
            notification.setDefaults(Notification.DEFAULT_SOUND);
            notification.setAutoCancel(true);
            notification.setContentTitle("Unable download photos from email");
            notification.setContentText("Check email login or whether data connection enabled or your balance");
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); // Создаем экземпляр менеджера уведомлений
            mNotificationManager.notify(999,notification.build()); // запуск уведомления
*/
            if(b) Toast.makeText(updatedbWithSmtp.this, "Can not close folder", Toast.LENGTH_LONG).show();

            // --------------- включаем IMAPListener ----------------------------------
    //     if (push.equals("enabled")) {
     //       try {unregisterReceiver(br);} catch (Exception e ) {e.printStackTrace();}
         //   startService(new Intent(updatedbWithSmtp.this, IMAPListener.class).putExtra("action", "startidle").putExtra("smtpToMail", smtpToMail).putExtra("smtpToPwd", smtpToPassword));
      //    }

            Intent intent1 = new Intent();
            intent1.putExtra("tname", name);
            intent1.putExtra("phone", pphone);
        //    intent1.putExtra("NumberOfPages", s.intValue());

        if (intent.getBooleanExtra("flagM", true)) {intent1.setClass(getApplicationContext(), updatedbWithMMS.class);}
        else {intent1.setClass(updatedbWithSmtp.this, cameraView.class);
              intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);}

            startActivity(intent1);
            finish();
        }

        @Override
        protected Void doInBackground(String... params) {

     //       if (params[2].equals("nopush")) downloadSMTP();
     //       else {
//--------------------загружаем фото с email, если требуется----------------------------------------

                listMessages = new LinkedList<>();
            attach = new ArrayList<String>();

            db = MainActivity.dbHelper.getWritableDatabase();
            String columns[] = {"pphone", "smtpToMail", "smtpToPassword", "smtpFromMail", "smtp"};
            Cursor cursor = db.query("cameras", columns, "pphone=?", new String[]{String.valueOf(params[1])}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    flag = cursor.getString(cursor.getColumnIndex("smtp"));
                    //      smtpToMail = cursor.getString(cursor.getColumnIndex("smtpToMail"));
                    //      smtpToPassword = cursor.getString(cursor.getColumnIndex("smtpToPassword"));
                    smtpFromMail = cursor.getString(cursor.getColumnIndex("smtpFromMail"));

                } while (cursor.moveToNext());
            }
            cursor.close();
     /*       Cursor cursor2 = db.query("smtp", new String[]{"smtpToMail", "smtpToPassword", "push"}, null, null, null, null, null);
            if (cursor2.moveToFirst()) {
                do {
                    smtpToMail = cursor2.getString(cursor2.getColumnIndex("smtpToMail"));
                    smtpToPassword = cursor2.getString(cursor2.getColumnIndex("smtpToPassword"));
                    push = cursor2.getString(cursor2.getColumnIndex("push"));

                } while (cursor2.moveToNext());
            }
          cursor2.close();
     */
            db.close();  // закрываем позже

            if (!flag.equals("enabled")) {  // не нужно загружать фото, выходим

//                publishProgress (0);

            } else {// загружаем непрочитанные фото:

                publishProgress(0); // меняем текст прогресс-бара

                // ------- выключаем IMAP листенер и ждём пока idle закроется --------------------------

                System.out.println("push = " + push);

            //    if (push.equals("enabled") && p) {
/*                    if (!p) {

                    getApplicationContext().startService(new Intent(updatedbWithSmtp.this, IMAPListener.class).putExtra("action", "closeidle"));
                    System.out.println("7777 close idle intent sent");
*/
          /*          BroadcastReceiver br = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            System.out.println("7777 received triggered");
                                 Count count1 = new Count();
                                 count1.execute(aphone, pphone);
                            }
                       };
                    registerReceiver(br, new IntentFilter("idle closed"));
                    p = false; // флаг о том, что ждём бродкаст, не выходим.

        */
                //System.out.println("777 receiver registered");

            //    } else {

                Properties props = System.getProperties();
                session = Session.getDefaultInstance(props, null);
                store = null;
                try {
                    store = session.getStore("imaps");
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                    count.cancel(false);

                }


                if (store != null) {
                    try {
                        String imap = "imap."+smtpToMail.substring(1+smtpToMail.indexOf("@"));
                        //     store.connect("imap.gmail.com", smtpToMail, smtpToPassword);
                        //    store.connect(imap, 993, smtpToMail, smtpToPassword);
                        store.connect(imap, 993, smtpToMail, smtpToPassword);
      //          System.out.println("8888 connected to store");
                        inbox = store.getFolder("Inbox");
     //           System.out.println("8888 folder open");
                        inbox.open(Folder.READ_WRITE);
                        // ищем непрочитанные сообщения:
                        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
       //                 Log.d("tag", "Messages downloaded");
       //                 System.out.println("8888 Messages downloaded");
       //                 System.out.println("8888 unread messages = "+messages.length);
       //                 Log.d("tag", Integer.toString(messages.length));
                        //        Message[] messages1 = new Message[messages.length];
                        if (messages.length > 0) {
                            String from = "";
                            //  int j = 0;
                            // в массив messages1 записываем сообщения только с заданного адреса
                            int len =0;
                            //        from0 = messages[0].getFrom()[0].toString();
                            for (int i = 0; i < messages.length; i++) {
                                try {
                                    from = messages[i].getFrom()[0].toString();

                                    //        Log.d("tag", "From:"+from.substring(from.indexOf("<") + 1, from.indexOf(">")));
                                    //        Log.d("tag", smtpFromMail);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
           //         System.out.println("8888 message from"+from);
                    //            if (smtpToMail.contains("gmail")) {
                                    if (!smtpFromMail.equals(from.substring(from.indexOf("<") + 1, from.indexOf(">")))) {
                                        messages[i] = null;
                                        //  j++;
                                    } else len++; // считаем число непустых элементов
                            }

                            Message[] messages1 = new Message[len];
                            Log.d("tag", "Messages1 created");
                            int k=0;
                            for (int i=0; i<messages.length; i++) {
                                if (messages[i] == null) {} else {messages1[k] = messages[i]; k++;
              //                      Log.d("tag", "Messages0 populated");
                                }
                                }
             //               System.out.println("8888 messages1 length "+messages1.length);
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

                    }  //catch (MessagingException | IOException e) {
                    catch (Exception e) {
                        e.printStackTrace();
                        count.cancel(false);
                    }
                    if (count.isCancelled()) return null;

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
                                // вставляем новую строку в таблицу:
                                if (!db.isOpen()) db = MainActivity.dbHelper.getWritableDatabase();
                                db.insert(aphone, null, cv);
                                db.close();
                            }
                        }
                    }
                }
             }
   //     }
   //        db.close();
            return null;
        }

//--------------------------------------------------------------------------------------------------

//----------------------- вспомогательные классы и методы ------------------------------------------
        // проверка типа сообщений
    private LinkedList<MessageBean> getPart (Message[] messages, ArrayList <String> attachments) throws MessagingException , IOException {
            LinkedList<MessageBean> listMessages = new LinkedList<MessageBean>();
            SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            //  MessageBean message = null;
            for (int i =0; i< messages.length; i++) {
                Log.d("tag", "Here31");
                attachments.clear();
                if (messages[i].isMimeType("text/plain")) {
                    Log.d("tag", "Here32");
                    MessageBean message = new MessageBean(messages[i].getMessageNumber(), MimeUtility.decodeText(messages[i].getSubject()), messages[i].getFrom()[0].toString(), null, String.valueOf((messages[i].getSentDate().getTime())/1000), (String)messages[i].getContent(), false, null);
                    listMessages.add(message);
                    //        m++;
                } else if (messages[i].isMimeType("multipart/*")) {
                    Log.d("tag", "Here33");
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
                Log.d("tag", "Here34");
            }
         //   m = listMessages.size();
            //       n = attachments.size();

            return listMessages;
        }

        public String saveFile(String filename, InputStream input) throws FileNotFoundException, IOException {
            String path = filename;
            File file = new File(updatedbWithSmtp.this.getFilesDir(), path);
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
            return updatedbWithSmtp.this.getFilesDir() +"/"+path;
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

                //     count.cancel(false);
                //   return null;
                // store.isConnected()
              //  if (store.isConnected()) {

                    try {
                        count.cancel(true);
                    } catch (Exception e) {

        /*          if (push.equals("enabled")) {
                      try {
                          unregisterReceiver(br);
                      } catch (Exception ee) {
                          ee.printStackTrace();
                      }
        //              startService(new Intent(updatedbWithSmtp.this, IMAPListener.class).putExtra("action", "startidle").putExtra("smtpToMail", smtpToMail).putExtra("smtpToPwd", smtpToPassword));
                  }
                  */

                        Intent intent1 = new Intent();
                        intent1.putExtra("tname", name);
                        intent1.putExtra("phone", pphone);
                        if (intent.getBooleanExtra("flagM", true)) {
                            intent1.setClass(updatedbWithSmtp.this, updatedbWithMMS.class);
                        } else intent1.setClass(updatedbWithSmtp.this, cameraView.class);

                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        finish();
                    }
             //   } try {store.close();} catch (Exception e){e.printStackTrace();}

                     //   finish();
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

