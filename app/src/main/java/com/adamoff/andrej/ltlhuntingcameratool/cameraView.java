package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t.ltlhuntingcameratool.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;


public class cameraView extends Activity {
    public cameraView() {
    }
    HashMap<String, Integer> hm;

    ViewPager viewPager;
    MyPagerAdapter mPagerAdapter;
    MyPagerAdapter1 mPagerAdapter1; // исп-ся когда еще нет MMS (NumberOfPages = 0)

    SQLiteDatabase db;
    MainActivity.DBHelper dbHelper;

    String table;
    String aphone;
    String pphone, camtype;

    PopupWindow popUp;
    TextView txtv20, textView19;
    ScrollView scrlv;
    Boolean click;
    Drawable d;
    int g = 0;
    ArrayList SMSlist = new ArrayList();
    int orientation;

    int position; // позиция фото в ViewPager

    int NumberOfPages;
    Intent intent;
    String sms=""; // флаг-переключатель на отображение SMS
    String sms1; // текст последней SMS

    Dialog dsetbtn;
    Boolean ads;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager connectivityManager = (ConnectivityManager)cameraView.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
       if (ni!=null){
//System.out.println("9999 subtype: "+ ni.getSubtype());
//        System.out.println("9999 EDGE: "+ TelephonyManager.NETWORK_TYPE_EDGE);
//        System.out.println("9999 GPRS: "+ TelephonyManager.NETWORK_TYPE_GPRS);
        if (ni.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE || ni.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS)
        {ads = false; setContentView(R.layout.activity_camera_view_wo_ads);}
                   else {ads = true; setContentView(R.layout.activity_camera_view);}
       }
       else { ads = false; setContentView(R.layout.activity_camera_view_wo_ads);}

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        //    txtv20 = (TextView) findViewById(R.id.textView20);     // SMS
        //    textView19 = (TextView) findViewById(R.id.textView19); // заголовок

        orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT && ads) {

            // реклама
              MobileAds.initialize(getApplicationContext(), "ca-app-pub-9367463267962842/9792773013");  // application Id
         //   MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); // test

            AdView mAdView = (AdView) findViewById(R.id.adView);
            // AdRequest adRequest = new AdRequest.Builder().build();
            AdRequest request = new AdRequest.Builder()
             //       .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
             //       .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                    .build();

            //     mAdView.loadAd(adRequest);
            mAdView.loadAd(request);
        }


        ListView listView = new ListView(this); // list SMS

        // устанавливаем PopUp окно SMS. PopUp обладает свойством прокрутки, scrollView не требуется
        popUp = new PopupWindow();
        popUp.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popUp.setContentView(listView);

        click = true;

        dbHelper = new MainActivity.DBHelper(this);
        //   db = dbHelper.getWritableDatabase();
        mPagerAdapter = new MyPagerAdapter();
        mPagerAdapter1 = new MyPagerAdapter1();

        intent = getIntent();

        table = intent.getStringExtra("tname"); // получаем имя камеры
        pphone = intent.getStringExtra("phone"); // получаем телефон камеры c +
        aphone = "a" + (pphone.substring(1)); // удаляем +, ставим а
// устанавливаем подпись на кнопках управления:
        //       camtype = intent.getStringExtra("camtype"); // получаем тип камеры
        //       Button btn1 = (Button)findViewById(R.id.button4);
        //       btn1.setText("Ltl Acorn get photo");


