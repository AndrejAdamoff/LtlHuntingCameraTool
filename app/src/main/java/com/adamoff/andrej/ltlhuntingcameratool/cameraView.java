package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t.ltlhuntingcameratool.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    String pphone;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera_view);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
    //    txtv20 = (TextView) findViewById(R.id.textView20);     // SMS
    //    textView19 = (TextView) findViewById(R.id.textView19); // заголовок


        orientation = this.getResources().getConfiguration().orientation;
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
    } catch (Exception e) {e.printStackTrace();}
         // ----------------------------------------------------

    // --------------------SMS------------------------------------
        db = dbHelper.getWritableDatabase();
        Cursor c =  db.query("cameras", new String[]{"sms"}, "pphone=?", new String[]{pphone}, null, null, null);
        if (c.moveToFirst()){
            do{
               sms = c.getString(c.getColumnIndex("sms"));
            } while (c.moveToNext());
        } c.close();
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
    //        setContentView(R.layout.activity_camera_view);
    //        viewPager = (ViewPager) findViewById(R.id.view_pager);
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

/*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        position =  viewPager.getCurrentItem();

        viewPager.setAdapter(null); // удаляет из памяти предыдущий адаптер, помогает избежать переполнения памяти

        updatescreen();

        // Проверяем ориентацию экрана
   /*     if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_camera_view_land);
            viewPager = (ViewPager) findViewById(R.id.view_pager);
            if (NumberOfPages == 0) {
                viewPager.setAdapter(mPagerAdapter1);
            } else viewPager.setAdapter(mPagerAdapter); viewPager.setCurrentItem(position);

        }   else
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_camera_view);
            viewPager = (ViewPager) findViewById(R.id.view_pager);
            if (NumberOfPages == 0) {
                viewPager.setAdapter(mPagerAdapter1);
            } else viewPager.setAdapter(mPagerAdapter); viewPager.setCurrentItem(position);

            //      rl = (RelativeLayout) findViewById(R.id.rl);
            textView19 = (TextView) findViewById(R.id.textView19);
            textView19.setText(table + "\n" + pphone);

            if (sms.equals("enabled")) {
            txtv20 = (TextView) findViewById(R.id.textView20);
            txtv20.setText(sms1); }
        }

    }
    */


    public void getfoto(View view) {
        Intent intent = new Intent(this,SMSsender.class);
        intent.putExtra("number",pphone);
        intent.putExtra("text","ltl60*1#aa");
        startService(intent);
     //   startService(new Intent(this, IMAPListener.class).putExtra("action", "startidle"));
    }

    public void onClickCntrl(View view) {
        Intent intent = new Intent(this, CameraControl.class);
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
                // удаляем таблицу камеры
                String SQLstatement = "DROP TABLE IF EXISTS " + aphone;
                db.execSQL(SQLstatement);
                // удаление строки из cameras:
                db.delete("cameras", "pphone=?", new String[]{String.valueOf (pphone)});

                // если это последняя камера, имеющая включённый push, то останавливаем IMAP listener
               Cursor cursor1 = db.query("cameras", new String[]{"push"}, "push=?", new String[]{"enabled"}, null, null, null);
                if (cursor1.getCount()==0) {
                    startService(new Intent(cameraView.this, IMAPListener.class).putExtra("action", "stopidle"));
                       ContentValues cv11 = new ContentValues();
                       cv11.put("push", "disabled");
                       db.update("smtp", cv11, null, null);
                }
                cursor1.close();
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

            String columns[] = {"time", "path"};
            String d = null;
            String time;
            // читаем строку таблицы с помощью объекта cursor
            Cursor cursor = db.query(aphone, columns, null, null, null, null, null);
            cursor.moveToPosition(NumberOfPages - 1 - position); // курсор может выйти за границу?
            String timeStamp = cursor.getString(cursor.getColumnIndex("time"));
            java.util.Date date = new java.util.Date(Long.valueOf(timeStamp) * 1000);
            d = cursor.getString(cursor.getColumnIndex("path"));
            cursor.close();
            db.close();
            // ------- при необходимости уменьшаем изображение для устранения нехватки памяти ------
            try {
                Drawable draw = Drawable.createFromPath(Uri.parse(d).toString());
                BitmapDrawable bm = (BitmapDrawable)draw;
                bm.getBitmap();
                float heigth = bm.getBitmap().getHeight();
                float width = bm.getBitmap().getWidth();
                float ratio = width/heigth;
                int nheigth = Math.round(640/ratio);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bm.getBitmap(), 640, nheigth, false));
            } catch (NullPointerException n) {n.printStackTrace();}



            // устанавливаем расположение textView с датой:
            //   TextView textView = new TextView(MainActivity.this);
            //   textView.setTextColor(Color.WHITE);
            //   textView.setTextSize(30);
            //   textView.setTypeface(Typeface.DEFAULT_BOLD);
            fdata = new SimpleDateFormat("HH:mm   dd-MM-yyyy").format(date);
            textView.setTextSize(18);
            textView.setText(getResources().getString(R.string.fotoreceived) + fdata); // необходимо применять такую конструкцию, т.к. R.string даёт int идентификатор

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
