package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t.ltlhuntingcameratool.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class CameraControl extends ActionBarActivity {

    SQLiteDatabase db;
    ContentValues cv, cvf;
    MainActivity.DBHelper dbHelper;

    String pphone, aphone, table;
    String SMS;

    TextView txtmode, txtfotosize, txtvideosize, txtfotonumber, txtsense, txtPIR, txtMMStype;

    TextView tv;  // показывает длительность видеозаписи, S
    TextView dl;  // показывает значение задержки повторного срабатывания MM:SS
    EditText Camname; // camera serial number 4 letters/digits
    TextView tview29;
    EditText edtxt; // serial number
    TextView tv33; // показывает интервал автосъёмки timelapse
    TextView tv38; // показывает лимит mms
    TextView tv39; // таймер
    TextView tv47; // таймер2
    TextView tv49; // SMS интервал


    Dialog dMode; RadioGroup radioGroup; // установка режима работы
    Dialog dFotosize;  // установка размера фото
    Dialog dVideosize; // установка размера видео
    Dialog dfotonumber; // установка числа фотографий
    Dialog dsense; // установка чувствительности датчика
    Dialog dpir; // боковые датчики
    Dialog dmms; // тип MMS
    Dialog dlimit; NumberPicker nplimit; // mms limit

    Dialog dVL; NumberPicker npvideolength; // установка длительности видеозаписи
    Dialog dDEL; NumberPicker delnp1, delnp2;  // delay - задержка повторного срабатывания
    Dialog dserial; Switch sserial;// установка имени камеры
    Dialog dINT; NumberPicker npInt1, npInt2, npInt3; Switch sInt; // Интервал автосъёмки
    Dialog dtimer; NumberPicker npT1, npT2, npT3, npT4; Switch sTimer; // timer
    Dialog dtimer2; NumberPicker npT12, npT22, npT32, npT42; Switch sTimer2; // timer2
    Dialog dsmsint; Dialog dsmsoff; NumberPicker npsmsint; Switch ssmsint; // интервал приема SMS
    Dialog dphone2, dphone3, demail; EditText edtxtphone2,edtxtphone3,edtxtemail; TextView txtviewphone2,txtviewphone3,txtviewmail;

   // дефолтные значения для пикеров:
    int VideoLength = 10;
    int VideoLengthNew = 0;
    int delMin=1, delSec=0; // минуты и секунды задержки повт. срабатывания
  //  int timeronHH, timeronMin, timeroffHH, timeroffMin;
  //  int timer2onHH, timer2onMin, timer2offHH, timer2offMin;

    String mode,fotosize, videosize, fotonumber, videolength,
            delayMin,delaySec,serial,sense,lapse,lapseHH,lapseMM,lapseSS,sidePIR,MMStype,MMSlimit,
            timer,timeronHH, timeronMin, timeroffHH, timeroffMin,
            timer2,timer2onHH, timer2onMin, timer2offHH, timer2offMin,
            SMScontrol,phone2orEmail,phone3orEmail,Email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_control);
        Intent intent = getIntent();
        pphone = intent.getStringExtra("phone");
        aphone = "a" + (pphone.substring(1)); // удаляем +, ставим а
        table = intent.getStringExtra("name");

        cv = new ContentValues();

  //      TableLayout table = (TableLayout)findViewById(R.id.table);
 //       ScrollView scroll = (ScrollView)findViewById(R.id.scrollView2);

 //       TableRow tbrow1 = (TableRow)findViewById(R.id.tbrow1);
 //       TableRow.LayoutParams param = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    //    TextView mode = new TextView(this);
    //    mode.setText("Режим работы:");
    //    mode.setTextSize(22);
      //  mode.setBackgroundResource(R.drawable.line);// setBackground(R.layout.list_item);
      // tbrow1.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    //    param.setMargins(0, 10, 0, 10); //setPadding(0,10,0,10);
    //    param.gravity = Gravity.LEFT;
    //    tbrow1.addView(mode, 0, param);
    //    tbrow1.setBackgroundResource(R.drawable.line);

         // читаем параметры из БД:
        dbHelper = new MainActivity.DBHelper(this);
        db = dbHelper.getWritableDatabase();
        String columns[] = {"pphone", "name","mode","fotosize","videosize","fotonumber","videolength",
                "delayMin","delaySec","serial","sense","lapse","lapseHH","lapseMM","lapseSS","sidePIR","MMStype","MMSlimit",
                "timer","timeronHH", "timeronMin", "timeroffHH", "timeroffMin",
                "timer2","timer2onHH", "timer2onMin", "timer2offHH", "timer2offMin",
                "SMScontrol","phone2orEmail","phone3orEmail","Email"};

        String args [] = {pphone};
        // читаем строку таблицы с помощью объекта cursor
        Cursor cursor = db.query("cameras", columns, "pphone=?", args, null, null, null);
        cursor.moveToFirst();
        mode = cursor.getString(cursor.getColumnIndex("mode"));
    //    modepos = cursor.getColumnIndex("mode");
        fotosize = cursor.getString(cursor.getColumnIndex("fotosize"));
        videosize = cursor.getString(cursor.getColumnIndex("videosize"));
        fotonumber = cursor.getString(cursor.getColumnIndex("fotonumber"));
        videolength  = cursor.getString(cursor.getColumnIndex("videolength"));
        delayMin = cursor.getString(cursor.getColumnIndex("delayMin"));
        delaySec = cursor.getString(cursor.getColumnIndex("delaySec"));
        serial = cursor.getString(cursor.getColumnIndex("serial"));
        sense = cursor.getString(cursor.getColumnIndex("sense"));
        lapse = cursor.getString(cursor.getColumnIndex("lapse"));
        lapseHH = cursor.getString(cursor.getColumnIndex("lapseHH"));
        lapseMM = cursor.getString(cursor.getColumnIndex("lapseMM"));
        lapseSS = cursor.getString(cursor.getColumnIndex("lapseSS"));

        sidePIR = cursor.getString(cursor.getColumnIndex("sidePIR"));
        MMStype = cursor.getString(cursor.getColumnIndex("MMStype"));
        MMSlimit = cursor.getString(cursor.getColumnIndex("MMSlimit"));

        timer = cursor.getString(cursor.getColumnIndex("timer"));
        timeronHH = cursor.getString(cursor.getColumnIndex("timeronHH"));
        timeronMin = cursor.getString(cursor.getColumnIndex("timeronMin"));
        timeroffHH = cursor.getString(cursor.getColumnIndex("timeroffHH"));
        timeroffMin = cursor.getString(cursor.getColumnIndex("timeroffMin"));

        timer2 = cursor.getString(cursor.getColumnIndex("timer2"));
        timer2onHH = cursor.getString(cursor.getColumnIndex("timer2onHH"));
        timer2onMin = cursor.getString(cursor.getColumnIndex("timer2onMin"));
        timer2offHH = cursor.getString(cursor.getColumnIndex("timer2offHH"));
        timer2offMin = cursor.getString(cursor.getColumnIndex("timer2offMin"));

        SMScontrol = cursor.getString(cursor.getColumnIndex("SMScontrol"));
        phone2orEmail = cursor.getString(cursor.getColumnIndex("phone2orEmail"));
        phone3orEmail = cursor.getString(cursor.getColumnIndex("phone3orEmail"));
        Email = cursor.getString(cursor.getColumnIndex("Email"));
        db.close();

 /*       // спиннеры:
        // адаптер для заполнения спиннера1

        ArrayAdapter<String> adapter1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Smode);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner_mode = (Spinner)findViewById(R.id.spinnerMode);
        spinner_mode.setAdapter(adapter1);
        // устанавливаем дефолтное значение:
        spinner_mode.setSelection(Integer.valueOf(mode)); // camera
       // листенер выбранного значения
        spinner_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
           //      mode = parent.getItemAtPosition(pos).toString(); // новое значение mode
           //     if (Smode[modepos] == mode) modeIsNew = true;
           //     вызов отправки SMS:
           //      Dialog send = new Dialog(this);
              //  spinner_mode.setBackgroundColor(0xB4FF1D02);
             //   EditText edtxt = (EditText)findViewById(R.id.editText4);
             //   edtxt.setText(mode);
                            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
*/

        // диалог с выбором параметра и отправкой SMS

        dMode = new Dialog(CameraControl.this);
        dMode.setContentView(R.layout.setmode);
     //   dMode.getWindow().setLayout(280, 290);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dMode.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
      //  d.show();
        dMode.getWindow().setAttributes(lp);
        dMode.setTitle("Mode");
        txtmode = (TextView)findViewById(R.id.txtmode);
        txtmode.setText(mode);

        // диалог с выбором параметра и отправкой SMS

        dFotosize = new Dialog(CameraControl.this);
        dFotosize.setContentView(R.layout.setfotosize);
      //  dFotosize.getWindow().setLayout(280, 290);
        lp.copyFrom(dFotosize.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dFotosize.getWindow().setAttributes(lp);
        dFotosize.setTitle("Foto size");
        txtfotosize = (TextView)findViewById(R.id.txtfotosize);
        txtfotosize.setText(fotosize);

        // диалог с выбором параметра Videosize и отправкой SMS

        dVideosize = new Dialog(CameraControl.this);
        dVideosize.setContentView(R.layout.setvideosize);
     //   dVideosize.getWindow().setLayout(280, 290);
        lp.copyFrom(dVideosize.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dVideosize.getWindow().setAttributes(lp);
        dVideosize.setTitle("Video size");
        txtvideosize = (TextView)findViewById(R.id.txtvideosize);
        txtvideosize.setText(videosize);

        dfotonumber = new Dialog(CameraControl.this);
        dfotonumber.setContentView(R.layout.setfotonumber);
     //   dfotonumber.getWindow().setLayout(280, 288);
        lp.copyFrom(dfotonumber.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dfotonumber.getWindow().setAttributes(lp);
        dfotonumber.setTitle("Foto number");
        txtfotonumber = (TextView)findViewById(R.id.txtfotonumber);
        txtfotonumber.setText(fotonumber);

    // создаём диалог с numberpicker для установки длительности видео:
        tv = (TextView) findViewById(R.id.textView23);
        tv.setText(videolength);
        dVL = new Dialog(CameraControl.this);
        dVL.setContentView(R.layout.setvideolength);
     //   dVL.getWindow().setLayout(270, 335);
        lp.copyFrom(dVL.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dVL.getWindow().setAttributes(lp);
        dVL.setTitle("Video length, sec");
        //  d.setContentView(R.layout.dialog);
     //   Button b1VL = (Button) dVL.findViewById(R.id.button1);
     //   Button b2VL = (Button) dVL.findViewById(R.id.button2);
        npvideolength = (NumberPicker) dVL.findViewById(R.id.numberPicker2);
        npvideolength.setMaxValue(60);
        npvideolength.setMinValue(1);
        // дефолтное значение
        npvideolength.setValue(Integer.valueOf(videolength)); // 5 сек
        npvideolength.setWrapSelectorWheel(false);
        // далее см. shownp()

    // диалог с numberpicker для установки задержки повторного срабатывания MM:SS
        dl = (TextView) findViewById(R.id.textView25);
        dl.setText(delayMin+"m:"+delaySec+"s");
        dDEL = new Dialog(CameraControl.this);
        dDEL.setContentView(R.layout.setdelay);
   //     dDEL.getWindow().setLayout(280, 372);
        lp.copyFrom(dDEL.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dDEL.getWindow().setAttributes(lp);
        dDEL.setTitle("Delay");
        delnp1 = (NumberPicker) dDEL.findViewById(R.id.numberPicker1);
        delnp1.setMaxValue(59);
        delnp1.setMinValue(0);
        delnp1.setWrapSelectorWheel(false);
        // устанавливаем дефолтное значение. Заменить на значение из БД
        delnp1.setValue(Integer.valueOf(delayMin));
        delnp2 = (NumberPicker) dDEL.findViewById(R.id.numberPicker2);
       delnp2.setMaxValue(59);
       delnp2.setMinValue(0);
   /*    String[] sec = new String[60];
    //  String[]  sec = {"01","02","03"};
       for (int i=0; i<60; i++){
            if (i<10) sec[i] = "0"+Integer.toString(i);
            else sec[i] = Integer.toString(i);
        }

        delnp2.setDisplayedValues(sec); */
        delnp2.setWrapSelectorWheel(false);
        delnp2.setValue(Integer.valueOf(delaySec));
     // далее см. setdelay()

        // установка имени (serial numbers) камеры:
        dserial = new Dialog(CameraControl.this);
        dserial.setContentView(R.layout.setserial);
      //  dserial.getWindow().setLayout(280, 280);
        lp.copyFrom(dserial.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dserial.getWindow().setAttributes(lp);
        dserial.setTitle("Set camera name");
        tview29 = (TextView)findViewById(R.id.textView29);
        tview29.setText(serial);
        // далее см. setSerial2()

        dsense = new Dialog(CameraControl.this);
        dsense.setContentView(R.layout.setsense);
    //    dsense.getWindow().setLayout(280, 330);
        lp.copyFrom(dsense.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dsense.getWindow().setAttributes(lp);
        dsense.setTitle("Sensor sensitivity");
        txtsense = (TextView)findViewById(R.id.txtsense);
        txtsense.setText(sense);

  // "Интервал автосъёмки:"
        tv33 = (TextView)findViewById(R.id.textView33);
        if (lapse.equals("Off")){tv33.setText("Off");}
        else {tv33.setText(lapseHH+"h: "+lapseMM+"m: "+lapseSS+"s: ");}
        dINT = new Dialog(CameraControl.this);
        dINT.setContentView(R.layout.setinterval);
     //   dINT.getWindow().setLayout(280, 405);
        lp.copyFrom(dINT.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dINT.getWindow().setAttributes(lp);
        dINT.setTitle("Set time lapse");
        sInt  = (Switch)dINT.findViewById(R.id.switch2);
        npInt1 = (NumberPicker) dINT.findViewById(R.id.numberPicker1);
        npInt1.setMaxValue(23);
        npInt1.setMinValue(0);
        npInt1.setWrapSelectorWheel(false);
        npInt2 = (NumberPicker)dINT.findViewById(R.id.numberPicker2);
        npInt2.setMaxValue(59);
        npInt2.setMinValue(0);
        npInt2.setWrapSelectorWheel(false);
        npInt3 = (NumberPicker)dINT.findViewById(R.id.numberPicker3);
        npInt3.setMaxValue(59);
        npInt3.setMinValue(0);
        npInt3.setWrapSelectorWheel(false);

   // дефолтное значение свитча - выкл.
        sInt.setChecked(false);
        npInt1.setEnabled(false);
        npInt2.setEnabled(false);
        npInt3.setEnabled(false);
        // далее см. setInterval()

// Боковые датчики:
        dpir = new Dialog(CameraControl.this);
        dpir.setContentView(R.layout.setpir);
    //    dpir.getWindow().setLayout(280, 245);
        lp.copyFrom(dpir.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dpir.getWindow().setAttributes(lp);
        dpir.setTitle("Side PIR");
        txtPIR = (TextView)findViewById(R.id.txtpir);
        txtPIR.setText(sidePIR);


// MMS:
        dmms = new Dialog(CameraControl.this);
        dmms.setContentView(R.layout.setmmstype);
    //    dmms.getWindow().setLayout(280, 280);
        lp.copyFrom(dmms.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dmms.getWindow().setAttributes(lp);
        dmms.setTitle("MMS type");
        txtMMStype = (TextView)findViewById(R.id.txtMMStype);
        txtMMStype.setText(MMStype);

// Лимит фото в сутки:

        dlimit = new Dialog(CameraControl.this);
        dlimit.setContentView(R.layout.setlimitmms);
   //     dlimit.getWindow().setLayout(280, 380);
        lp.copyFrom(dlimit.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dlimit.getWindow().setAttributes(lp);
        dlimit.setTitle("MMS limit");
        tv38 = (TextView)findViewById(R.id.tv38);
        if (MMSlimit.equals("0")){tv38.setText("no limit");}
        else {tv38.setText(MMSlimit);}

// Таймер1:
        tv39 = (TextView)findViewById(R.id.textView39);
        if (timer.equals("On")) {tv39.setText("ON: "+timeronHH+"h"+":"+timeronMin+"m"+"\n"+"OFF: "+timeroffHH+"h"+":"+timeroffMin+"m");}
        else {tv39.setText("Off");}
        dtimer = new Dialog(CameraControl.this);
        dtimer.setContentView(R.layout.settimer);
   //     dtimer.getWindow().setLayout(298, 447);
        lp.copyFrom(dtimer.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dtimer.getWindow().setAttributes(lp);
        dtimer.setTitle("Set timer");
        sTimer = (Switch) dtimer.findViewById(R.id.switch2);
        npT1 = (NumberPicker) dtimer.findViewById(R.id.numberPicker11);
        npT1.setMaxValue(23);
        npT1.setMinValue(0);
     /*   String[] H = new String[24];
        for (int i=0; i<24; i++){
            if (i<10) H[i] = "0"+Integer.toString(i);
            else H [i] = Integer.toString(i);
        }
        npT1.setDisplayedValues(H); */
        npT1.setWrapSelectorWheel(false);
        npT2 = (NumberPicker) dtimer.findViewById(R.id.numberPicker12);
        npT2.setMaxValue(59);
        npT2.setMinValue(0);
     /*   String[] M = new String[60];
        for (int i=0; i<60; i++){
            if (i<10) M[i] = "0"+Integer.toString(i);
            else M [i] = Integer.toString(i);
        }
        npT2.setDisplayedValues(M); */
        npT2.setWrapSelectorWheel(false);
        npT3 = (NumberPicker) dtimer.findViewById(R.id.numberPicker13);
        npT3.setMaxValue(23);
        npT3.setMinValue(0);
    /*    String[] H1 = new String[24];
        for (int i=0; i<24; i++){
            if (i<10) H1[i] = "0"+Integer.toString(i);
            else H1 [i] = Integer.toString(i);
        }
        npT3.setDisplayedValues(H1); */
        npT3.setWrapSelectorWheel(false);
        npT4 = (NumberPicker) dtimer.findViewById(R.id.numberPicker14);
        npT4.setMaxValue(59);
        npT4.setMinValue(0);
    /*    String[] M1 = new String[60];
        for (int i=0; i<60; i++){
            if (i<10) M1 [i] = "0"+Integer.toString(i);
            else M1 [i] = Integer.toString(i);
        }
        npT4.setDisplayedValues(M1); */
        npT4.setWrapSelectorWheel(false);

        if (timer.equals("On")) {sTimer.setChecked(true);
            npT1.setValue(Integer.valueOf(timeronHH));
            npT2.setValue(Integer.valueOf(timeronMin));
            npT3.setValue(Integer.valueOf(timeroffHH));
            npT4.setValue(Integer.valueOf(timeroffMin));
        }
        else {sTimer.setChecked(false);
            npT1.setEnabled(false);
            npT1.setValue(Integer.valueOf(timeronHH));
            npT2.setEnabled(false);
            npT2.setValue(Integer.valueOf(timeronMin));
            npT3.setEnabled(false);
            npT3.setValue(Integer.valueOf(timeroffHH));
            npT4.setEnabled(false);
            npT4.setValue(Integer.valueOf(timeroffMin));}

        // далее см. settimer()

// Таймер2:
        tv47 = (TextView)findViewById(R.id.textView47);
        if (timer2.equals("On")) {tv47.setText("ON: "+timer2onHH+"h"+":"+timer2onMin+"m"+"\n"+"OFF: "+timer2offHH+"h"+":"+timer2offMin+"m");}
        else {tv47.setText("Off");}
        dtimer2 = new Dialog(CameraControl.this);
        dtimer2.setContentView(R.layout.settimer2);
    //    dtimer2.getWindow().setLayout(298, 447);
        lp.copyFrom(dtimer2.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dtimer2.getWindow().setAttributes(lp);
        dtimer2.setTitle("Set timer2");
        sTimer2 = (Switch) dtimer2.findViewById(R.id.switch2);
        npT12 = (NumberPicker) dtimer2.findViewById(R.id.numberPicker11);
        npT12.setMaxValue(23);
        npT12.setMinValue(0);
        npT12.setWrapSelectorWheel(false);
        npT22 = (NumberPicker) dtimer2.findViewById(R.id.numberPicker12);
        npT22.setMaxValue(59);
        npT22.setMinValue(0);
        npT22.setWrapSelectorWheel(false);
        npT32 = (NumberPicker) dtimer2.findViewById(R.id.numberPicker13);
        npT32.setMaxValue(23);
        npT32.setMinValue(0);
        npT32.setWrapSelectorWheel(false);
        npT42 = (NumberPicker) dtimer2.findViewById(R.id.numberPicker14);
        npT42.setMaxValue(59);
        npT42.setMinValue(0);
        npT42.setWrapSelectorWheel(false);
        // дефолтное значение свитча - выкл.
        if (timer2.equals("On")) {sTimer2.setChecked(true);
            npT12.setValue(Integer.valueOf(timer2onHH));
            npT22.setValue(Integer.valueOf(timer2onMin));
            npT32.setValue(Integer.valueOf(timer2offHH));
            npT42.setValue(Integer.valueOf(timer2offMin));
        }
        else {
        sTimer2.setChecked(false);
        npT12.setEnabled(false);
            npT12.setValue(Integer.valueOf(timer2onHH));
        npT22.setEnabled(false);
            npT22.setValue(Integer.valueOf(timer2onMin));
        npT32.setEnabled(false);
            npT32.setValue(Integer.valueOf(timer2offHH));
        npT42.setEnabled(false);
            npT42.setValue(Integer.valueOf(timer2offMin));}
        // далее см. settimer2()

// SMS interval:
        tv49 = (TextView)findViewById(R.id.textView49);
        tv49.setText(SMScontrol);
        dsmsint = new Dialog(CameraControl.this);
        dsmsint.setContentView(R.layout.smsnterval);
    //    dsmsint.getWindow().setLayout(280, 385);
        lp.copyFrom(dsmsint.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dsmsint.getWindow().setAttributes(lp);
        dsmsint.setTitle("SMS interval");
        ssmsint = (Switch) dsmsint.findViewById(R.id.switch3);
        npsmsint = (NumberPicker) dsmsint.findViewById(R.id.numberPicker2);
        npsmsint.setMaxValue(24);
        npsmsint.setMinValue(0);
        npsmsint.setWrapSelectorWheel(false);
        // дефолтное значение:
        ssmsint.setChecked(true);
        npsmsint.setValue(0);

        dsmsoff = new Dialog(CameraControl.this);
        dsmsoff.setContentView(R.layout.smsoff);
     //   dsmsoff.getWindow().setLayout(300, 268);
        lp.copyFrom(dsmsoff.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dsmsoff.getWindow().setAttributes(lp);
        dsmsoff.setTitle("Warning");


   // phone2 or email:
        dphone2 = new Dialog(CameraControl.this);
        dphone2.setContentView(R.layout.setaddphone2oremail);
   //     dphone2.getWindow().setLayout(300, 228);
        lp.copyFrom(dphone2.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dphone2.getWindow().setAttributes(lp);
        dphone2.setTitle("Set phone2 or email");
        edtxtphone2 = (EditText)dphone2.findViewById(R.id.editText4);
        txtviewphone2 = (TextView)findViewById(R.id.txtviewphone2);
        if (phone2orEmail.equals("")){txtviewphone2.setText(R.string.phone2);}
        else {txtviewphone2.setText(phone2orEmail);}

   // phone3 or email:
        dphone3 = new Dialog(CameraControl.this);
        dphone3.setContentView(R.layout.setaddphone3oremail);
    //    dphone3.getWindow().setLayout(300, 228);
        lp.copyFrom(dphone3.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dphone3.getWindow().setAttributes(lp);
        dphone3.setTitle("Set phone3 or email");
        edtxtphone3 = (EditText)dphone3.findViewById(R.id.editText4);
        txtviewphone3 = (TextView)findViewById(R.id.txtviewphone3);
        if (phone3orEmail.equals("")){txtviewphone3.setText(R.string.phone3);}
        else {txtviewphone3.setText(phone3orEmail);}

   // email:
      demail = new Dialog(CameraControl.this);
     //  AlertDialog.Builder demail = new AlertDialog.Builder(CameraControl2.this);
    //   RelativeLayout view = (RelativeLayout) getLayoutInflater().inflate(R.layout.setmail, null);
        // устанавливаем ее, как содержимое тела диалога
     //   demail.setView(view);
        demail.setContentView(R.layout.setmail);

     //  demail.getWindow().setBackgroundDrawableResource (R.drawable.button);
    //    demail.getWindow().setBackgroundDrawableResource (R.drawable.rounded);
    //    demail.setContentView(R.layout.setmail);


   //     demail.getWindow().setLayout(300, 228);
        lp.copyFrom(demail.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        demail.getWindow().setAttributes(lp);
        demail.setTitle("Set email");
    //    edtxtemail = (EditText)demail.findViewById(R.id.edtxtemail);
        edtxtemail = (EditText)demail.findViewById(R.id.edtxtemail);
        txtviewmail = (TextView)findViewById(R.id.txtviewmail);
        if (Email.equals("")){txtviewmail.setText(R.string.email);}
        else {txtviewmail.setText(Email);}
      }

    public void setmode(View v){
        Button b1 = (Button)dMode.findViewById(R.id.btnSendMode);
        Button b2 = (Button)dMode.findViewById(R.id.btnModeCancel);
        radioGroup = (RadioGroup) dMode.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
                switch (checkedIndex){
                    case 0:
                        mode = "Camera";
                        SMS = "ltl01*0#aa";
                        break;
                    case 1:
                        mode = "Video";
                        SMS = "ltl01*1#aa";
                        break;
                    case 2:
                        mode = "Cam+Video";
                        SMS = "ltl01*2#aa";
                        break;
                    }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //  TextView txtmode = (TextView)findViewById(R.id.txtmode);
               txtmode.setText(mode);
               dMode.dismiss();
               sendSMS (pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("mode", mode);
                db.update("cameras", cv, "pphone=?", args);
  //              cv.clear();
                db.close();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dMode.dismiss();
            }
        });
        dMode.show();
    }

    public void setfotosize(View v){
        Button b1 = (Button)dFotosize.findViewById(R.id.btnSendMode);
        Button b2 = (Button)dFotosize.findViewById(R.id.btnModeCancel);
        radioGroup = (RadioGroup) dFotosize.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
                switch (checkedIndex){
                    case 0:
                        fotosize = "12MP";
                        SMS = "ltl02*0#aa";
                        break;
                    case 1:
                        fotosize = "5MP";
                        SMS = "ltl02*1#aa";
                        break;
                    case 2:
                        fotosize = "2MP";
                        SMS = "ltl02*2#aa";
                        break;
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   TextView txt = (TextView)findViewById(R.id.txtfotosize);
                txtfotosize.setText(fotosize);
                dFotosize.dismiss();
                sendSMS (pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("fotosize", fotosize);
                db.update("cameras", cv, "pphone=?", args);
    //            cv.clear();
                db.close();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dFotosize.dismiss();
            }
        });
        dFotosize.show();
    }

    public void setvideosize(View v){
        Button b1 = (Button)dVideosize.findViewById(R.id.btnSendMode);
        Button b2 = (Button)dVideosize.findViewById(R.id.btnModeCancel);
        radioGroup = (RadioGroup) dVideosize.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
                switch (checkedIndex){
                    case 0:
                        videosize = "fullHD";
                        SMS = "ltl03*0#aa";
                        break;
                    case 1:
                        videosize = "halfHD";
                        SMS = "ltl03*1#aa";
                        break;
                    case 2:
                        videosize = "VGA";
                        SMS = "ltl03*2#aa";
                        break;
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtvideosize.setText(videosize);
                dVideosize.dismiss();
                sendSMS (pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("videosize", videosize);
                db.update("cameras", cv, "pphone=?", args);
      //          cv.clear();
                db.close();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dVideosize.dismiss();
            }
        });
        dVideosize.show();
    }

    public void setfotonumber(View v){
        Button b1 = (Button)dfotonumber.findViewById(R.id.btnSendMode);
        Button b2 = (Button)dfotonumber.findViewById(R.id.btnModeCancel);
        radioGroup = (RadioGroup) dfotonumber.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
                switch (checkedIndex){
                    case 0:
                        fotonumber = "1";
                        SMS = "ltl04*0#aa";
                        break;
                    case 1:
                        fotonumber = "2";
                        SMS = "ltl04*1#aa";
                        break;
                    case 2:
                        fotonumber = "3";
                        SMS = "ltl04*2#aa";
                        break;
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtfotonumber.setText(fotonumber);
                dfotonumber.dismiss();
                sendSMS (pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("fotonumber", fotonumber);
                db.update("cameras", cv, "pphone=?", args);
        //        cv.clear();
                db.close();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dfotonumber.dismiss();
            }
        });
        dfotonumber.show();
    }

    public void setvideolength(View v) {

        Button b1 = (Button) dVL.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dVL.findViewById(R.id.setmailbutton2);

        npvideolength.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
               // np.tag = newVal;
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videolength = String.valueOf(npvideolength.getValue());
                tv.setText(videolength);
                SMS = "ltl05*"+videolength+"#aa";
                dVL.dismiss();
                sendSMS (pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("videolength", videolength);
                db.update("cameras", cv, "pphone=?", args);
          //      cv.clear();
                db.close();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dVL.dismiss();
            }
        });
        dVL.show();
    }

    public void setdelay(View v) {

        Button b1 = (Button) dDEL.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dDEL.findViewById(R.id.setmailbutton2);

        delnp1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
            }
        });

        delnp2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
            {
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               delayMin = String.valueOf(delnp1.getValue());
               delaySec = String.valueOf(delnp2.getValue());
                if (delayMin.length()==1 && delaySec.length() ==1) {SMS = "ltl06*m"+"0"+delayMin+"s"+"0"+delaySec+"#aa";}
                if (delayMin.length()==2 && delaySec.length() ==1) {SMS = "ltl06*m"+delayMin+"s"+"0"+delaySec+"#aa";}
                if (delayMin.length()==1 && delaySec.length() ==2) {SMS = "ltl06*m"+"0"+delayMin+"s"+delaySec+"#aa";}
                if (delayMin.length()==2 && delaySec.length() ==2) {SMS = "ltl06*m"+delayMin+"s"+delaySec+"#aa";}
                dl.setText(delayMin+"m: "+ delaySec+"s");

                dDEL.dismiss();
                sendSMS(pphone,SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("delayMin", delayMin);
                cv.put("delaySec", delaySec);
                db.update("cameras", cv, "pphone=?", args);
            //    cv.clear();
                db.close();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dDEL.dismiss();
            }
        });
        dDEL.show();
    }

    public void setSerial2(View v) {

       //  d.setContentView(R.layout.dialog);
        Button b1 = (Button) dserial.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dserial.findViewById(R.id.setmailbutton2);

        edtxt = (EditText) dserial.findViewById(R.id.editText55);
        edtxt.setEnabled(false);
      //  edtxt.clearFocus();
        sserial  = (Switch)dserial.findViewById(R.id.switch1);
        sserial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                edtxt.setEnabled(true);
                else edtxt.setEnabled(false);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sserial.isChecked()){
                serial = edtxt.getText().toString();
                tview29.setText(serial);
                SMS = "ltl07*1"+serial+"#aa"; }
                else {serial = "4 letters/digits"; tview29.setText(serial); SMS = "07*0#";}

                dserial.dismiss();
                sendSMS(pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("serial", serial);
                db.update("cameras", cv, "pphone=?", args);
                db.close();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dserial.dismiss();
            }
        });
        dserial.show();
    }

    public void setsense (View v){
        Button b1 = (Button)dsense.findViewById(R.id.btnSendMode);
        Button b2 = (Button)dsense.findViewById(R.id.btnModeCancel);
        radioGroup = (RadioGroup) dsense.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
                switch (checkedIndex){
                    case 0:
                        sense = "High";
                        SMS = "ltl08*2#aa";
                        break;
                    case 1:
                        sense = "Normal";
                        SMS = "ltl08*1#aa";
                        break;
                    case 2:
                        sense = "Low";
                        SMS = "ltl08*0#aa";
                        break;
                    case 3:
                        sense = "Off";
                        SMS = "ltl08*3#aa";
                        break;
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtsense = (TextView)findViewById(R.id.txtsense);
                txtsense.setText(sense);
                dsense.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("sense", sense);
                db.update("cameras", cv, "pphone=?", args);
                db.close();
                sendSMS (pphone, SMS);

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsense.dismiss();
            }
        });
        dsense.show();
    }

    public void setsidepir (View v){
        Button b1 = (Button)dpir.findViewById(R.id.btnSendMode);
        Button b2 = (Button)dpir.findViewById(R.id.btnModeCancel);
        radioGroup = (RadioGroup) dpir.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
                switch (checkedIndex){
                    case 0:
                        sidePIR = "On";
                        SMS = "ltl10*1#aa";
                        break;
                    case 1:
                        sidePIR = "Off";
                        SMS = "ltl10*0#aa";
                        break;
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPIR.setText(sidePIR);
                dpir.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("sidePIR", sidePIR);
                db.update("cameras", cv, "pphone=?", args);
                db.close();
                sendSMS (pphone, SMS);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpir.dismiss();
            }
        });
        dpir.show();
    }

    public void setInterval(View v) {

        Button b1 = (Button) dINT.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dINT.findViewById(R.id.setmailbutton2);
       // String HH;
       // String Min;
       //  String Sec;

        npInt1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
                //   String HH = String.valueOf(npInt1.getValue());
            }
        });
        npInt2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
                //     Min = String.valueOf(npInt2.getValue());
            }
        });
        npInt3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;24
                //       Sec = String.valueOf(npInt3.getValue());
            }
        });

        sInt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    npInt1.setEnabled(true);
                    npInt2.setEnabled(true);
                    npInt3.setEnabled(true);

                } else {  npInt1.setEnabled(false);
                          npInt2.setEnabled(false);
                          npInt3.setEnabled(false);
                          //
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(sInt.isChecked() == true) {
              lapse = "ON";
              lapseHH = String.valueOf(npInt1.getValue());
              lapseMM = String.valueOf(npInt2.getValue());
              lapseSS = String.valueOf(npInt3.getValue());

              if (lapseHH.length()==1&&lapseMM.length()==1&&lapseSS.length()==1) SMS = "ltl16*1"+"0"+lapseHH+"0"+lapseMM+"0"+lapseSS+"#aa";
              if (lapseHH.length()==1&&lapseMM.length()==1&&lapseSS.length()==2) SMS = "ltl16*1"+"0"+lapseHH+"0"+lapseMM+lapseSS+"#aa";
              if (lapseHH.length()==1&&lapseMM.length()==2&&lapseSS.length()==1) SMS = "ltl16*1"+"0"+lapseHH+lapseMM+"0"+lapseSS+"#aa";
              if (lapseHH.length()==1&&lapseMM.length()==2&&lapseSS.length()==2) SMS = "ltl16*1"+"0"+lapseHH+lapseMM+lapseSS+"#aa";
              if (lapseHH.length()==2&&lapseMM.length()==1&&lapseSS.length()==1) SMS = "ltl16*1"+lapseHH+"0"+lapseMM+"0"+lapseSS+"#aa";
              if (lapseHH.length()==2&&lapseMM.length()==1&&lapseSS.length()==2) SMS = "ltl16*1"+lapseHH+"0"+lapseMM+lapseSS+"#aa";
              if (lapseHH.length()==2&&lapseMM.length()==2&&lapseSS.length()==1) SMS = "ltl16*1"+lapseHH+lapseMM+"0"+lapseSS+"#aa";
              if (lapseHH.length()==2&&lapseMM.length()==2&&lapseSS.length()==2) SMS = "ltl16*1"+lapseHH+lapseMM+lapseSS+"#aa";

              tv33.setText(lapseHH+"h:"+lapseMM+"m:"+lapseSS+"s");
              }
             else { lapse = "OFF"; tv33.setText("OFF"); SMS = "09*0#"; }

                dINT.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("lapse", lapse);
                cv.put("lapseHH", lapseHH);
                cv.put("lapseMM", lapseMM);
                cv.put("lapseSS", lapseSS);
                db.update("cameras", cv, "pphone=?", args);
                db.close();
                sendSMS(pphone,SMS);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dINT.dismiss();
            }
        });

        dINT.show();
    }

    public void setmmstype (View v){
        Button b1 = (Button)dmms.findViewById(R.id.btnSendMode);
        Button b2 = (Button)dmms.findViewById(R.id.btnModeCancel);
        radioGroup = (RadioGroup) dmms.findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
                switch (checkedIndex){
                    case 0:
                        MMStype = "VGA";
                        SMS = "ltl11*1#aa";
                        break;
                    case 1:
                        MMStype = "SMS";
                        SMS = "ltl11*2#aa";
                        break;
                    case 2:
                        MMStype = "Off";
                        SMS = "ltl11*0#aa";
                        break;
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtMMStype.setText(MMStype);
                dmms.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("MMS", MMStype);
                db.update("cameras", cv, "pphone=?", args);
                db.close();
                sendSMS (pphone, SMS);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dmms.dismiss();
            }
        });
        dmms.show();
    }

    public void setmmslimit(View v) {

        //  d.setContentView(R.layout.dialog);
        Button b1 = (Button) dlimit.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dlimit.findViewById(R.id.setmailbutton2);
        nplimit = (NumberPicker) dlimit.findViewById(R.id.numberPicker2);
        nplimit.setMaxValue(99);
        nplimit.setMinValue(0);
        nplimit.setWrapSelectorWheel(false);
        nplimit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MMSlimit = String.valueOf(nplimit.getValue());
                if (MMSlimit.equals("0")) tv38.setText("no limit");
                else tv38.setText(MMSlimit);
                SMS = "ltl15*"+MMSlimit+"#aa";
                dlimit.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("MMSlimit", MMSlimit);
                db.update("cameras", cv, "pphone=?", args);
                db.close();
                sendSMS(pphone,SMS);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlimit.dismiss();
            }
        });
        dlimit.show();
    }

    public void settimer(View v) {


        //  d.setContentView(R.layout.dialog);
        Button b1 = (Button) dtimer.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dtimer.findViewById(R.id.setmailbutton2);
        //   tview29 = (TextView)findViewById(R.id.textView29);

        //  edtxt.clearFocus();

        npT1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
            }
        });

        npT2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
            }
        });

        npT3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;24
            }
        });

        npT4.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
            }
        });

        sTimer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    npT1.setEnabled(true);
                    npT2.setEnabled(true);
                    npT3.setEnabled(true);
                    npT4.setEnabled(true);

                } else {
                    npT1.setEnabled(false);
                    npT2.setEnabled(false);
                    npT3.setEnabled(false);
                    npT4.setEnabled(false);
                    //
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sTimer.isChecked() == true) {
                    timer = "On";
                timeronHH = String.valueOf(npT1.getValue());
                    String SMStimeronHH;
                if (timeronHH.length()==1) SMStimeronHH = "0"+ timeronHH; else SMStimeronHH = timeronHH;

                timeronMin = String.valueOf(npT2.getValue());
                    String SMStimeronMin;
                    if (timeronMin.length()==1) SMStimeronMin = "0"+ timeronMin; else SMStimeronMin = timeronMin;

                timeroffHH = String.valueOf(npT3.getValue());
                    String SMStimeroffHH;
                    if (timeroffHH.length()==1) SMStimeroffHH = "0"+ timeroffHH; else SMStimeroffHH = timeroffHH;
                timeroffMin = String.valueOf(npT4.getValue());
                    String SMStimeroffMin;
                    if (timeroffMin.length()==1) SMStimeroffMin = "0"+ timeroffMin; else SMStimeroffMin = timeroffMin;


          //      String onHH = String.valueOf(npT1.getValue());
          //      String onMin = String.valueOf(npT2.getValue());
          //      String offHH = String.valueOf(npT3.getValue());
          //      String offMin = String.valueOf(npT4.getValue());
                tv39.setText("ON: "+timeronHH+"h"+":"+timeronMin+"m"+"\n"+"OFF: "+timeroffHH+"h"+":"+timeroffMin+"m");

                SMS = "ltl17*1"+SMStimeronHH+SMStimeronMin+SMStimeroffHH+SMStimeroffMin+"#aa"; }
                else {timer = "Off"; tv39.setText("Off"); SMS = "ltl17*0#aa";}
                dtimer.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("timer", timer);
                cv.put("timeronHH", timeronHH);
                cv.put("timeronMin", timeronMin);
                cv.put("timeroffHH", timeroffHH);
                cv.put("timeroffMin", timeroffMin);
                db.update("cameras", cv, "pphone=?", args);
                db.close();
                sendSMS(pphone,SMS);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtimer.dismiss();
            }
        });

        dtimer.show();
    }

    public void settimer2(View v) {


        //  d.setContentView(R.layout.dialog);
        Button b12 = (Button) dtimer2.findViewById(R.id.setmailbutton1);
        Button b22 = (Button) dtimer2.findViewById(R.id.setmailbutton2);
        //   tview29 = (TextView)findViewById(R.id.textView29);

        //  edtxt.clearFocus();

        npT12.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
            }
        });

        npT22.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
            }
        });

        npT32.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;24
            }
        });

        npT42.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
            }
        });

        sTimer2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    npT12.setEnabled(true);
                    npT22.setEnabled(true);
                    npT32.setEnabled(true);
                    npT42.setEnabled(true);

                } else {
                    npT12.setEnabled(false);
                    npT22.setEnabled(false);
                    npT32.setEnabled(false);
                    npT42.setEnabled(false);
                    //
                }
            }
        });

        b12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sTimer2.isChecked() == true) {
                    timer2 = "On";
                    timer2onHH = String.valueOf(npT12.getValue());
                    timer2onMin = String.valueOf(npT22.getValue());
                    timer2offHH = String.valueOf(npT32.getValue());
                    timer2offMin = String.valueOf(npT42.getValue());

                    String SMStimer2onHH;
                    if (timer2onHH.length()==1) SMStimer2onHH = "0"+ timer2onHH; else SMStimer2onHH = timer2onHH;

                    String SMStimer2onMin;
                    if (timer2onMin.length()==1) SMStimer2onMin = "0"+ timer2onMin; else SMStimer2onMin = timer2onMin;

                    String SMStimer2offHH;
                    if (timer2offHH.length()==1) SMStimer2offHH = "0"+ timer2offHH; else SMStimer2offHH = timer2offHH;

                    String SMStimer2offMin;
                    if (timer2offMin.length()==1) SMStimer2offMin = "0"+ timer2offMin; else SMStimer2offMin = timer2offMin;
                    tv47.setText("ON:"+timer2onHH+"h"+":"+timer2onMin+"m"+"\n"+"OFF:"+timer2offHH+"h"+":"+timer2offMin+"m");
                    SMS = "ltl18*1"+SMStimer2onHH+SMStimer2onMin+SMStimer2offHH+SMStimer2offMin+"#aa"; }

                else {timer2 = "Off"; tv47.setText("Off"); SMS = "ltl18*0#aa"; }
                dtimer2.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("timer2", timer2);
                cv.put("timer2onHH", timer2onHH);
                cv.put("timer2onMin", timer2onMin);
                cv.put("timer2offHH", timer2offHH);
                cv.put("timer2offMin", timer2offMin);
                db.update("cameras", cv, "pphone=?", args);
                db.close();
                sendSMS(pphone,SMS);
            }
        });
        b22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtimer2.dismiss();
            }
        });

        dtimer2.show();
    }

    public void setSMSinterval(View v) {

        //  d.setContentView(R.layout.dialog);
        Button b1 = (Button) dsmsint.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dsmsint.findViewById(R.id.setmailbutton2);

        npsmsint.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Save the value in the number picker
                // np.tag = newVal;
            }
        });
        ssmsint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                             // do something, the isChecked will be
                                             // true if the switch is in the On position
                                             if (isChecked) {
                                                 npsmsint.setEnabled(true);
                                             } else {
                                                 npsmsint.setEnabled(false);
                                             }
                                         }
                                     });

            b1.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if (ssmsint.isChecked()) {
                                              SMScontrol = String.valueOf(npsmsint.getValue());
                                              tv49.setText(SMScontrol);
                                              SMS = "ltl19*" + SMScontrol + "#aa";
                                              sendSMS(pphone, SMS);}
                                          else {
                                              Button b1 = (Button) dsmsoff.findViewById(R.id.offsmsbutton1);
                                              Button b2 = (Button) dsmsoff.findViewById(R.id.offsmsbutton2);
                                              b1.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            SMS = "ltl19*25#aa"; tv49.setText("Off");
                                                                            sendSMS(pphone, SMS);
                                                                            dsmsoff.dismiss();}
                                                                    });

                                              b2.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      dsmsoff.dismiss();}
                                              });
                                              dsmsoff.show();
                                          }
                                          db = dbHelper.getWritableDatabase();
                                          String args[] = new String[]{pphone};
                                          cv.put("SMScontrol", SMScontrol);
                                          db.update("cameras", cv, "pphone=?", args);
                                          db.close();
                                          dsmsint.dismiss();

                                      }
                                  }

            );
            b2.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                dsmsint.dismiss();
            }
            }

            );
            dsmsint.show();
        }
    public void setphone2(View v){
        Button b1 = (Button)dphone2.findViewById(R.id.setmailbutton1);
        Button b2 = (Button)dphone2.findViewById(R.id.setmailbutton2);
       b1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               phone2orEmail = edtxtphone2.getText().toString();
               SMS = "ltl12*"+phone2orEmail+"#aa";
               txtviewphone2.setText(phone2orEmail);
               dphone2.dismiss();
               db = dbHelper.getWritableDatabase();
               String args[] = new String[]{pphone};
               cv.put("phone2orEmail", phone2orEmail);
               db.update("cameras", cv, "pphone=?", args);
               db.close();
               sendSMS(pphone,SMS);
           }
       });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dphone2.dismiss();
            }
        });
        dphone2.show();
            }

    public void setphone3(View v){
        Button b1 = (Button)dphone3.findViewById(R.id.setmailbutton1);
        Button b2 = (Button)dphone3.findViewById(R.id.setmailbutton2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone3orEmail = edtxtphone3.getText().toString();
                SMS = "ltl13*"+phone3orEmail+"#aa";
                txtviewphone3.setText(phone3orEmail);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("phone3orEmail", phone3orEmail);
                db.update("cameras", cv, "pphone=?", args);
                db.close();
                dphone3.dismiss();
                sendSMS(pphone,SMS);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dphone3.dismiss();
            }
        });
        dphone3.show();
    }


    public void setmail (View v){
        Button b1 = (Button)demail.findViewById(R.id.setmailbutton1);
        Button b2 = (Button)demail.findViewById(R.id.setmailbutton2);
  //      Button b1 = (Button)findViewById(R.id.setmailbutton1);
  //      Button b2 = (Button)findViewById(R.id.setmailbutton2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email = edtxtemail.getText().toString();
                SMS = "ltl14*"+Email+"#aa";
                txtviewmail.setText(Email);
                demail.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("Email", Email);
                db.update("cameras", cv, "pphone=?", args);
                db.close();
                sendSMS(pphone,SMS);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demail.dismiss();
            }
        });
        demail.show();
    }

    protected void sendSMS (String pphone, String SMS){
        // проверка длины команды:
       // hello_str.getBytes().length    в этой версии не используется
        // отправка SMS:
        Intent intent = new Intent(this,SMSsender.class);
        intent.putExtra("number",pphone);
        intent.putExtra("text",SMS);
        startService(intent);
       Toast toast = Toast.makeText(getApplicationContext(), "SMS sending..."+SMS, Toast.LENGTH_SHORT);
       toast.show();

    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
     //   startActivity(new Intent(this, cameraView.class));
     //   finish();
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

                startActivity(new Intent(this, AddEditCamera.class).putExtra("who", "view").putExtra("name",table).putExtra("pphone", pphone));
                finish();
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
                SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
                // удаляем таблицу камеры
                String SQLstatement = "DROP TABLE IF EXISTS " + aphone;
                db.execSQL(SQLstatement);
                // удаление строки из cameras:
                db.delete("cameras", "pphone=?", new String[]{String.valueOf (pphone)});

                // если это последняя камера, имеющая включённый push, то останавливаем IMAP listener
                Cursor cursor1 = db.query("cameras", new String[]{"push"}, "push=?", new String[]{"enabled"}, null, null, null);
                if (cursor1.getCount()==0) {
                    startService(new Intent(CameraControl.this, IMAPListener.class).putExtra("action", "stopidle"));
                    ContentValues cv11 = new ContentValues();
                    cv11.put("push", "disabled");
                    db.update("smtp", cv11, null, null);
                }
                cursor1.close();
                db.close();

           //   MainActivity.dbHelper.close();

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
                        if (obs.size()==0){
                            // останавливаем сервис и обзервер:
                         //   startService(new Intent(CameraControl.this, StartContentObserverService.class).putExtra("action", "stop"));
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
                }
                catch (Exception f){ // файла нет
                    // обзервер не запущен
                }

                startActivity(new Intent(CameraControl.this, MainActivity.class));
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
}
