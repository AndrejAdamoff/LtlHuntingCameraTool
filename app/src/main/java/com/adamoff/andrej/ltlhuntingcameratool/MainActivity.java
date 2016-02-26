package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.t.ltlhuntingcameratool.R;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    SQLiteDatabase db;
    String table;
    String fname;
    String fphone;
    String tname;
    String tphone;
    String aphone;
    String pphone;
    ArrayList<LL> list = new ArrayList<LL>();
    Bundle b =null;

    String downloadmode;

    ProgressBar progress;
    TextView progresstext;

 //   public static String account=null; // флажок content observer-a

    TextView dview; // нужен для привязки waitdialog
    PopupWindow popUp;
    RelativeLayout rl;
    RelativeLayout.LayoutParams RelLayoutParam;
    LL m;
    // HorizontalScrollView scroll;
    // TextView textView1;

    public MainActivity() {
    }

    // при запуске приложения проверяем пустая ли БД,
    // если да, то идём в активити main_empty
    // если нет, то выводим картинки всех имеющихся камер455555555555555555555555555555555555555555555555555555555555555555555571
    // '?4555555555555555555555555555555555555555555555555555555555555555555555277777777777777777777777777221798555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555555554'
    // это мне Серый помогал!

    // создаём специальный подкласс для открытия, создания и апдейта базы данных
    public static class DBHelper extends SQLiteOpenHelper {
        Context context;

        // создаём конструктор, т.к. в супер классе нет пустого конструктора
        public DBHelper(Context context) {
            super(context, "cam.db", null, 3);
        }
        // ???  public DBHelper = super(context, "cameras.db",null,1)};

        // добавляем 2 обязательных метода onCreate и onUpdate:
        public void onCreate(SQLiteDatabase db) {
            // в этом методе первоначально создаётся БД с таблицами (можно пустыми)
/*            {"pphone", "name","mode","fotosize","videosize","fotonumber","videolength",
                    "delayMin","delaySec","serial","sense","lapse","sidePIR","MMS","MMSlimit",
                    "timer","timer2","SMScontrol","phone2orEmail","phone3orEmail","Email"}; */

            // создаём SQL statement для создания таблицы:
            String SQLStatement = "create table cameras ("
                    + "id integer primary key autoincrement,"
                    + "pphone text,"
                    + "name text,"
                    + "mode text,"
                    + "fotosize text,"
                    + "videosize text,"
                    + "fotonumber text,"
                    + "videolength text,"
                    + "delayMin text,"
                    + "delaySec text,"
                    + "serial text,"
                    + "sense text,"
                    + "lapse text,"
                    + "lapseHH text,"
                    + "lapseMM text,"
                    + "lapseSS text,"
                    + "sidePIR text,"
                    + "MMStype text,"
                    + "MMSlimit text,"
                    + "timer text,"
                    + "timeronHH text,"
                    + "timeronMin text,"
                    + "timeroffHH text,"
                    + "timeroffMin text,"
                    + "timer2 text,"
                    + "timer2onHH text,"
                    + "timer2onMin text,"
                    + "timer2offHH text,"
                    + "timer2offMin text,"
                    + "SMScontrol text,"
                    + "phone2orEmail text,"
                    + "phone3orEmail text,"
                    + "Email text,"
                    + "smtpToMail,"
                    + "smtpFromMail,"
                    + "smtpToPassword,"
                    + "sms,"
                    + "smtp,"
                    + "push,"
                    + "mms,"
                    + "smtprefresh,"
                    + "mmsrefresh"
                    + ");";
            // создаём заглавную таблицу cameras в БД:
            db.execSQL(SQLStatement);

            // создаём таблицу smtp
            String SQLStatement2 = "create table smtp ("
                    + "_id integer primary key autoincrement," + "smtpToMail text," + "smtpToPassword text," + "push" + ");";
            String SQLStatement3 = "INSERT INTO " + "smtp" + " (" + "smtpToMail" + ", " + "smtpToPassword" + ", " + "push" + ") Values ('','','')";
            // создаём таблицу с именем smtp для всех камер:
            try {
                db.execSQL(SQLStatement2);
            } catch (SQLiteException e) {
            }
            try {
                db.execSQL(SQLStatement3);
            } catch (SQLiteException e2) {
            }
        }

        // создаём обязательный метод onUpgrade(). Пока пустой.
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    // возвращаемся к MainActivity

    static public DBHelper dbHelper; // создаём ссылки на эти объекты,
    EditText edName;   // чтобы они были видны во всех методах класса
    EditText edPhone;
    String account_new = "";
    //  EditText edaddr;

    //  Dialog waitdialog;
    PendingIntent pi;
    Intent intent;

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this, AlertDialog.THEME_TRADITIONAL);
        quitDialog.setTitle(R.string.exit);

        quitDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rl = (RelativeLayout) findViewById(R.id.mainScreen);

  //      downloadmode ="SMTPMMS";

