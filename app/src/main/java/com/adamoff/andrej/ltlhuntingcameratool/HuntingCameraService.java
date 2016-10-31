package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.text.MessageFormat;
//import com.example.t.huntingcameratool.DBHelper;

// В этом сервисе проверяется наш ли это номер

public class HuntingCameraService extends Service {
    public HuntingCameraService() {  };
    String phone;
    SQLiteDatabase db;
    String nam, type;
    int g = 0, n = 0, k=0, f=0;
    Handler h;
    Runnable run1;
    MainActivity.DBHelper dbHelper;
    Thread t;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


       // int n;

        try {type = intent.getStringExtra("type");}
        catch (NullPointerException e){return 0;}

        if (type.equals("MMS") || type.equals("SMS")) {
            try {phone = intent.getStringExtra("phone");}
            catch (NullPointerException e){return 0;}
            // проверяем наш ли это номер, есть ли он в БД:
            dbHelper = new MainActivity.DBHelper(this); // так передаётся контекст из mainactivity в myservice, иначе db не открывается
            db = dbHelper.getReadableDatabase();


            String[] columns = {"name"}; //, "pphone"};
            Cursor cur = db.query("acorn", columns, "pphone=?", new String[]{phone}, null, null, null);
            if (cur.moveToFirst()) {
        //        do {

       //             String ph = cur.getString(cur.getColumnIndex("pphone"));
// заменить vtbphone на phone
         //           if (phone.equals(ph)) {
                        nam = cur.getString(cur.getColumnIndex("name"));
//                    String adr = cur.getString(cur.getColumnIndex("addr"));
                        g = 1;
        //                break;
       //             } // номер нашли, выходим
       //         }
       //         while (cur.moveToNext());
            } else {
                Cursor cur2 = db.query("sifar", columns, "pphone=?", new String[]{phone}, null, null, null);
                if (cur2.moveToFirst()) {
                      nam = cur2.getString(cur2.getColumnIndex("name"));
                      g = 1;
                } else {
                    Cursor cur3 = db.query("other", columns, "pphone=?", new String[]{phone}, null, null, null);
                    if (cur3.moveToFirst()) {
                        nam = cur3.getString(cur3.getColumnIndex("name"));
                        g = 1;
                    } cur3.close();
                }
                cur2.close();
            }
            cur.close();
            db.close();

       if (g == 1) { // т.е. сообщение с нашего номера

       if (type.equals("MMS")) {

           Log.d("tag33", "MMS coming");
           // ждём пока загрузится mms - опрашиваем mms content resolver каждые 15 c
           Toast.makeText(getApplicationContext(), "New MMS from hunting camera is coming", Toast.LENGTH_LONG).show();

           //         phonelist.add(phone); // запоминаем номер
           //            db = dbHelper.getReadableDatabase();

           n=0; // флаг новая/старая mms
           //       m=0; // число циклов

           //    handler = new Handler();

           h = new Handler();

           run1 = new Runnable() {

               String number = phone; // запоминаем свой номер для своего потока
               String  anumber = "a" + number.substring(1);

               int m=0;
               int s =0;

               @Override
               public void run() {

                   m++;

                   //         Log.d("tagnumber", "number = " + number + " " + anumber);
                   //          Log.d("tag33", "m = " + m);
                   //          Toast.makeText(getApplicationContext(), "Circle is started, m ="+m, Toast.LENGTH_LONG).show();
                   //          Log.d("tag33", "circle is started, m= " + m);
                   // проверяем mms db:
                   k = 0;
                   // ищем новые mmsid, которые отсутствуют внашей db

                   if (!db.isOpen()) db = dbHelper.getWritableDatabase();

                   String[] projection = {"_id", "date"};
                   Cursor cur2 = getContentResolver().query(Uri.parse("content://mms/"), projection, null, null, null); // читаем только один столбец projection = "_id"
                   if (cur2.moveToLast()) {
                       //         Toast.makeText(getApplicationContext(), "Here 1", Toast.LENGTH_LONG).show();
                       w:
                       do {
                           f = 0;
                           n = 0;
                           String Id = cur2.getString(cur2.getColumnIndex("_id")); //из БД получаем id одной MMS
                           Log.d("tag33", "Id1=" + Id);
                           String date = cur2.getString(cur2.getColumnIndex("date"));
                           // для этого MMS id проверяем тел.номер:
                           String selectionAdd = ("msg_id=" + Id);
                           String uriStr = MessageFormat.format("content://mms/{0}/addr", Id);
                           Uri uriAddress = Uri.parse(uriStr);
                           String col[] = {"address"};
                           Cursor curaddr = getContentResolver().query(uriAddress, col, selectionAdd, null, null);
                           if (curaddr.moveToFirst()) {
                               //                       Toast.makeText(getApplicationContext(), "Here 2", Toast.LENGTH_LONG).show();
                               do {
                                   String num = curaddr.getString(curaddr.getColumnIndex("address"));
                                   if (num != null) {    // если не ставить !=null, то может выскочить исключение
                                       if (num.equals(number)) {  // сравнение строк произв-ся через equals, не через ==
                                           //нашли нужный MMS id
                                           // смотрим, есть ли он в нашей db:
                                           //    db = dbHelper.getWritableDatabase();
                                           String[] columns2 = {"mmsid"};
                                           if (!db.isOpen()) db = dbHelper.getWritableDatabase();  // без этой проверки на второй mms выскакивает иск.что db is closed
                                           Cursor cursor = db.query(anumber, columns2, null, null, null, null, null);
                                           if (cursor.moveToFirst()) {
                                               h:
                                               do {
                                                   //            Id = cursor.getString(cursor.getColumnIndex("mmsid"));
                                                   if (Id.equals(cursor.getString(cursor.getColumnIndex("mmsid")))) {
                                                       n = 1;
                                                       //            n=0;
                                                       //                                              Toast.makeText(getApplicationContext(), "n=" + n, Toast.LENGTH_LONG).show();
                                                       break h; // старая mms
                                                   } else n = 0;
                                               } while (cursor.moveToNext());
                                               //     cursor.moveToFirst();

                                               //       db.close();
                                           }    cursor.close();
                                           //                 db.close();
                                       } else f = 1;

                                       if (n == 0 && f == 0) {  // новая mms с нашего номера, проверяем загрузился ли аттачмент
                                           //        Log.d("tag33", "MMS33 new");

                                           // проверяем не пустой ли path:
                                           Id = cur2.getString(cur2.getColumnIndex("_id")); // получаем id новой MMS
                                           //        Log.d("tag33", "Id2=" + Id);
                                           //-------------находим путь к файлу:
                                           String selectionPart = "mid=" + Id;
                                           Uri uri = Uri.parse("content://mms/part");
                                           // читаем все? столбцы для данного Id
                                           int c = 0;
                                           Cursor curdata = getContentResolver().query(uri, null, selectionPart, null, null);
                                           if (curdata.moveToFirst()) {
                                               //    curdata.moveToFirst();
                                               do {
                                                   //          Log.d("tag33", "MMS33 count = " + c);
                                                   c++;
                                                   String dat = curdata.getString(curdata.getColumnIndex("_data"));
                                                   //   dat ="1";
                                                   //--------------
                                                   //    проверяем, не пустой ли path
                                                   if (dat != null) {
                                                       // сразу пишем в поля БД камер:
                                                       //                        db = dbHelper.getWritableDatabase();
                                                       ContentValues cv = new ContentValues();
                                                       cv.put("mmsid", Id);
                                                       cv.put("time", date);
                                                       cv.put("path", dat);
                                                       // вставляем новую строку в таблицу:
                                                       long rowId = db.insert(anumber, null, cv);
                                                       //          Toast.makeText(getApplicationContext(), "n2=" + n, Toast.LENGTH_LONG).show();
                                                       //         Log.d("tag33", "mms gotten, m = " + m);
                                                       curdata.close();
                                                       curaddr.close();
                                                       h.removeMessages(0);

                                                       Intent intent2 = new Intent("android.intent.action.MAIN");
                                                       intent2.setClass(getApplicationContext(), ServiceDialog.class);  // д.б. такой контекст при передаче интента из фреда в активити
                                                       intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                       intent2.putExtra("phone", phone);
                                                       intent2.putExtra("name", nam);
                                                       intent2.putExtra("type", "MMS");
                                                       startActivity(intent2);
                                                       t.interrupt();
                                                       s=1;
                                                       //      stopSelf();
                                                       break w;
                                                   }
                                                   //          Log.d("tag33", "MMS33 empty");
                                               }
                                               while (curdata.moveToNext());

                                           }   //  else Log.d("tag33", "MMS33 curdata=0");
                                           curdata.close();                                             //
                                       }  //
                                       //       db.close();
                                   }
                               }   while (curaddr.moveToNext()) ;
                           }
                           curaddr.close();

                       } while (cur2.moveToPrevious());
                   }
                   cur2.close();
                   db.close();
                   //       }
                   //       cursor.close();

                   if (m>36)  {
                       try{      Toast.makeText(getApplicationContext(), "Can not download MMS", Toast.LENGTH_LONG).show();}
                       catch (Exception e){e.printStackTrace();}
                       h.removeCallbacksAndMessages(this);
                       h.removeMessages(0);
                       t.interrupt();
                       s=1;
                       //         Log.d ("tag33", "m>20");
                       //       stopSelf();
                   }

                   if (m<=36 && s ==0)   h.postDelayed (run1, 5000); // 36 х 5 сек = 180 сек = 3 мин макс. ждём загрузки аттачмента
               }
           };

           t = new Thread(run1){} ;
           t.setDaemon(true);
           t.start();

       } else if  (type.equals("SMS")){
           Intent intent2 = new Intent("android.intent.action.MAIN");
           intent2.setClass(this, ServiceDialog.class);
           intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           intent2.putExtra("phone", phone);
           intent2.putExtra("name", nam);
           intent2.putExtra("type", "SMS");
           startActivity(intent2);
           stopSelf();
       }
   }
            //  db.close();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