//        Toast.makeText (this, "Camera view pphone "+pphone, Toast.LENGTH_LONG).show();

        //-------- удаление камеры из файла нотификаций hm при открытии cameraView -----------------
        try {
            if (intent.getStringExtra("type").equals("smtp")) {
                try {
                    FileInputStream fis = openFileInput("notif.ser");
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    //        HashMap<String, Integer> hm = new HashMap<String, Integer>();
                    hm = (HashMap<String, Integer>) ois.readObject();
                    ois.close();
                    fis.close();
                    //     Toast.makeText(this, "old hm size"+ hm.size(), Toast.LENGTH_LONG).show();
                    hm.remove(pphone);
                    //     Toast.makeText(this, "new hm size"+ hm.size(), Toast.LENGTH_LONG).show();
                    FileOutputStream fos = openFileOutput("notif.ser", Context.MODE_PRIVATE);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(hm);
                    oos.flush();
                    oos.close();
                    fos.close();

                    FileInputStream fis1 = openFileInput("notif.ser");
                    ObjectInputStream ois1 = new ObjectInputStream(fis1);
                    //        HashMap<String, Integer> hm = new HashMap<String, Integer>();
                    hm = (HashMap<String, Integer>) ois1.readObject();
                    ois1.close();
                    fis1.close();
                    //     Toast.makeText(this, "new new hm size"+ hm.size(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Can not open file", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ----------------------------------------------------

        // --------------------SMS------------------------------------
        db = dbHelper.getWritableDatabase();
        // сначала находим тип камеры
        Cursor cur = db.query(aphone, null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                camtype = cur.getString(cur.getColumnIndex("camtype"));
                if (camtype != null) break;
            } while (cur.moveToNext());
        }
        cur.close();

        System.out.println("888 type: " + camtype);

        Cursor c = db.query(camtype, new String[]{"sms"}, "pphone=?", new String[]{pphone}, null, null, null);
        if (c.moveToFirst()) {
            do {
                sms = c.getString(c.getColumnIndex("sms"));
                if (sms != null) break;
            } while (c.moveToNext());
        }
        c.close();
        db.close();

        if (sms.equals("enabled")) {
            //Настраиваем для нашего ListView показ массива с адаптера:
            ArrayAdapter<String> StAd = new ArrayAdapter<String>(this, R.layout.list_item, SMSlist);
            listView.setAdapter(StAd);
            //   txtv20 = (TextView) findViewById(R.id.textView20);
            // обновляем список SMS и записываем первую SMS в текст. поле.
            sms1 = updateDbwithSMS(pphone, table);
        }

        updatescreen();
      }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        table = intent.getStringExtra("tname"); // получаем имя камеры
        pphone = intent.getStringExtra("phone"); // получаем телефон камеры c +
        aphone = "a" + (pphone.substring(1)); // удаляем +, ставим а

        updatescreen();
    }

    private void updatescreen (){

        NumberOfPages = count();

//        viewPager.setAdapter(null); // удаляет из памяти предыдущий адаптер, помогает избежать переполнения памяти

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            if (camtype.equals("other")) {

                Button btngph = (Button) findViewById(R.id.button4);
                btngph.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        setbtngetphone();
                        return false;
                    }
                });
            }

       if (NumberOfPages == 0) {
                viewPager.setAdapter(mPagerAdapter1);
            } else viewPager.setAdapter(mPagerAdapter);

            //      rl = (RelativeLayout) findViewById(R.id.rl);
            textView19 = (TextView) findViewById(R.id.textView19);
            textView19.setText(table + "\n" + pphone);

            if (sms.equals("enabled")) {
                txtv20 = (TextView) findViewById(R.id.textView20);
                // обновляем список SMS и записываем первую SMS в текст. поле.
          //      sms1 = updateDbwithSMS(pphone, table);
                txtv20.setText(sms1);
            }
            //  }
        }
        else {
    //        setContentView(R.layout.activity_camera_view_land);
            viewPager = (ViewPager) findViewById(R.id.view_pager);
            if (NumberOfPages == 0) {
            viewPager.setAdapter(mPagerAdapter1);
               } else viewPager.setAdapter(mPagerAdapter);
       }
}

    protected int count () {  // вычисляем число страниц для pageView (число MMS)
        // в адаптере читаем БД:

        db = dbHelper.getWritableDatabase();
        NumberOfPages=0;
        String columns[] ={"time","path"};
        String d = null;
          // читаем строку таблицы с помощью объекта cursor
        Cursor cursor = db.query(aphone, columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                d = cursor.getString(cursor.getColumnIndex("path"));
                if (d != null){NumberOfPages++;}
            }
            while (cursor.moveToNext());
        }
       cursor.close();
       db.close();

       return NumberOfPages;
    }

    public void getfoto(View view) {
        Intent intent = new Intent(this,SMSsender.class);
        intent.putExtra("number",pphone);
        if (camtype.equals("acorn")) {intent.putExtra("text","ltl60*1#aa");Toast.makeText(getApplicationContext(), getString(R.string.sendingsms) + "ltl60*1#aa", Toast.LENGTH_SHORT).show();startService(intent);}
        if (camtype.equals("sifar")) {intent.putExtra("text","12*#");Toast.makeText(getApplicationContext(), getString(R.string.sendingsms) + "12*#", Toast.LENGTH_SHORT).show();startService(intent);}
        if (camtype.equals("other")) {
            db = dbHelper.getWritableDatabase();

            String columns[] = {"getphoto"};
            Cursor cursor = db.query("other", columns, "pphone=?", new String[]{pphone}, null, null, null);
            cursor.moveToFirst();
            String getphoto = cursor.getString(cursor.getColumnIndex("getphoto"));
            if (getphoto.equals("") || getphoto == null) Toast.makeText(this, R.string.btngetfotonotconf, Toast.LENGTH_LONG).show();
              else {intent.putExtra("text", getphoto);Toast.makeText(getApplicationContext(), getString(R.string.sendingsms) + getphoto, Toast.LENGTH_SHORT).show(); startService(intent);
            }
            cursor.close();
            db.close();
        }
     }

    public void onClickCntrl(View view) {
        Intent intent = new Intent();//, CameraControl.class);
      if (camtype.equals("acorn")) intent.setClass(this, AcornControl.class);
      if (camtype.equals("sifar")) intent.setClass(this, SifarControl.class);
      if (camtype.equals("other")) intent.setClass(this, OtherControl.class);
        intent.putExtra("phone", pphone);
        intent.putExtra("tname", table);
        startActivity(intent);
        //  onPause();
    }

   protected String updateDbwithSMS (String number, String name) {
       //        db = dbHelper.getWritableDatabase();

       String[] projection = {"_id", "address", "date", "body", "type"};
       Cursor cursms = getContentResolver().query(Uri.parse("content://sms"), projection, null, null, null);
       String body = "нет SMS";
       String cont=getString(R.string.noSMSyet);
       String cont1=getString(R.string.noSMSyet);
       String fdata = "нет времени";
       String timeStamp ="нет";
       String type =" ";

       if (cursms.moveToFirst()) {
           g = 0;
           String num;
           int f;

           do {
               //  String Id = cursms.getString(cursms.getColumnIndex("_id")); //из БД получаем id одной MMS
               //  String date = cur.getString(cur.getColumnIndex("date"));
               // для этого SMS id проверяем тел.номер:
               num = cursms.getString(cursms.getColumnIndex("address"));
               if (num!=null) { // если не ставить num!= null, то выскакивает исключение
                   if (num.equals(number)) // сравнение строк произв-ся через equals, не через ==
                   {
                       body = cursms.getString(cursms.getColumnIndex("body"));
                       if (body!=null) {
                           f=0;
                           //   SMSlist.add(body) ;
                           timeStamp = cursms.getString(cursms.getColumnIndex("date"));
                           if (timeStamp!=null) {
                               java.util.Date d = new java.util.Date(Long.valueOf(timeStamp)); // для SMS не нужно умножать на 1000!!!
                               fdata = new SimpleDateFormat("HH:mm   dd-MM-yyyy").format(d);
                               type = cursms.getString(cursms.getColumnIndex("type"));
                               if (type.equals("1")) {cont = getResources().getString(R.string.smsreceived) + fdata + "\n " + body;}
                               else if (type.equals("2")) {cont = getResources().getString(R.string.sms_sent) + fdata + "\n " + body;}
                            //   cont = getResources().getString(R.string.smsreceived) + fdata + "\n " + body;
                               if (g==0){
                                   //txtv20.setText(cont);
                                   cont1 = cont;
                                   g=1; f=1; } // g даёт записать первую SMS в текстовое поле,
                               if (f==0) SMSlist.add(cont) ;}   // f не даёт записать первую SMS в listView SMSlist
                       }
                   }
               }
           }  while (cursms.moveToNext());
       } cursms.close();

       return cont1;
   }

    public void displaySMS(View view) { //здесь запускаем PopUp окно со списком всех SMS
        // можно сделать методом PopUpWindows или как новую активность с layout-ом меньшего размера
        try { // исключение возникает когда отключен показ SMS, нет txt20.
            if (click){                           //       popUp.showAsDropDown(txtv20);/
       //     popUp.showAtLocation(txtv20,Gravity.CENTER, 0, 0);
     //       popUp.update(txtv20, 320, 240);
                popUp.setHeight(240);
            popUp.showAsDropDown(txtv20,0,0);

            click = false;}
        else  {popUp.dismiss();  click = true;} }
        catch (Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
   //     getMenuInflater().inflate(R.menu.menu_camera_view3, menu);
      //  if (camtype.equals("other")) getMenuInflater().inflate(R.menu.other_menu_camera_view, menu);
     //      else
            getMenuInflater().inflate(R.menu.menu_camera_view3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {

/*            case R.id.btngetphoto: {
                setbtngetphone();
                return true;
            }
*/
            case R.id.delcamera: {
                delDialog();
                return true;
            }

            case R.id.editcamera: {

           //     startActivity(new Intent(this,editCamera.class).putExtra("name",table).putExtra("pphone", pphone));
             //   startActivity(new Intent(this,AddEditCamera.class).putExtra("name",table).putExtra("pphone", pphone));
                 startActivity(new Intent(this, AddEditCamera.class).putExtra("who", "view").putExtra("name",table).putExtra("pphone", pphone));
         //       startActivity(new Intent(this, PrefActivityTest.class).putExtra("who", "view").putExtra("name",table).putExtra("pphone", pphone));

        //     finish();
                return true;
            }
        }
            return super.onOptionsItemSelected(item);
    }
    private void delDialog() {
        AlertDialog.Builder delDialog = new AlertDialog.Builder(this);
        delDialog.setTitle(R.string.delcamconf);

        delDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                // удаление камеры
                SQLiteDatabase db = dbHelper.getWritableDatabase();
            // сначала находим тип камеры
                Cursor cur1 =  db.query(aphone, null, null, null, null, null, null);
                if (cur1.moveToFirst()){
                    do{
                        camtype = cur1.getString(cur1.getColumnIndex("camtype"));
                        if (camtype !=null) break;
                    } while (cur1.moveToNext());
                } cur1.close();
    System.out.println("1234 del camtype: "+camtype);
            // удаляем таблицу камеры
                String SQLstatement = "DROP TABLE IF EXISTS " + aphone;
                db.execSQL(SQLstatement);
            // удаление строки из таблицы:
    System.out.println("1234 del camtype: "+pphone);
                db.delete(camtype, "pphone=?", new String[]{String.valueOf (pphone)});

             // если это последняя камера, имеющая включённый push, то останавливаем IMAP listener
                Cursor cursor1 = db.query("acorn", new String[]{"push"}, "push=?", new String[]{"enabled"}, null, null, null);
                Cursor cursor2 = db.query("sifar", new String[]{"push"}, "push=?", new String[]{"enabled"}, null, null, null);
                Cursor cursor3 = db.query("other", new String[]{"push"}, "push=?", new String[]{"enabled"}, null, null, null);
                if ((cursor1.getCount()+cursor2.getCount()+cursor3.getCount())==0) {
                    startService(new Intent(cameraView.this, IMAPListener.class).putExtra("action", "stopidle"));
                       ContentValues cv11 = new ContentValues();
                       cv11.put("push", "disabled");
                       db.update("smtp", cv11, null, null);
                }
                cursor1.close();
                cursor2.close();
                cursor3.close();
                db.close();  // dbHelper.close();

                startActivity(new Intent(cameraView.this, MainActivity.class));

             // вычищаем камеру из hash-файла:
                HashMap<String, Boolean> obs;   // = new HashMap<String, Boolean>();
                try{
                    FileInputStream fis=openFileInput("observer");
                    ObjectInputStream ois=new ObjectInputStream(fis);
                    obs =(HashMap<String, Boolean>)ois.readObject();
                    ois.close();
                    fis.close();
                    if (obs.size()!=0){
                        obs.remove(pphone);
                    }
                    if (obs.size()==0){
                        // останавливаем сервис и обзервер:
                    //    startService(new Intent(cameraView.this, StartContentObserverService.class).putExtra("action", "stop"));
                    }
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
                }
                catch (Exception f){ // файла нет
                    // обзервер не запущен
                }

                finish();
            }
        });

        delDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        delDialog.show();
    }
   public void  setbtngetphone(){

     //   dsetbtn = new Dialog (this, R.style.Theme_AppCompat_Light_DarkActionBar);
       dsetbtn = new Dialog (this, R.style.MyStyle);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        dsetbtn.setContentView(R.layout.other_setbtnphoto);
        lp.copyFrom(dsetbtn.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dsetbtn.getWindow().setAttributes(lp);
        dsetbtn.setTitle("Set Get Photo button");
    //   dsetbtn.se
       db = dbHelper.getWritableDatabase();
       Cursor c = db.query("other", new String[]{"getphoto"}, "pphone=?", new String[]{pphone}, null,null, null);
       c.moveToFirst();
       String getphoto = c.getString(c.getColumnIndex("getphoto"));
       c.close();
       db.close();
       EditText et = (EditText)dsetbtn.findViewById(R.id.editbtngetphoto);
       et.setText(getphoto);

       Button b1 =(Button)dsetbtn.findViewById(R.id.setmailbutton1);
       Button b2 =(Button)dsetbtn.findViewById(R.id.setmailbutton2);
       b1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               setbtnphoto(v);
           }
       });
       b2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               setbtnphoto(v);
           }
       });
        dsetbtn.show();
    }

    public void setbtnphoto(View v){
        if (v.getId()==R.id.setmailbutton1) {
                               EditText et = (EditText)dsetbtn.findViewById(R.id.editbtngetphoto);
                               String gph = et.getText().toString();
                               db = dbHelper.getWritableDatabase();
                               ContentValues cv = new ContentValues();
                               cv.put("getphoto", gph);
                               db.update("other", cv, "pphone=?", new String[]{pphone});
                               db.close();
                             }
        dsetbtn.dismiss();
    }

    // Создаем вложенный класс - адаптер для ViewPager:

    protected class MyPagerAdapter extends PagerAdapter {

        // Метод, исполняющий счет страниц:
        @Override
        public int getCount() {
            return NumberOfPages;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            String fdata;

            ImageView imageView = new ImageView(cameraView.this);
            TextView textView = new TextView(cameraView.this);
            textView.setBackgroundColor(0xffffffff);
            textView.setTextColor(0xff000000);

            LinearLayout layout = new LinearLayout(cameraView.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //    layoutParams.gravity = Gravity.LEFT;
            //  устанавливаем расположение изображения
            // изображение привязывается к bottom из-за чего текст внизу расположить невозможно.

            ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(imageParams);

            // при перелистывании страниц устанавливаем изображение MMS:
            //            MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(super.);
            //       SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
            db = dbHelper.getWritableDatabase();
            //    dbHelper = new MainActivity.DBHelper(this);
            //    db = dbHelper.getWritableDatabase();

            String columns[] = {"time", "path","mmsid", "id"};
            String d, mid, _id = null;
            String time;
            // читаем строку таблицы с помощью объекта cursor
            Cursor cursor = db.query(aphone, columns, null, null, null, null, null);
       //     cursor.moveToPosition(NumberOfPages - 1 - position); // курсор может выйти за границу?
            cursor.moveToPosition(NumberOfPages - position);  // Samsung
            String timeStamp = cursor.getString(cursor.getColumnIndex("time"));
    System.out.println("TimeStamp: "+timeStamp);
            java.util.Date date = new java.util.Date(Long.valueOf(timeStamp) * 1000);
            d = cursor.getString(cursor.getColumnIndex("path"));
            mid = cursor.getString(cursor.getColumnIndex("mmsid"));
            _id = cursor.getString(cursor.getColumnIndex("id"));
            cursor.close();
            db.close();
            System.out.println("path to mms d: "+d);
            System.out.println("mms id: "+mid);
            System.out.println("id: "+_id);

            // устанавливаем расположение textView с датой:
            //   TextView textView = new TextView(MainActivity.this);
            //   textView.setTextColor(Color.WHITE);111111111
            //   textView.setTextSize(30);
            //   textView.setTypeface(Typeface.DEFAULT_BOLD);
            fdata = new SimpleDateFormat("HH:mm   dd-MM-yyyy").format(date);
            textView.setTextSize(18);
            //          textView.setText(getResources().getString(R.string.fotoreceived) + fdata); // необходимо применять такую конструкцию, т.к. R.string даёт int идентификатор

            Bitmap bm = null;

            // ------- при необходимости уменьшаем изображение для устранения нехватки памяти ------
            try {
                if (_id.equals("smtp")) {
                    Drawable draw = Drawable.createFromPath(Uri.parse(d).toString());
                    BitmapDrawable bmd = (BitmapDrawable) draw;
                    bm = bmd.getBitmap();
                    textView.setText("SMTP "+getResources().getString(R.string.fotoreceived) + fdata); // необходимо применять такую конструкцию, т.к. R.string даёт int идентификатор

                } else {                   //
                    InputStream  is = getContentResolver().openInputStream(Uri.parse("content://mms/part/" + _id));
                    bm = BitmapFactory.decodeStream(is);
                    textView.setText("MMS "+getResources().getString(R.string.fotoreceived) + fdata); // необходимо применять такую конструкцию, т.к. R.string даёт int идентификатор

                }

                //          Drawable draw = Drawable.createFromPath(Uri.parse(d).toString());
    //           BitmapDrawable bm = (BitmapDrawable)draw;

   //             Bitmap bm = BitmapFactory.decodeFile("/data/data/com.android.providers.telephony/app_parts/PART_1474456746392_20160811_185507.jpeg");
   //             imageView.setImageBitmap(bm);

     //          bm.getBitmap();
   //             float heigth = bm.getBitmap().getHeight();
               float heigth = bm.getHeight();
   //             float width = bm.getBitmap().getWidth();
                float width = bm.getWidth();
                float ratio = width/heigth;
                int nheigth = Math.round(640/ratio);
   //             imageView.setImageBitmap(Bitmap.createScaledBitmap(bm.getBitmap(), 640, nheigth, false));
               imageView.setImageBitmap(Bitmap.createScaledBitmap(bm, 640, nheigth, false));

            } catch (Exception n) {n.printStackTrace();}


            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            //   layoutParams.gravity = Gravity.LEFT;
            layoutParams.leftMargin = 3;
            if (orientation == Configuration.ORIENTATION_PORTRAIT){layout.addView(textView, layoutParams);}
                layout.addView(imageView);

            //    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    // Создаем вложенный класс - адаптер для ViewPager:
    // --------- вывод дефолтной картинки при отсутствии фотографий от камеры ----------------------
    protected class MyPagerAdapter1 extends PagerAdapter {

        // Метод, исполняющий счет страниц:
        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(cameraView.this);
            TextView textView = new TextView(cameraView.this);
            textView.setText(R.string.noMMSyet);
            textView.setTextSize(18);
            textView.setTextColor(0xff505050);

            imageView.setImageResource(R.drawable.field);

            //  устанавливаем расположение изображения
            ViewGroup.LayoutParams imageParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(imageParams);

            LinearLayout layout = new LinearLayout(cameraView.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //  layout.setBackgroundColor(backgroundcolor[position]);
            //   layout.setLayoutParams(layoutParams);
            //  imageView.setId(100);
            //   textView.setId(2);
            //   layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            //     layout.addView(imageView, layoutParams);
            //    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {layout.addView(textView, layoutParams);}
                layout.addView(imageView);

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent1 = new Intent(this, MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent1);
        finish();
    }
}