/*        // сначала определяем uri папки Inbox ящика
        Cursor labelsCursor = getContentResolver().query(GmailContract.Labels.getLabelsUri("remoteguard2013@gmail.com"), null, null, null, null);
        labelsCursor.moveToFirst();
        String labelUri = labelsCursor.getString(labelsCursor.getColumnIndex("labelUri"));
        // затем регистрируем observer, который следит за изменением контента папки Inboх
        GmailContentObserver gco = new GmailContentObserver(new Handler());
        ContentResolver cr = getContentResolver();
        //   cr.registerContentObserver(Uri.parse("content://com.google.android.gm/remoteguard2013@gmail.com/label/652503172"),true,gco);

      cr.registerContentObserver(Uri.parse(labelUri),true,gco);
     */


/*
        ImapIdleNew obj = new ImapIdleNew();
        obj.main(new String[] {"imap.gmail.com", "remoteguard2013@gmail.com", "mhts-mhts"});
*/
        //       imap(new String[]{"imap.gmail.com", "remoteguard2013@gmail.com", "mhts-mhts"});

   /*
        TextView textView2 = (TextView) findViewById(R.id.textView16);
        Button btn1 = (Button) findViewById(R.id.btn1);
        scroll = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        rl = ((RelativeLayout) findViewById(R.id.mainScreen));
        RelLayoutParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelLayoutParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        //    mainRL = (RelativeLayout) findViewById(R.id.mainRL);


        dview = (TextView) findViewById(R.id.textView30);
        popUp = new PopupWindow();
        popUp.setContentView(dview); */
        //  txtView1 = (TextView) findViewById(R.id.txtView1);

//        LinearLayout layoutdialog = new LinearLayout(this);
//        waitdialog = new Dialog(MainActivity.this);
//        waitdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        waitdialog.setContentView(R.layout.dialog2);

        // прогресс-бар плохо крутится, заедает,
      //  progress = (ProgressBar) findViewById(R.id.progressBar);
      //  progress.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0xffffffff));
      //  progress.setVisibility(ProgressBar.INVISIBLE);
        //      RelativeLayout PRL = (RelativeLayout)findViewById(R.id.progressRL);
        //      PRL.setVisibility(View.INVISIBLE);

      //       progresstext = (TextView)findViewById(R.id.textView54);
        //    progresstext.setText("jhiuh");
        //   txtView1.setText("jhiuh");
     //  progresstext.setVisibility(View.INVISIBLE);

        // создаём вспомогательный объект для создания БД
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase(); // создаём db, если её ещё нет, или окрываем, если она уже есть.
db.close();



        // проверяем запущен ли сервис с обзёрвером
    //      if (!isMyServiceRunning(StartContentObserverService.class)) {// не запущен
/*        if (!isMyServiceRunning(pollGmailService.class)){// не запущен
            db = MainActivity.dbHelper.getWritableDatabase();
        Cursor cur = db.query("cameras", new String[]{"smtpToMail"}, null, null, null, null, null);

        if (cur.moveToFirst()) {
            do {
                account_new = cur.getString(cur.getColumnIndex("smtpToMail"));
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();

        if (!account_new.equals(""))
            startService((new Intent(this, pollGmailService.class).putExtra("action", "start").putExtra("account", account_new).putExtra("who", "main")));
    }
*/
        //проверяем нужно ли запускать обзервер
/*            obs = new HashMap<String, Boolean>();
            try{
                FileInputStream fis=openFileInput("observer");
                ObjectInputStream ois=new ObjectInputStream(fis);
                obs =(HashMap<String, Boolean>)ois.readObject();
                ois.close();
                fis.close();
                if (obs.size()!=0){ // нужно запускать
                    // находим email account:
                    String account_new="";
                    db = MainActivity.dbHelper.getWritableDatabase();
                    Cursor cur = db.query("cameras", new String[]{"smtpToMail"}, null, null, null, null, null);
                    if (cur.moveToFirst()) {
                        do {
                            account_new = cur.getString(cur.getColumnIndex("smtpToMail"));
                        } while (cur.moveToNext());
                    }
                    cur.close();
                    db.close();

                    startService((new Intent(this, StartContentObserverService.class).putExtra("action", "start").putExtra("account",account_new).putExtra("who", "main")));
                }
            }
            catch (Exception f){ // файла нет
              // обзервер не нужно запускать
            }
*/


