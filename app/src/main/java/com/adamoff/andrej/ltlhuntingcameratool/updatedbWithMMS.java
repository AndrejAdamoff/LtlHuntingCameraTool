package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.example.t.ltlhuntingcameratool.R;

import java.text.MessageFormat;

public class updatedbWithMMS extends Activity {

    String pphone, name, aphone;
    String smtpToMail, smtpToPassword, push;
    Intent intent;
    CountM countm;
    TextView text;
    MainActivity.DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatemms);
      //  text = (TextView)findViewById(R.id.txtviewcam);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); // запрещаем поворот экрана. Действует в пределах активити.

        dbHelper = new MainActivity.DBHelper(this); // так передаётся контекст из mainactivity в myservice, иначе db не открывается

        intent = getIntent();
        pphone = intent.getStringExtra("phone");
        name = intent.getStringExtra("tname");

        aphone = "a" + (pphone.substring(1));

        // запускаем асинхронную задачу:
        countm = new CountM();
        countm.execute(aphone, pphone);
    }

    public class CountM extends AsyncTask<String, Integer, Void> {

        // метод находит и считывает инфо по
        // MMS для заданного номера из mmssms.db и записывает данные а нашу БД db.
        // открываем нашу БД:
        int n; // флажок, указывающий, есть ли уже mms в нашей БД
        SQLiteDatabase db;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

      //      Toast.makeText(updatedbWithMMS.this, "MMS preExecute"+pphone+name, Toast.LENGTH_LONG).show();

       /*     db = MainActivity.dbHelper.getWritableDatabase();
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
            */
        //   if (push.equals("enabled"))
        //        getApplicationContext().startService(new Intent(updatedbWithMMS.this, IMAPListener.class).putExtra("action", "closeidle"));

//            Toast.makeText(updatedbWithMMS.this, "push "+push, Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onProgressUpdate(Integer... value) {
            super.onProgressUpdate(value);
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            //     if (flag.equals("enabled")) Toast.makeText(updateSMSandMMS.this, "Downloaded "+m+" new photo(s) from email box", Toast.LENGTH_LONG).show();
      //     if (push.equals("enabled"))
      //     startService(new Intent(updatedbWithMMS.this, IMAPListener.class).putExtra("action", "startidle").putExtra("smtpToMail", smtpToMail).putExtra("smtpToPwd", smtpToPassword));

            Intent intent1 = new Intent();
            intent1.putExtra("tname", name);
            intent1.putExtra("phone", pphone);
            //      intent1.putExtra("NumberOfPages", s.intValue());
            intent1.setClass(updatedbWithMMS.this, cameraView.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // передача стека активностей в интенте:
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(updatedbWithMMS.this);
            // Adds the back stack
            stackBuilder.addParentStack(cameraView.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(intent1);

            startActivity(intent1);
            finish();
        }

        @Override
        protected void onCancelled(Void s) {
            super.onCancelled();
            //      Toast.makeText(updateSMSandMMS.this, "Unable download photos from email. Check whether data connection enabled", Toast.LENGTH_LONG).show();
     //       if (push.equals("enabled"))
      //      startService(new Intent(updatedbWithMMS.this, IMAPListener.class).putExtra("action", "startidle").putExtra("smtpToMail", smtpToMail).putExtra("smtpToPwd", smtpToPassword));

            Intent intent1 = new Intent();
            intent1.putExtra("tname", name);
            intent1.putExtra("phone", pphone);
            //        intent1.putExtra("NumberOfPages", s.intValue());
            intent1.setClass(updatedbWithMMS.this, cameraView.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
            finish();
        }

        @Override
        protected Void doInBackground(String... params) {
//--------------------обновляем MMS-----------------------------------------------------------------

            db = dbHelper.getWritableDatabase();

            // получаем значения имеющихся id, для чего читаем столбец id таблицы pdu:
            String[] projection = {"_id", "date"};
            Cursor cur = getContentResolver().query(Uri.parse("content://mms/"), projection, null, null, null); // читаем только один столбец projection = "_id"

 // -----------Samsung:

   Cursor cur3 = getContentResolver().query(Uri.parse("content://mms/"), null, null, null, null); // читаем все столбцы

//  Cursor cur3 = getContentResolver().query(Uri.parse("content://mms/part"), null, null, null, null); // читаем все столбцы
                                        cur3.moveToFirst();
                                        do {
                                            int f = cur3.getColumnCount();
                                        //         String [] q = cur.getColumnNames();
                                        //         System.out.println("1234: "+q);
                                        int qq = 0;
                                        do {
                                            String w = cur3.getColumnName(qq);
                                            System.out.println("# "+qq+" updatedbWithMMS content/mms/ name: " + w);
                                            System.out.println("value: " + cur3.getString(cur3.getColumnIndex(w))); // содержимое столбца

                                            //     catch (Exception e){};
                                            qq++;
                                        }
                                        while (qq < f);

                                    }  while (cur3.moveToNext());
                                        cur3.close();
       /*
            // сразу получим курсор для нашей db:
            //         String columns[] = {"mmsid"};
            //          Cursor cursor = db.query(params[0], columns, null, null, null, null, null);
            //          if (cursor.moveToFirst()) {

            Cursor cur2 = getContentResolver().query(Uri.parse("content://mms/part"), null, null, null, null); // читаем все столбцы
            cur2.moveToFirst();
            int f = cur2.getColumnCount();
   //         String [] q = cur.getColumnNames();
   //         System.out.println("1234: "+q);
            int qq = 0;
            do {
               String w = cur2.getColumnName(qq);
                System.out.println("5678: "+w);
                System.out.println("9: "+cur2.getString(cur2.getColumnIndex(w))); // содержимое столбца

          //     catch (Exception e){};
                qq++;
            }
            while (qq<f);
            cur2.close();
*/
 // --------Samsung End ------------------

            // перебираем все mms_id
            if (cur.moveToLast()) {
                do {
                    n = 0;
                    String Id = cur.getString(cur.getColumnIndex("_id")); //из БД получаем id одной MMS
                    String date = cur.getString(cur.getColumnIndex("date"));
                    // сначала проверим, есть ли уже в нашей БД такой mms_id:
                    String columns[] = {"mmsid"};
                    // читаем строку таблицы с помощью объекта cursor
                    Cursor cursor = db.query(params[0], columns, null, null, null, null, null);
              //      Log.d("tag34", "cursor length"+cursor.getCount() );
                    if (cursor.moveToFirst()) {
                        do {
                            if (Id.equals(cursor.getString(cursor.getColumnIndex("mmsid")))) {
                                n = 1;
                                break;
                            } else n=0;
                        }
                        while (cursor.moveToNext()); // доходим до конца
                        //      cursor.moveToFirst(); // возвращаемся к началу для следующей итерации
                    } cursor.close();
           //         Log.d("tag34", "n = "+n );
                    if (n == 0) {  // т.е. нет такого mmsid в нашей БД (т.е. новая MMS)

                        // для этого MMS id проверяем тел.номер:
                        // Cursor curaddr = getContentResolver().query(Uri.parse("content://mms/Id/addr"), null, null, null, null);
                        String selectionAdd = "msg_id=" + Id;
                        String uriStr = MessageFormat.format("content://mms/{0}/addr", Id);
                        Uri uriAddress = Uri.parse(uriStr);
                        String col[] = {"address"};
                        Cursor curaddr = getContentResolver().query(uriAddress, col, selectionAdd, null, null);
                        if (curaddr.moveToFirst()) {
                    t:        do {
                                String num = curaddr.getString(curaddr.getColumnIndex("address"));
                                // если не ставить !=null, то может выскочить исключение
                                if (num != null) {
                                    if (num.equals(params[1])) {  // сравнение строк произв-ся через equals, не через ==
                        // -----------------------------
                                       System.out.println("Found mms_id= "+Id);
                                       Cursor cur2 = getContentResolver().query(Uri.parse("content://mms/part"), null, null, null, null); // читаем все столбцы
                                        cur2.moveToFirst();
                                        do {
                                            int f = cur2.getColumnCount();
                                        //         String [] q = cur.getColumnNames();
                                        //         System.out.println("1234: "+q);
                                        int qq = 0;
                                        do {
                                            String w = cur2.getColumnName(qq);
                                            System.out.println("# "+qq+" name: " + w);
                                            System.out.println("value: " + cur2.getString(cur2.getColumnIndex(w))); // содержимое столбца

                                            //     catch (Exception e){};
                                            qq++;
                                        }
                                        while (qq < f);

                                    }  while (cur2.moveToNext());
                                        cur2.close();

                              //------------------------------------

                                        //нашли нужный MMS id и сразу находим путь к файлу:
                                        String selectionPart = "mid=" + Id;
                                        //    String[] column = {"_data", "date"};
                                        Uri uri = Uri.parse("content://mms/part");
                                        // читаем все? столбцы для данного Id
                                        Cursor curdata = getContentResolver().query(uri, null, selectionPart, null, null);
                    System.out.println("888! here");
                                        curdata.moveToFirst();
                                //        do {} while (curdata.moveToNext());
                                //        if (curdata.moveToFirst()) {
                                            do {
                                                try {String dat = curdata.getString(curdata.getColumnIndex("_data"));
                                                     String _id = curdata.getString(curdata.getColumnIndex("_id"));

                    System.out.println("888!: "+ dat);
                                                //    String date = "0"; //curdata.getString(curdata.getColumnIndex("date"));        // получаем время приёма MMS
                                                if (dat != null) { // пропускаем пустые поля таблицы
                                                    // пишем в поля БД камер:
                                                    // New value for one column
                                                    ContentValues cv = new ContentValues();
                                                    cv.put("mmsid", Id);
                                                    cv.put("id", _id);
                                                    cv.put("time", date);
                                                    cv.put("path", dat);
                                                    // вставляем новую строку в таблицу:
                                                    long rowId = db.insert(params[0], null, cv);
                                                    break; // если нашли путь, то сразу выходим из цикла do
                                                  }
                                                }  catch (Exception e) {};
                                            } while (curdata.moveToNext());
                                            //  curdata.close();
                                  //      }
                                        curdata.close();
                                    } break t; // номер чужой, выходим или обработали новую mms
                                }
                            }  while (curaddr.moveToNext());
                            // здесь закончили перебор всех mms_id
                        }   curaddr.close();
                    } // тут
                }
                while (cur.moveToPrevious()); // передвигаем курсор к предыдущей строке - проверяем другой mms_id

            } cur.close();

            //    } else cursor.close();

            // закрываем нашу БД
            db.close();

            return null;
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

                countm.cancel(true);

              /*  if (push.equals("enabled"))
                   startService(new Intent(updatedbWithMMS.this, IMAPListener.class).putExtra("action", "startidle").putExtra("smtpToMail", smtpToMail).putExtra("smtpToPwd", smtpToPassword));

                Intent intent1 = new Intent();
                intent1.putExtra("tname", name);
                intent1.putExtra("phone", pphone);
                intent1.setClass(updatedbWithMMS.this, cameraView.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);

                finish();
                */
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