//   Toast.makeText(this, account + "observer was NOT started", Toast.LENGTH_LONG).show();


            //         Toast.makeText(addCameraAsync.this, "File open", Toast.LENGTH_LONG).show();

/*
        // проверка нужно ли запускать контент-обзервер:
        //if (!isMyServiceRunning(StartContentObserverService.class)) // не запущен
        if (!obs_running) // не запущен
        {
            if (!account_new.equals("")) { // теперь нужно запустить
        Toast.makeText(this, "start observer", Toast.LENGTH_LONG).show();
                // включаем OnBoot бродкаст-ресивер:
                //      getPackageManager().setComponentEnabledSetting(new ComponentName("com.adamoff.andrej.ltlhuntingcameratool", "com.adamoff.andrej.ltlhuntingcameratool.BootReceiver"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
                // первоначальный запуск обзервера
                startService(new Intent(this, StartContentObserverService.class).putExtra("action", "start").putExtra("account",account_new).putExtra("who", "main"));

/*                if (registerobserver(account_new)){
                    //запоминаем состояние в файле:
                if (obs.size()!=0) obs.clear();
                    obs_running = true;
                obs.put(account_new, obs_running);
                //    FileOutputStream fos=null;
                try {
                    FileOutputStream fos = openFileOutput("observer", Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(obs);
                    oos.flush();
                    oos.close();
                    fos.close();
                } catch (Exception h) {
                    h.printStackTrace();
                }
            } else Toast.makeText(getApplicationContext(), "No Gmail account found", Toast.LENGTH_LONG).show();

            }     // не запущен, нет аккаунта, запускать не нужно - выходим

         } else  // уже запущен
            { if (account_new.equals("")) { // если теперь нужно остановить
           startService(new Intent(this, StartContentObserverService.class).putExtra("action", "stop"));

  /*              getContentResolver().unregisterContentObserver(gco);
                //запоминаем состояние в файле:
                obs.clear();
                try {
                    FileOutputStream fos = openFileOutput("observer", Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(obs);
                    oos.flush();
                    oos.close();
                    fos.close();
                } catch (Exception h) {
                    h.printStackTrace();
                }

            // отключаем OnBoot бродкаст-ресивер:
        //    getPackageManager().setComponentEnabledSetting(new ComponentName("com.adamoff.andrej.ltlhuntingcameratool", "com.adamoff.andrej.ltlhuntingcameratool.BootReceiver"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
        }
            else   // уже запущен, проверка нужно ли обновить account
            if (!account_new.equals(account)) {// если новый account
                //     startService(new Intent(this, StartContentObserverService.class).putExtra("action", "stop"));
             //   getContentResolver().unregisterContentObserver(gco);
                startService((new Intent(this, StartContentObserverService.class).putExtra("action", "update").putExtra("account",account_new).putExtra("who", "main")));
        /*   //     registerobserver(account_new);
                // обновляем файл:
                obs.clear();
                obs_running = true;
                obs.put(account_new, obs_running);
                //    FileOutputStream fos=null;
                try {
                    FileOutputStream fos = openFileOutput("observer", Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(obs);
                    oos.flush();
                    oos.close();
                    fos.close();
                } catch (Exception h) {
                    h.printStackTrace();
                }

            } else {Toast.makeText(this, "observer was started MAIN", Toast.LENGTH_LONG).show();}// запущен, аккаунт не менялся - ничего не делаем, выходим
      }
*/
      updateScreen();
    }

    private void updateScreen() {

        rl.removeAllViews();
        setContentView(R.layout.activity_main);

  //      progress = (ProgressBar) findViewById(R.id.progressBar);
  //      progress.setVisibility(ProgressBar.INVISIBLE);

        progresstext = (TextView)findViewById(R.id.textView54);
        //    progresstext.setText("jhiuh");
        //   txtView1.setText("jhiuh");
        progresstext.setVisibility(View.INVISIBLE);

   //     progresstext = (TextView) findViewById(R.id.textView54);
//    progresstext.setText("rrntynty");
//       progresstext.setVisibility(View.INVISIBLE);

  //      TextView textView2 = (TextView) findViewById(R.id.textView16);
 //       Button btn1 = (Button) findViewById(R.id.btn1);
        HorizontalScrollView scroll = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        //   RelativeLayout rl = ((RelativeLayout) findViewById(R.id.mainScreen));
        RelLayoutParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelLayoutParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        //    mainRL = (RelativeLayout) findViewById(R.id.mainRL);
        LinearLayout Layout0 = (LinearLayout) findViewById(R.id.layout0);
        LinearLayout.LayoutParams Layout0params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dview = (TextView) findViewById(R.id.textView30);
        popUp = new PopupWindow();
        popUp.setContentView(dview);
        //  TextView txtView1 = (TextView) findViewById(R.id.txtView1);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

  /*      String columns[] = {"pphone", "name", "mode", "fotosize", "videosize", "fotonumber", "videolength",
                "delayMin", "delaySec", "serial", "sense", "lapse", "sidePIR", "MMS", "MMSlimit",
                "timer", "timeronHH", "timeronMin", "timeroffHH", "timeroffMin",
                "timer2", "timer2onHH", "timer2onMin", "timer2offHH", "timer2offMin",
                "SMScontrol", "phone2orEmail", "phone3orEmail", "Email"};
     */
        String columns[] = {"pphone", "name"};
        //              // читаем строку таблицы с помощью объекта cursor
        //              Cursor cursor = db.query(table, columns, "_id=1", null, null, null, null);
        Cursor cursor = db.query("cameras", columns, null, null, null, null, null);

        // определяем кол-во строк (камер):
        switch (cursor.getCount()) {

            case 0:
                //   LinearLayout Layout0 = new LinearLayout(this);
                //     LinearLayout Layout0 = (LinearLayout)findViewById(R.id.layout0);
          //      LinearLayout.LayoutParams layoutPar = new LinearLayout.LayoutParams();
                TextView textView1 = new TextView(this);
                //   textView1.setBackgroundColo(r(0xCFDEDEDE);
                textView1.setText(R.string.maintxtaddcamera);
                textView1.setTextColor(0xFF333333);
         //       textView1.setPadding(0, 50, 0, 0);
                textView1.setTextSize(42);
                textView1.setGravity(Gravity.CENTER);
                Layout0.addView(textView1); //,RelLayoutParam);
                //     rl.addView(Layout0, RelLayoutParam);
                cursor.close();
                db.close();
                break;

            case 1:
                TableLayout tbl0 = new TableLayout(this);
                //    TableLayout.LayoutParams tblparam = new TableLayout.LayoutParams(100,100);
                TableRow tr10 = new TableRow(this);
                TableRow.LayoutParams tbrow0 = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); //(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // ширина-высота ViewGroup.LayoutParams.WRAP_CONTENT); // ViewGroup.LayoutParams.MATCH_PARENT);
         //       tbrow0.setMargins(0, 50, 0, 0);
              //  tbrow0.gravity = Gravity.CENTER_VERTICAL;
                //      RelativeLayout.LayoutParams RelLayoutParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout layout0;

                // далее подставляем свой текст-подпись, в зависимости от камеры:
                //     TextView text = new TextView(this);
                // работаем с курсором:
                if (cursor.moveToFirst()) { // ставим курсор на первую строку

                    // если таблица-результат не содержит строк, то moveToFirst =  false
                    // определяем номера столбцов (id) по имени поля в выборке
                    do {
                        //    cursor.moveToFirst();
                        fphone = cursor.getString(cursor.getColumnIndex("pphone"));
                        fname = cursor.getString(cursor.getColumnIndex("name"));
                        // создаём layout, в который помещаются картинка и подпись к камере
                        layout0 = new LinearLayout(this);
                        layout0.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                       llparams.leftMargin = 0;
                        llparams.gravity = Gravity.CENTER_HORIZONTAL;
                        // картинка
                        ImageView image = new ImageView(MainActivity.this);
                        image.setImageResource(R.drawable.ltl6210200);
                        image.setLayoutParams(llparams);
                        layout0.addView(image, llparams);
                        // подпись к камере
                        TextView text = new TextView(MainActivity.this);
                        text.setBackgroundColor(0xCFDEDEDE); //
                        text.setMaxLines(2);
                        text.setMaxWidth(105);
                        text.setTextColor(0xff000000);

                        if (fname.length() != 0) text.setText(fname + "\n" + fphone);
                        else fname = "Camera";
                        text.setText(fname + "\n" + fphone);
                        // text.setTextColor(0xffffffff); // белый
                        // text.setId(j);
                        layout0.addView(text, llparams);
                        //  делаем LL кликабельным:
                        layout0.isClickable();
                        layout0.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                shortClick1(v);
                            }
                        });


                        //  tr10.addView(layout0,1,tbrow0);  // ставим View в позицию 1 в строке (т.е. во вторую позицию)

                    }
                    while (cursor.moveToNext());
                    // выносим всё на экран:
                    //  tbl0.addView(tr10);
                    //  scroll.addView(layout0);
                    //                 layout0.setPadding (0,0,0,30); // поднимаем немного вверх paddingBottom
                    // LinearLayout Layout0 = (LinearLayout)findViewById(R.id.layout0);
                    Layout0params.gravity = Gravity.CENTER;
          //          Layout0params.topMargin = 10;
                    Layout0.addView(layout0, Layout0params);
                    //       rl.addView(layout0,RelLayoutParam);

                }
                cursor.close();
                db.close();
                break;


            case 2:
                int i = 0;
                int j = 0;
                // создаём разметку строки таблицы:
                //    HorizontalScrollView scroll = new HorizontalScrollView(this);

                TableLayout tbl = new TableLayout(this);
                TableLayout.LayoutParams tblparam = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
           //     tblparam.gravity = Gravity.CENTER_VERTICAL;
          //       tbl.setPadding(0,0,0,0);

                TableRow tr1 = new TableRow(this);
                TableRow.LayoutParams tbrow = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // ширина-высота ViewGroup.LayoutParams.WRAP_CONTENT); // ViewGroup.LayoutParams.MATCH_PARENT);
  //   tbrow.gravity = Gravity.LEFT;
               tbrow.setMargins(30, 0, 25, 0);

                // далее подставляем свой текст-подпись, в зависимости от камеры:
                //     TextView text = new TextView(this);
                LinearLayout layout;
                // работаем с курсором:
                if (cursor.moveToFirst()) { // ставим курсор на первую строку
                    j = 0;
                    do {
                        fphone = cursor.getString(cursor.getColumnIndex("pphone"));
                        fname = cursor.getString(cursor.getColumnIndex("name"));

                        layout = new LinearLayout(this);
                        layout.setId(j); // чтобы потом по id определить какой layout был кликнут

                        m = new LL(j, fname, fphone); // name,fphone,faddr);
                        list.add(m); // добавляем объект в перечень list

                        layout.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        //                     llparams.leftMargin = 0;
                        llparams.gravity = Gravity.CENTER_HORIZONTAL;
                        ImageView image = new ImageView(MainActivity.this);
                        image.setImageResource(R.drawable.ltl6210200);
                        image.setLayoutParams(llparams);
                        layout.addView(image, llparams);
                        TextView text = new TextView(MainActivity.this);
                        text.setBackgroundColor(0xCFDEDEDE); //
                        text.setMaxWidth(105);
                        text.setMaxLines(3);
                        text.setTextColor(0xff000000);
                        if (fname.length() != 0) text.setText(fname + "\n" + fphone);
                        else fname = "Camera";
                        text.setText(fname + "\n" + fphone);
                        //  text.setText("Имя: " + fname + "\n" + "Тел: " + fphone);
                        text.setId(j);
                        layout.addView(text, llparams);
                        //  делаем LL кликабельным:
                        layout.isClickable();
                        layout.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                shortClick2(v);
                            }
                        });

                        tr1.addView(layout, j, tbrow);

                        j++; // ставим View в позицию j в строке

                    }
                    while (cursor.moveToNext());
                    // выносим всё на экран:
                    //      TableLayout.LayoutParams tblpar = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //      tblpar.gravity = Gravity.BOTTOM;
                    tbl.addView(tr1,tblparam);

                //    TableLayout.LayoutParams tbparam = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                 //   tbparam.gravity = Gravity.CENTER_VERTICAL;
                 //   tbparam.setMargins(0,50,0,0);
                    scroll.addView(tbl);
                 //   LinearLayout.LayoutParams layout0param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                //    Layout0.addView(scroll, Layout0params);
                }
                break;

            default:
        //        i = 0;
        //        j = 0;
                // создаём разметку строки таблицы:
                //    HorizontalScrollView scroll = new HorizontalScrollView(this);

                tbl = new TableLayout(this);
                //      TableLayout.LayoutParams tblparam = new TableLayout.LayoutParams(100,100);
                tr1 = new TableRow(this);
                tbrow = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); //(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // ширина-высота ViewGroup.LayoutParams.WRAP_CONTENT); // ViewGroup.LayoutParams.MATCH_PARENT);
                tbrow.setMargins(10, 0, 15, 0);

                // далее подставляем свой текст-подпись, в зависимости от камеры:
                //     TextView text = new TextView(this);
              //  LinearLayout layout;
                // работаем с курсором:
                if (cursor.moveToFirst()) { // ставим курсор на первую строку
                    j = 0;
                    // если таблица-результат не содержит строк, то moveToFirst =  false
                    // определяем номера столбцов (id) по имени поля в выборке
                    do {
                        //    cursor.moveToFirst();
                        fphone = cursor.getString(cursor.getColumnIndex("pphone"));
                        fname = cursor.getString(cursor.getColumnIndex("name"));
                        //   faddr = cursor.getString(cursor.getColumnIndex("addr"));
                        // создаём объект-LinearLayout:
                        //         layout = new LL(this, name, fphone,faddr);
                        //         layout.setLL(layout);
                        layout = new LinearLayout(this);
                        layout.setId(j); // чтобы потом по id определить какой layout был кликнут

                        m = new LL(j, fname, fphone); // name,fphone,faddr);
                        list.add(m); // добавляем объект в перечень list
                        //       m.setName(fname);
                        //       m.setPhone(fphone);
                        //       m.setAddr(faddr);

                        layout.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        //                     llparams.leftMargin = 0;
                        llparams.gravity = Gravity.CENTER_HORIZONTAL;
                        ImageView image = new ImageView(MainActivity.this);
                        image.setImageResource(R.drawable.ltl6210200);
                        image.setLayoutParams(llparams);
                        layout.addView(image, llparams);
                        TextView text = new TextView(MainActivity.this);
                        text.setBackgroundColor(0xCFDEDEDE); //
                        text.setMaxWidth(105);
                        text.setMaxLines(3);
                        text.setTextColor(0xff000000);
                        if (fname.length() != 0) text.setText(fname + "\n" + fphone);
                        else fname = "Camera";
                        text.setText(fname + "\n" + fphone);
                        //  text.setText("Имя: " + fname + "\n" + "Тел: " + fphone);
                        text.setId(j);
                        layout.addView(text, llparams);
                        //  делаем LL кликабельным:
                        layout.isClickable();
                        layout.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                shortClick2(v);
                            }
                        });
                        tr1.addView(layout, j, tbrow);
                        j++; // ставим View в позицию j в строке

                    }
                    while (cursor.moveToNext());
                    // выносим всё на экран:
                    //      TableLayout.LayoutParams tblpar = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    //      tblpar.gravity = Gravity.BOTTOM;
                    tbl.addView(tr1);

                    scroll.addView(tbl);
                }
                break;

        } // else n = 0;
    }

    @Override
    protected void onNewIntent(Intent intent) { // вызывается при повторном вызове активности
        super.onNewIntent(intent);

        updateScreen();
    }

    // при коротком нажатии на картинку переходим на экран работы с камерой
    public void shortClick1(View v) {


    // ------------------ читаем настройки для данной камеры: mms и/или smtp -------------------
        String smtp = "", mms = "", smtprefresh = "", mmsrefresh = "";
        String col[] = {"pphone", "name", "smtp", "mms", "smtprefresh", "mmsrefresh"};
        db = dbHelper.getWritableDatabase();
        Cursor cur = db.query("cameras", col, "pphone=?", new String[]{fphone}, null, null, null);
        if (cur.moveToFirst()) {
            do {
                smtp = cur.getString(cur.getColumnIndex("smtp"));
                mms = cur.getString(cur.getColumnIndex("mms"));
                smtprefresh = cur.getString(cur.getColumnIndex("smtprefresh"));
                mmsrefresh = cur.getString(cur.getColumnIndex("mmsrefresh"));
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
    //------------------------------------------------------------------------------------------
        Intent intent = new Intent();

        if (mms.equals("enabled") || smtp.equals("enabled")) {

            if (smtp.equals("enabled") && mms.equals("enabled")){
                if (smtprefresh.equals("enabled") && mmsrefresh.equals("enabled")) {
                    intent.setClass(MainActivity.this, updatedbWithSmtp.class);
                    intent.putExtra("flagM", true);
                }
                if (smtprefresh.equals("disabled") && mmsrefresh.equals("enabled")) {
                    intent.setClass(MainActivity.this, updatedbWithMMS.class);
                }
                if (smtprefresh.equals("disabled") && mmsrefresh.equals("disabled")) {
                    progresstext.setVisibility(View.VISIBLE);
                    intent.setClass(MainActivity.this, cameraView.class);
                }
                if (smtprefresh.equals("enabled") && mmsrefresh.equals("disabled")) {
                    intent.setClass(MainActivity.this, updatedbWithSmtp.class);
                    intent.putExtra("flagM", false);
                }

            }
            if (smtp.equals("disabled") && mmsrefresh.equals("enabled"))
                intent.setClass(MainActivity.this, updatedbWithMMS.class);
            if (smtp.equals("disabled") && mmsrefresh.equals("disabled"))
            {   progresstext.setVisibility(View.VISIBLE);
                intent.setClass(MainActivity.this, cameraView.class);} //

            if (mms.equals("disabled") && smtprefresh.equals("enabled")) {
                intent.setClass(MainActivity.this, updatedbWithSmtp.class);
                intent.putExtra("flagM", false); // флаг указывает нужно ли запускать updatedbWithMMS
            }
            if (mms.equals("disabled") && smtprefresh.equals("disabled")) {
                progresstext.setVisibility(View.VISIBLE);
                intent.setClass(MainActivity.this, cameraView.class);
            }

            if (mms.equals("disabled")) {
                if (smtprefresh.equals("enabled")) {
                    intent.setClass(MainActivity.this, updatedbWithSmtp.class);
                    intent.putExtra("flagM", false);
                } else { progresstext.setVisibility(View.VISIBLE);
                         intent.setClass(MainActivity.this, cameraView.class);}
            }
            if (smtp.equals("disabled")) {
                if (mmsrefresh.equals("enabled")) {
                    intent.setClass(MainActivity.this, updatedbWithMMS.class);
                } else
                {    progresstext.setVisibility(View.VISIBLE);
                    intent.setClass(MainActivity.this, cameraView.class);}
            }

            intent.putExtra("tname", fname);
            intent.putExtra("phone", fphone);
            startActivity(intent);

        } else {  progresstext.setVisibility(View.VISIBLE);
            intent.setClass(MainActivity.this, cameraView.class);
            intent.putExtra("tname", fname);
            intent.putExtra("phone", fphone);
            startActivity(intent);}
   }


    public void shortClick2(View v) {

        for (LL m : list) {
            if (m.getId() == (v.getId())) {
                tname = m.getName();
                tphone = m.getPhone();
            }
        }
      // --------- читаем настройки для данной камеры: mms и/или smtp -----------
        String smtp = "", mms = "", smtprefresh = "", mmsrefresh = "";
        String col[] = {"pphone", "name", "smtp", "mms", "smtprefresh", "mmsrefresh"};
        db = dbHelper.getWritableDatabase();
        Cursor cur = db.query("cameras", col, "pphone=?", new String[]{tphone}, null, null, null);
        if (cur.moveToFirst()) {
            do {
                smtp = cur.getString(cur.getColumnIndex("smtp"));
                mms = cur.getString(cur.getColumnIndex("mms"));
                smtprefresh = cur.getString(cur.getColumnIndex("smtprefresh"));
                mmsrefresh = cur.getString(cur.getColumnIndex("mmsrefresh"));
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
      // --------------------------------------------------------------------------
        Intent intent = new Intent();

        if (mms.equals("enabled") || smtp.equals("enabled")) {

            if (smtp.equals("enabled") && mms.equals("enabled")){
                if (smtprefresh.equals("enabled") && mmsrefresh.equals("enabled")) {
                    intent.setClass(MainActivity.this, updatedbWithSmtp.class);
                    intent.putExtra("flagM", true);
                }
                if (smtprefresh.equals("disabled") && mmsrefresh.equals("enabled")) {
                    intent.setClass(MainActivity.this, updatedbWithMMS.class);
                }
                if (smtprefresh.equals("disabled") && mmsrefresh.equals("disabled")) {
                    progresstext.setVisibility(View.VISIBLE);
                    intent.setClass(MainActivity.this, cameraView.class);
                }
                if (smtprefresh.equals("enabled") && mmsrefresh.equals("disabled")) {
                    intent.setClass(MainActivity.this, updatedbWithSmtp.class);
                    intent.putExtra("flagM", false);
                }

            }
            if (smtp.equals("disabled") && mmsrefresh.equals("enabled"))
                intent.setClass(MainActivity.this, updatedbWithMMS.class);
            if (smtp.equals("disabled") && mmsrefresh.equals("disabled"))
            {    progresstext.setVisibility(View.VISIBLE);
                intent.setClass(MainActivity.this, cameraView.class);} //

            if (mms.equals("disabled") && smtprefresh.equals("enabled")) {
                intent.setClass(MainActivity.this, updatedbWithSmtp.class);
                intent.putExtra("flagM", false); // флаг указывает нужно ли запускать updatedbWithMMS
            }
            if (mms.equals("disabled") && smtprefresh.equals("disabled")) {
                progresstext.setVisibility(View.VISIBLE);
                intent.setClass(MainActivity.this, cameraView.class);
            }

            if (mms.equals("disabled")) {
                if (smtprefresh.equals("enabled")) {
                    intent.setClass(MainActivity.this, updatedbWithSmtp.class);
                    intent.putExtra("flagM", false);
                } else {  progresstext.setVisibility(View.VISIBLE);
                    intent.setClass(MainActivity.this, cameraView.class);}
            }
            if (smtp.equals("disabled")) {
                if (mmsrefresh.equals("enabled")) {
                    intent.setClass(MainActivity.this, updatedbWithMMS.class);
                } else
                {    progresstext.setVisibility(View.VISIBLE);
                    intent.setClass(MainActivity.this, cameraView.class);}
            }
            intent.putExtra("tname", tname);
            intent.putExtra("phone", tphone);
            startActivity(intent);

        } else {  progresstext.setVisibility(View.VISIBLE);
            intent.setClass(MainActivity.this, cameraView.class);
            intent.putExtra("tname", tname);
            intent.putExtra("phone", tphone);
            startActivity(intent);}

    //    Toast.makeText(this, "Main mms: "+mms, Toast.LENGTH_LONG).show();
    //    Toast.makeText(this, "Main smtp: "+smtp, Toast.LENGTH_LONG).show();
    }

    // получаем интент от сервиса dbUpdateWithMMS и запускаем CameraView
/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        int result = data.getIntExtra("NumberOfPages", 0);

        //  Intent intent = new Intent(MainActivity.this, cameraView.class);
        Intent intent = new Intent(MainActivity.this, cameraView.class);
        intent.putExtra("tname", tname);
        intent.putExtra("phone", tphone);
//        intent.putExtra("NumberOfPages", result);
        startActivity(intent);

  //      progress.setVisibility(ProgressBar.INVISIBLE);
  //      progresstext.setVisibility(View.INVISIBLE);

    }
*/
    public String getPhone() {
        return fphone;
    }

    public String getName() {
        return table;
    }

 /*   public void addCamera(View view) {
        Intent intent = new Intent(MainActivity.this, addCamera.class);
        startActivity(intent);

    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Dialog));
            dialog.setTitle(R.string.about);
            dialog.setIcon(android.R.drawable.ic_dialog_info);
            dialog.setMessage(R.string.version);

            dialog.setNegativeButton(R.string.Ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {

                }
            });

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface arg0) {
                }
            });

            dialog.show();

            return true;
        }
        if (id == R.id.add){
           startActivity(new Intent(this, AddEditCamera.class).putExtra("who", "main"));
        //    startActivity(new Intent(this, PrefActivityTest.class).putExtra("pphone", "123"));
          //  finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 7:
                if (resultCode == Activity.RESULT_OK) {
                    // Has become the device administrator.

                DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                dpm.lockNow();

                } else {
                    //Canceled or failed.
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // классик объектов-LL
  class LL  {
       String name, phone;
        int ID;

       protected LL (int id){
           int ID = id;
         name =  getName();
         phone= getPhone();

       }

       protected LL(int id, String lname, String lphone) {
            ID = id;
            name = lname;
            phone = lphone;
         //   address = laddress;
                    }

       protected int getId (){
            return ID;
        };

        protected String getName (){
            return name;
        }

        protected String getPhone () {
            return phone;
        }
      //  protected String getAddress () {
      //      return address;
      //  }

    }

// метод проверки запущенных сервисов
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    protected void phonedialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.wrongnumber);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                return;
            }
        });
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        progresstext.setVisibility(View.INVISIBLE);
    }
}




