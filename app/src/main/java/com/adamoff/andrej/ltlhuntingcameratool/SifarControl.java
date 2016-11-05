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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.t.ltlhuntingcameratool.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class SifarControl extends ActionBarActivity {

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
    TextView tvsense; // sense
    TextView tvlog, tvsendlog;
    TextView tvsmscontrol;
    TextView tvzoom;
    TextView tvfotonumber;

    Dialog dMode; RadioGroup radioGroup; // установка режима работы
    Dialog dFotosize;  // установка размера фото
    Dialog dVideosize; // установка размера видео
    Dialog dfotonumber; NumberPicker npfotonumber; // установка числа фотографий
    Dialog dsense; // установка чувствительности датчика
    Dialog dlimit; NumberPicker nplimit; Switch swlim; // mms limit
    Dialog dzoom; NumberPicker npzoom; Switch swzoom;

    Dialog dVL; NumberPicker npvideolength; // установка длительности видеозаписи
    Dialog dDEL; NumberPicker delnp1, delnp2, delnp3; Switch swdelay; // delay - задержка повторного срабатывания
    Dialog dlog; Switch swlog;
    Dialog dsendlog;
    Dialog dserial; Switch sserial;// установка имени камеры
    Dialog dINT; NumberPicker npInt1, npInt2, npInt3; Switch sInt; // Интервал автосъёмки
    Dialog dtimer; NumberPicker npT1, npT2, npT3, npT4; Switch sTimer; // timer
    Dialog dsmsint; Dialog dsmsoff; NumberPicker npsmsint; Switch ssmsint; // интервал приема SMS
    Dialog dphone, demail; //1,dphone2,dphone3,dphone4,demail1,demail2,demail3,demail4;
    EditText edtxtphone,edtxtphone1,edtxtphone2,edtxtphone3,edtxtphone4,edtxtemail,edtxtemail1,edtxtemail2,edtxtemail3,edtxtemail4;
    TextView txtviewphone1,txtviewphone2,txtviewphone3,txtviewphone4,txtviewmail1,txtviewmail2,txtviewmail3,txtviewmail4;

    // дефолтные значения для пикеров:
    int VideoLength = 10;
    int VideoLengthNew = 0;
    int delMin=1, delSec=0; // минуты и секунды задержки повт. срабатывания
    //  int timeronHH, timeronMin, timeroffHH, timeroffMin;
    //  int timer2onHH, timer2onMin, timer2offHH, timer2offMin;

    String mode,fotosize, videosize, fotonumber, videolength,
            delay,delayH,delayMin,delaySec,serial,sense,lapse,lapseHH,lapseMM,lapseSS,sidePIR,MMStype,MMSlimit,
            timer,timeronHH, timeronMin, timeroffHH, timeroffMin,
            SMScontrol,phone1,phone2,phone3,phone4,email1,email2,email3,email4, zoom, log, sendlog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifar_control);
        Intent intent = getIntent();
        pphone = intent.getStringExtra("phone");
        aphone = "a" + (pphone.substring(1)); // удаляем +, ставим а
        table = intent.getStringExtra("name");

        cv = new ContentValues();

    // читаем параметры из БД:
        dbHelper = new MainActivity.DBHelper(this);
        db = dbHelper.getWritableDatabase();
        String columns[] = {"pphone", "name","mode","fotosize","videosize","fotonumber","videolength",
                "delay","delayH","delayMin","delaySec","serial","sense","lapse","lapseHH","lapseMM","lapseSS","MMSlimit",
                "timer","timeronHH", "timeronMin", "timeroffHH", "timeroffMin",
                "SMScontrol","phone1","phone2","phone3","phone4","email1","email2","email3","email4","zoom","log","sendlog"};

        String args [] = {pphone};
        // читаем строку таблицы с помощью объекта cursor
        Cursor cursor = db.query("sifar", columns, "pphone=?", args, null, null, null);
        cursor.moveToFirst();
        mode = cursor.getString(cursor.getColumnIndex("mode"));
        //    modepos = cursor.getColumnIndex("mode");
        fotosize = cursor.getString(cursor.getColumnIndex("fotosize"));
        videosize = cursor.getString(cursor.getColumnIndex("videosize"));
        fotonumber = cursor.getString(cursor.getColumnIndex("fotonumber"));
        videolength  = cursor.getString(cursor.getColumnIndex("videolength"));
        delay = cursor.getString(cursor.getColumnIndex("delay"));
        delayH = cursor.getString(cursor.getColumnIndex("delayH"));
        delayMin = cursor.getString(cursor.getColumnIndex("delayMin"));
        delaySec = cursor.getString(cursor.getColumnIndex("delaySec"));
        serial = cursor.getString(cursor.getColumnIndex("serial"));
        sense = cursor.getString(cursor.getColumnIndex("sense"));
        lapse = cursor.getString(cursor.getColumnIndex("lapse"));
        lapseHH = cursor.getString(cursor.getColumnIndex("lapseHH"));
        lapseMM = cursor.getString(cursor.getColumnIndex("lapseMM"));
        lapseSS = cursor.getString(cursor.getColumnIndex("lapseSS"));

        MMSlimit = cursor.getString(cursor.getColumnIndex("MMSlimit"));

        timer = cursor.getString(cursor.getColumnIndex("timer"));
        timeronHH = cursor.getString(cursor.getColumnIndex("timeronHH"));
        timeronMin = cursor.getString(cursor.getColumnIndex("timeronMin"));
        timeroffHH = cursor.getString(cursor.getColumnIndex("timeroffHH"));
        timeroffMin = cursor.getString(cursor.getColumnIndex("timeroffMin"));

        SMScontrol = cursor.getString(cursor.getColumnIndex("SMScontrol"));
        phone1 = cursor.getString(cursor.getColumnIndex("phone1"));
        phone2 = cursor.getString(cursor.getColumnIndex("phone2"));
        phone3 = cursor.getString(cursor.getColumnIndex("phone3"));
        phone4 = cursor.getString(cursor.getColumnIndex("phone4"));
        email1 = cursor.getString(cursor.getColumnIndex("email1"));
        email2 = cursor.getString(cursor.getColumnIndex("email2"));
        email3 = cursor.getString(cursor.getColumnIndex("email3"));
        email4 = cursor.getString(cursor.getColumnIndex("email4"));
        zoom = cursor.getString(cursor.getColumnIndex("zoom"));
        log = cursor.getString(cursor.getColumnIndex("log"));
        sendlog = cursor.getString(cursor.getColumnIndex("sendlog"));

        db.close();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

    // диалог с выбором параметра mode и отправкой SMS
        dMode = new Dialog(SifarControl.this);
        dMode.setContentView(R.layout.sif_setmode);
        lp.copyFrom(dMode.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dMode.getWindow().setAttributes(lp);
        dMode.setTitle("Mode");
        txtmode = (TextView)findViewById(R.id.txt3mode);
        txtmode.setText(mode);

    // диалог с выбором параметра fotosize и отправкой SMS
        dFotosize = new Dialog(SifarControl.this);
        dFotosize.setContentView(R.layout.sif_photosize);
        //  dFotosize.getWindow().setLayout(280, 290);
        lp.copyFrom(dFotosize.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dFotosize.getWindow().setAttributes(lp);
        dFotosize.setTitle("Foto size");
        txtfotosize = (TextView)findViewById(R.id.txt3fotosize);
        txtfotosize.setText(fotosize);

    // диалог с выбором параметра Videosize и отправкой SMS
        dVideosize = new Dialog(SifarControl.this);
        dVideosize.setContentView(R.layout.setvideosize);
        //   dVideosize.getWindow().setLayout(280, 290);
        lp.copyFrom(dVideosize.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dVideosize.getWindow().setAttributes(lp);
        dVideosize.setTitle("Video size");
        txtvideosize = (TextView)findViewById(R.id.txt3videosize);
        txtvideosize.setText(videosize);

    // диалог fotonumber
        dfotonumber = new Dialog(SifarControl.this);
        dfotonumber.setContentView(R.layout.sif_setphotonumber);
        //   dfotonumber.getWindow().setLayout(280, 288);
        npfotonumber = (NumberPicker) dfotonumber.findViewById(R.id.numberPicker2);
        npfotonumber.setMaxValue(7);
        npfotonumber.setMinValue(1);
        // дефолтное значение
        npfotonumber.setValue(Integer.valueOf(fotonumber)); //
        npfotonumber.setWrapSelectorWheel(false);
        lp.copyFrom(dfotonumber.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dfotonumber.getWindow().setAttributes(lp);
        dfotonumber.setTitle("Foto number");
        txtfotonumber = (TextView)findViewById(R.id.txt3fotonumber);
        txtfotonumber.setText(fotonumber);

    // Zoom:
        dzoom = new Dialog(SifarControl.this);
        dzoom.setContentView(R.layout.sif_setzoom);
        //     dlimit.getWindow().setLayout(280, 380);
        lp.copyFrom(dzoom.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dzoom.getWindow().setAttributes(lp);
        dzoom.setTitle("Zoom");
        tvzoom = (TextView)findViewById(R.id.textView8);
        if (zoom.equals("1")) tvzoom.setText("Off");
           else tvzoom.setText(zoom);

    // создаём диалог с numberpicker для установки длительности видео:
        tv = (TextView) findViewById(R.id.text3View23);
        tv.setText(videolength);
        dVL = new Dialog(SifarControl.this);
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
        npvideolength.setMinValue(5);
        // дефолтное значение
        npvideolength.setValue(Integer.valueOf(videolength)); // 5 сек
        npvideolength.setWrapSelectorWheel(false);

    // диалог с numberpicker для установки задержки повторного срабатывания MM:SS
        dl = (TextView) findViewById(R.id.text3View25);
        if (!delay.equals("off")) dl.setText(delayH+"h: "+delayMin+"m:"+delaySec+"s");
            else dl.setText("Off");
        dDEL = new Dialog(SifarControl.this);
        dDEL.setContentView(R.layout.sif_setdelay);
        //     dDEL.getWindow().setLayout(280, 372);
        lp.copyFrom(dDEL.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dDEL.getWindow().setAttributes(lp);
        dDEL.setTitle("Delay");
        Switch sdel = (Switch)dDEL.findViewById(R.id.switch2);
        delnp1 = (NumberPicker) dDEL.findViewById(R.id.numberPicker1);
        delnp1.setMaxValue(23);
        delnp1.setMinValue(0);
        delnp1.setWrapSelectorWheel(false);
        // устанавливаем дефолтное значение. Заменить на значение из БД
        delnp1.setValue(Integer.valueOf(delayH));
        delnp2 = (NumberPicker) dDEL.findViewById(R.id.numberPicker2);
        delnp2.setMaxValue(59);
        delnp2.setMinValue(0);
        delnp2.setWrapSelectorWheel(false);
        delnp2.setValue(Integer.valueOf(delayMin));
        delnp3 = (NumberPicker) dDEL.findViewById(R.id.numberPicker3);
        delnp3.setMaxValue(59);
        delnp3.setMinValue(0);
        delnp3.setWrapSelectorWheel(false);
        delnp3.setValue(Integer.valueOf(delaySec));
        // дефолтное значение свитча - выкл.
       if (delay.equals("on")){
           sdel.setChecked(true);
           delnp1.setEnabled(true);
           delnp2.setEnabled(true);
       } else {
           sdel.setChecked(false);
           delnp1.setEnabled(false);
           delnp2.setEnabled(false);
       }

    // установка имени (serial numbers) камеры:
        dserial = new Dialog(SifarControl.this);
        dserial.setContentView(R.layout.sif_setserial);
        //  dserial.getWindow().setLayout(280, 280);
        lp.copyFrom(dserial.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dserial.getWindow().setAttributes(lp);
        dserial.setTitle("Set camera name");
        tview29 = (TextView)findViewById(R.id.text3View29);
        tview29.setText(serial);

    // установка чувствительности камеры:
        dsense = new Dialog(SifarControl.this);
        dsense.setContentView(R.layout.sif_setsense);
        tvsense = (TextView)findViewById(R.id.txt3sense);

        switch (sense){
            case "high": tvsense.setText("high"); break;
            case "normal": tvsense.setText("normal"); break;
            case "low":   tvsense.setText("low"); break;
        }
        lp.copyFrom(dsense.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dsense.getWindow().setAttributes(lp);
        dsense.setTitle("Sensor sensitivity");

    // лог:
        dlog = new Dialog(SifarControl.this);
        dlog.setContentView(R.layout.sif_setlog);
        tvlog = (TextView)findViewById(R.id.textView10);
        TableRow tbr = (TableRow)findViewById(R.id.tbrowsendlog);
        if (log.equals("on")) {tvlog.setText("On"); tbr.setClickable(true);}
           else {tvlog.setText("Off"); tbr.setClickable(false);}
        lp.copyFrom(dlog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dlog.getWindow().setAttributes(lp);
        dlog.setTitle("Log");

    // отправка лога:
        dsendlog = new Dialog(SifarControl.this);
        dsendlog.setContentView(R.layout.sif_sendlog);
        tvsendlog = (TextView)findViewById(R.id.textView12);
        if (log.equals("off")) {
            //tvsendlog.setText("Off");
            tvsendlog.setText("enable Log first");}
           else tvsendlog.setText(sendlog);
        lp.copyFrom(dsendlog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dsendlog.getWindow().setAttributes(lp);
        dsendlog.setTitle("Send log");

    // "Интервал автосъёмки:"
        tv33 = (TextView)findViewById(R.id.text3View33);
        if (lapse.equals("off")){tv33.setText("Off");}
        else {tv33.setText(lapseHH+"h: "+lapseMM+"m: "+lapseSS+"s: ");}
        dINT = new Dialog(SifarControl.this);
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

        // далее см. setInterval()

    // Лимит фото в сутки:
        dlimit = new Dialog(SifarControl.this);
        dlimit.setContentView(R.layout.sif_setmmslimit);
        //     dlimit.getWindow().setLayout(280, 380);
        lp.copyFrom(dlimit.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dlimit.getWindow().setAttributes(lp);
        dlimit.setTitle("MMS limit");
        tv38 = (TextView)findViewById(R.id.tv383);
        if (MMSlimit.equals("0")){tv38.setText("no limit");}
        else {tv38.setText(MMSlimit);}

    // Таймер1:
        tv39 = (TextView)findViewById(R.id.text3View39);
        if (timer.equals("on")) {tv39.setText("ON: "+timeronHH+"h"+":"+timeronMin+"m"+"\n"+"OFF: "+timeroffHH+"h"+":"+timeroffMin+"m");}
        else {tv39.setText("Off");}
        dtimer = new Dialog(SifarControl.this);
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
        npT1.setWrapSelectorWheel(false);
        npT2 = (NumberPicker) dtimer.findViewById(R.id.numberPicker12);
        npT2.setMaxValue(59);
        npT2.setMinValue(0);
        npT2.setWrapSelectorWheel(false);
        npT3 = (NumberPicker) dtimer.findViewById(R.id.numberPicker13);
        npT3.setMaxValue(23);
        npT3.setMinValue(0);
        npT3.setWrapSelectorWheel(false);
        npT4 = (NumberPicker) dtimer.findViewById(R.id.numberPicker14);
        npT4.setMaxValue(59);
        npT4.setMinValue(0);
        npT4.setWrapSelectorWheel(false);

        if (timer.equals("on")) {sTimer.setChecked(true);
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

    // SMS control:
        dsmsint = new Dialog(SifarControl.this);
        dsmsint.setContentView(R.layout.sif_smscontrol);
        tvsmscontrol = (TextView)findViewById(R.id.text3View49);
        if (SMScontrol.equals("upontrigger")) tvsmscontrol.setText("Upon Trigger");
           else tvsmscontrol.setText("All the time");
        //    dsmsint.getWindow().setLayout(280, 385);
        lp.copyFrom(dsmsint.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dsmsint.getWindow().setAttributes(lp);
        dsmsint.setTitle("SMS control");

    // phone:
        dphone = new Dialog(SifarControl.this);
        dphone.setContentView(R.layout.setaddphone2oremail);
        //     dphone2.getWindow().setLayout(300, 228);
        lp.copyFrom(dphone.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dphone.getWindow().setAttributes(lp);
        dphone.setTitle("Set phone");
        txtviewphone1 = (TextView)findViewById(R.id.txt3viewphone1);
        txtviewphone2 = (TextView)findViewById(R.id.txt3viewphone2);
        txtviewphone3 = (TextView)findViewById(R.id.txt3viewphone3);
        txtviewphone4 = (TextView)findViewById(R.id.txt3viewphone4);
        txtviewphone1.setText("Phone 1: " + phone1);
        txtviewphone2.setText("Phone 2: " + phone2);
        txtviewphone3.setText("Phone 3: " + phone3);
        txtviewphone4.setText("Phone 4: " + phone4);

    // email:
        demail = new Dialog(SifarControl.this);
        demail.setContentView(R.layout.setmail);

        lp.copyFrom(demail.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        demail.getWindow().setAttributes(lp);
        demail.setTitle("Set email");

        txtviewmail1 = (TextView)findViewById(R.id.txt3viewmail1);
        txtviewmail2 = (TextView)findViewById(R.id.txt3viewmail2);
        txtviewmail3 = (TextView)findViewById(R.id.txt3viewmail3);
        txtviewmail4 = (TextView)findViewById(R.id.txt3viewmail4);
        txtviewmail1.setText("Email 1: " + email1);
        txtviewmail2.setText("Email 2: " + email2);
        txtviewmail3.setText("Email 3: " + email3);
        txtviewmail4.setText("Email 4: " + email4);
    }

    public void setmode(View v){
        Button b1 = (Button)dMode.findViewById(R.id.btnSendMode);
        Button b2 = (Button)dMode.findViewById(R.id.btnModeCancel);
        radioGroup = (RadioGroup) dMode.findViewById(R.id.sif_radiogroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
                switch (checkedIndex){
                    case 0:
                        mode = "Camera";
                        SMS = "01*1#";
                        break;
                    case 1:
                        mode = "Cam+Video";
                        SMS = "01*2#";
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
                db.update("sifar", cv, "pphone=?", args);
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
                        SMS = "02*1#";
                        break;
                    case 1:
                        fotosize = "8MP";
                        SMS = "02*2#";
                        break;
                    case 2:
                        fotosize = "5MP";
                        SMS = "02*3#";
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
                db.update("sifar", cv, "pphone=?", args);
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

    public void setzoom (View v){
        Button b1 = (Button)dzoom.findViewById(R.id.setmailbutton1);
        Button b2 = (Button)dzoom.findViewById(R.id.setmailbutton2);
        npzoom = (NumberPicker) dzoom.findViewById(R.id.npzoom);
        npzoom.setMaxValue(4);
        npzoom.setMinValue(2);
        npzoom.setWrapSelectorWheel(false);
        swzoom = (Switch)dzoom.findViewById(R.id.switch4);
        // устанавливаем дефолтное значение
        if (zoom.equals("1")) {
            swzoom.setChecked(false);
            npzoom.setValue(Integer.valueOf("2"));
            npzoom.setEnabled(false);
        } else {
            swzoom.setChecked(true);
            npzoom.setValue(Integer.valueOf(zoom));
        }
        swzoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                  // do something, the isChecked will be
                                                  // true if the switch is in the On position
                                                  if (isChecked) {
                                                      npzoom.setEnabled(true);
                                                  } else npzoom.setEnabled(false);
                                              }
                                          });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!swzoom.isChecked()){
                    zoom = "1";
                    SMS = "06*1#";
                    tvzoom.setText("Off");
                } else {
                    zoom = String.valueOf(npzoom.getValue());
                    SMS = "06*"+zoom+"#";
                    tvzoom.setText(zoom);
                }
                dzoom.dismiss();
                sendSMS (pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("zoom", zoom);
                db.update("sifar", cv, "pphone=?", args);
                //          cv.clear();
                db.close();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dzoom.dismiss();
            }
        });
        dzoom.show();
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
                        SMS = "17*1#";
                        break;
                    case 1:
                        videosize = "halfHD";
                        SMS = "17*2#";
                        break;
                    case 2:
                        videosize = "VGA";
                        SMS = "17*3#";
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
                db.update("sifar", cv, "pphone=?", args);
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
        Button b1 = (Button) dfotonumber.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dfotonumber.findViewById(R.id.setmailbutton2);
        tvfotonumber =(TextView)findViewById(R.id.txt3fotonumber);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotonumber = String.valueOf(npfotonumber.getValue());
            //    tv.setText(videolength);
                SMS = "03*"+fotonumber+"#";
                tvfotonumber.setText(fotonumber);
                dfotonumber.dismiss();
                sendSMS (pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("fotonumber", fotonumber);
                db.update("sifar", cv, "pphone=?", args);
                //      cv.clear();
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
                SMS = "18*"+videolength+"#";
                dVL.dismiss();
                sendSMS (pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("videolength", videolength);
                db.update("sifar", cv, "pphone=?", args);
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
        swdelay = (Switch)dDEL.findViewById(R.id.switch2);
        if (delay.equals("off")) {
            swdelay.setChecked(false);
        delnp1.setEnabled(false);
        delnp2.setEnabled(false);
        delnp3.setEnabled(false);
        } else {swdelay.setChecked(true);
            delnp1.setEnabled(true); delnp1.setValue(Integer.valueOf(delayH));
            delnp2.setEnabled(true); delnp2.setValue(Integer.valueOf(delayMin));
            delnp3.setEnabled(true); delnp3.setValue(Integer.valueOf(delaySec));
        }
        swdelay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    delnp1.setEnabled(true);
                    delnp2.setEnabled(true);
                    delnp3.setEnabled(true);
                }
                else {
                    delnp1.setEnabled(false);
                    delnp2.setEnabled(false);
                    delnp3.setEnabled(false);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swdelay.isChecked()) {
                delay = "on";
                delayH = String.valueOf(delnp1.getValue());
                delayMin = String.valueOf(delnp2.getValue());
                delaySec = String.valueOf(delnp3.getValue());
                if (delayH.length() == 1 && delayMin.length() == 1 && delaySec.length() == 1) {SMS = "10*"+"0"+delayH+"0"+delayMin+"0"+delaySec+"#";}
                if (delayH.length() == 1 && delayMin.length() == 2 && delaySec.length() == 1) {SMS = "10*"+"0"+delayH+delayMin+"0"+delaySec+"#";}
                if (delayH.length() == 1 && delayMin.length() == 2 && delaySec.length() == 2) {SMS = "10*"+"0"+delayH+delayMin+delaySec+"#";}
                if (delayH.length() == 1 && delayMin.length() == 1 && delaySec.length() == 2) {SMS = "10*"+"0"+delayH+"0"+delayMin+delaySec+"#";}
                if (delayH.length() == 2 && delayMin.length() == 1 && delaySec.length() == 1) {SMS = "10*"+delayH+"0"+delayMin+"0"+delaySec+"#";}
                if (delayH.length() == 2 && delayMin.length() == 2 && delaySec.length() == 1) {SMS = "10*"+delayH+delayMin+"0"+delaySec+"#";}
                if (delayH.length() == 2 && delayMin.length() == 1 && delaySec.length() == 2) {SMS = "10*"+delayH+"0"+delayMin+delaySec+"#";}
                if (delayH.length() == 2 && delayMin.length() == 2 && delaySec.length() == 2) {SMS = "10*"+delayH+delayMin+delaySec+"#";}

                dl.setText(delayH+ "h: "+delayMin + "m: " + delaySec + "s");

            } else {
                    SMS = ("10*#");
                    delay ="off";
                    dl.setText("Off");
                }
                dDEL.dismiss();
                sendSMS(pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("delay", delay);
                cv.put("delayH", delayH);
                cv.put("delayMin", delayMin);
                cv.put("delaySec", delaySec);
                db.update("sifar", cv, "pphone=?", args);
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
        edtxt.setText(serial);
       // edtxt.setEnabled(false);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      if (sserial.isChecked()){
                    serial = edtxt.getText().toString();
                    tview29.setText(serial);
                    SMS = "16*"+serial+"#";
                //}
        //        else {serial = "4 letters/digits"; tview29.setText(serial); SMS = "07*0#";}

                dserial.dismiss();
                sendSMS(pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                if (serial==null || serial ==""){cv.put("serial", "4 letters/digits");}
                    else cv.put("serial", serial);
                db.update("sifar", cv, "pphone=?", args);
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
        radioGroup = (RadioGroup) dsense.findViewById(R.id.rgroup_sense);
        switch (sense){
            case "high": ((RadioButton)radioGroup.getChildAt(0)).setChecked(true); break;
            case "normal": ((RadioButton)radioGroup.getChildAt(1)).setChecked(true); break;
            case "low": ((RadioButton)radioGroup.getChildAt(2)).setChecked(true); break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
                switch (checkedIndex){
                    case 0:
                        sense = "high";
                        SMS = "05*3#";
                        break;
                    case 1:
                        sense = "normal";
                        SMS = "05*2#";
                        break;
                    case 2:
                        sense = "low";
                        SMS = "05*1#";
                        break;
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         //       txtsense = (TextView)findViewById(R.id.txt3sense);
                tvsense.setText(sense);
                dsense.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("sense", sense);
                db.update("sifar", cv, "pphone=?", args);
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

     public void setInterval(View v) {

        Button b1 = (Button) dINT.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dINT.findViewById(R.id.setmailbutton2);

         // дефолтное значение свитча - выкл.
         if (lapse.equals("off")) {
             sInt.setChecked(false);
             npInt1.setEnabled(false); npInt1.setValue(Integer.valueOf(lapseHH));
             npInt2.setEnabled(false); npInt2.setValue(Integer.valueOf(lapseMM));
             npInt3.setEnabled(false); npInt3.setValue(Integer.valueOf(lapseSS));
         }
         else {
             sInt.setChecked(true);
             npInt1.setEnabled(true); npInt1.setValue(Integer.valueOf(lapseHH));
             npInt2.setEnabled(true); npInt2.setValue(Integer.valueOf(lapseMM));
             npInt3.setEnabled(true); npInt3.setValue(Integer.valueOf(lapseSS));
         }

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
                if(sInt.isChecked()) {
                    lapse = "on";
                    lapseHH = String.valueOf(npInt1.getValue());
                    lapseMM = String.valueOf(npInt2.getValue());
                    lapseSS = String.valueOf(npInt3.getValue());

                    if (lapseHH.length()==1&&lapseMM.length()==1&&lapseSS.length()==1) SMS = "04*"+"0"+lapseHH+"0"+lapseMM+"0"+lapseSS+"#";
                    if (lapseHH.length()==1&&lapseMM.length()==1&&lapseSS.length()==2) SMS = "04*"+"0"+lapseHH+"0"+lapseMM+lapseSS+"#";
                    if (lapseHH.length()==1&&lapseMM.length()==2&&lapseSS.length()==1) SMS = "04*"+"0"+lapseHH+lapseMM+"0"+lapseSS+"#";
                    if (lapseHH.length()==1&&lapseMM.length()==2&&lapseSS.length()==2) SMS = "04*"+"0"+lapseHH+lapseMM+lapseSS+"#";
                    if (lapseHH.length()==2&&lapseMM.length()==1&&lapseSS.length()==1) SMS = "04*"+lapseHH+"0"+lapseMM+"0"+lapseSS+"#";
                    if (lapseHH.length()==2&&lapseMM.length()==1&&lapseSS.length()==2) SMS = "04*"+lapseHH+"0"+lapseMM+lapseSS+"#";
                    if (lapseHH.length()==2&&lapseMM.length()==2&&lapseSS.length()==1) SMS = "04*"+lapseHH+lapseMM+"0"+lapseSS+"#";
                    if (lapseHH.length()==2&&lapseMM.length()==2&&lapseSS.length()==2) SMS = "04*"+lapseHH+lapseMM+lapseSS+"#";

                    tv33.setText(lapseHH+"h:"+lapseMM+"m:"+lapseSS+"s");
                }
                else { lapse = "off"; tv33.setText("Off"); SMS = "04*#"; }

                dINT.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("lapse", lapse);
                cv.put("lapseHH", lapseHH);
                cv.put("lapseMM", lapseMM);
                cv.put("lapseSS", lapseSS);
                db.update("sifar", cv, "pphone=?", args);
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

    public void setmmslimit(View v) {

        //  d.setContentView(R.layout.dialog);
        Button b1 = (Button) dlimit.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dlimit.findViewById(R.id.setmailbutton2);
        nplimit = (NumberPicker) dlimit.findViewById(R.id.numberPicker2);
        nplimit.setMaxValue(99);
        nplimit.setMinValue(1);
        nplimit.setWrapSelectorWheel(false);
        swlim = (Switch)dlimit.findViewById(R.id.switch4);
        if (MMSlimit.equals("0")){  // no limit
            swlim.setChecked(false);
            nplimit.setEnabled(false);
        }
        else {
            swlim.setChecked(true);
            nplimit.setEnabled(true);
            nplimit.setValue(Integer.valueOf(MMSlimit));
        }

        swlim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    nplimit.setEnabled(true);
                } else {
                    nplimit.setEnabled(false);
                }
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swlim.isChecked()){
                    MMSlimit = String.valueOf(nplimit.getValue());
                tv38.setText(MMSlimit);
                SMS = "09*" + MMSlimit + "#";
            } else {
                    MMSlimit="0";
                    tv38.setText("No limit");
                    SMS = "09*#";
                }
                dlimit.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("MMSlimit", MMSlimit);
                db.update("sifar", cv, "pphone=?", args);
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
                    timer = "on";
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

                    SMS = "11*"+SMStimeronHH+SMStimeronMin+"00"+"-"+SMStimeroffHH+SMStimeroffMin+"00"+"#"; }// seconds are not used
                else {timer = "off"; tv39.setText("Off"); SMS = "11*#";}
                dtimer.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("timer", timer);
                cv.put("timeronHH", timeronHH);
                cv.put("timeronMin", timeronMin);
                cv.put("timeroffHH", timeroffHH);
                cv.put("timeroffMin", timeroffMin);
                db.update("sifar", cv, "pphone=?", args);
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

    public void setlog (View v){

        Button b1 = (Button) dlog.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dlog.findViewById(R.id.setmailbutton2);
        swlog  = (Switch)dlog.findViewById(R.id.switch1);
    //    tvsendlog.setText("enable Log first");
  final TableRow tbr = (TableRow)findViewById(R.id.tbrowsendlog);

        if (log.equals("on")){
            swlog.setChecked(true);
        }
        else {
            swlog.setChecked(false);
             }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swlog.isChecked()){
                    log = "on";
                    SMS = "14*#";
                    tvlog.setText("On");
                    tbr.setClickable(true);
                    tvsendlog.setText(sendlog);
                } else {
                    log = "off";
                    SMS = "14*1#";
                    tvlog.setText("Off");
           //         TableRow tbr = (TableRow)findViewById(R.id.tbrowsendlog);
                    tbr.setClickable(false);
                    tvsendlog.setText("enable Log first");
                }
                sendSMS(pphone, SMS);
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("log", log);
                db.update("sifar", cv, "pphone=?", args);
                db.close();
                dlog.dismiss();
            }
        } );
        b2.setOnClickListener(new View.OnClickListener()
                              {
                                  @Override
                                  public void onClick(View v){
                                      dlog.dismiss();
                                  }
                              }
        );
        dlog.show();

    }

    public void sendlog (View v){

        Button b1 = (Button) dsendlog.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dsendlog.findViewById(R.id.setmailbutton2);
        RadioGroup rg = (RadioGroup)dsendlog.findViewById(R.id.rgroup_log);
          switch (sendlog) {
                case "phones": ((RadioButton)rg.getChildAt(0)).setChecked(true); break;
                case "emails": ((RadioButton)rg.getChildAt(1)).setChecked(true); break;
                case "ftp": ((RadioButton)rg.getChildAt(2)).setChecked(true); break;
            }

        rg=null;

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup rg = (RadioGroup)dsendlog.findViewById(R.id.rgroup_log);
                switch (rg.getCheckedRadioButtonId()) {
                        case R.id.rblogphone:
                            sendlog = "phones";
                            tvsendlog.setText("phones");
                            SMS = "15*1#";
                            sendSMS(pphone, SMS);
                            break;
                        case R.id.rblogemail:
                            sendlog = "emails";
                            tvsendlog.setText("emails");
                            SMS = "15*2#";
                            sendSMS(pphone, SMS);
                            break;
                        case R.id.rblogftp:
                            sendlog = "ftp";
                            tvsendlog.setText("FTP");
                            SMS = "15*3#";
                            sendSMS(pphone, SMS);
                            break;
            //            default:  dsendlog.dismiss(); break;
                    }
                Toast.makeText(getApplicationContext(), "radioGroup "+rg.getCheckedRadioButtonId(), Toast.LENGTH_LONG).show();

                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("sendlog", sendlog);
                db.update("sifar", cv, "pphone=?", args);
                db.close();
                dsendlog.dismiss();
            }
        } );
        b2.setOnClickListener(new View.OnClickListener()
                              {
                                  @Override
                                  public void onClick(View v){
                                      dsendlog.dismiss();
                                  }
                              }
        );
        dsendlog.show();

    }

    public void setSMScontrol(View v) {

        //  d.setContentView(R.layout.dialog);
        Button b1 = (Button) dsmsint.findViewById(R.id.btnSendMode);
        Button b2 = (Button) dsmsint.findViewById(R.id.btnModeCancel);
  final     RadioGroup rg = (RadioGroup)dsmsint.findViewById(R.id.rgsmscontrol);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (rg.getCheckedRadioButtonId()) {
                    case R.id.smscntrl1:
                        SMScontrol = "upontrigger";
                        tvsmscontrol.setText("Upon Trigger");
                        SMS = "13*1#";
                        sendSMS(pphone, SMS);
                        break;
                    case R.id.smscntrl2:
                        SMScontrol = "alltime";
                        tvsmscontrol.setText("All the time");
                        SMS = "13*2#";
                        sendSMS(pphone, SMS);
                        break;
                }
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                cv.put("SMScontrol", SMScontrol);
                db.update("sifar", cv, "pphone=?", args);
                db.close();
                dsmsint.dismiss();
            }
        } );
        b2.setOnClickListener(new View.OnClickListener()
                              {
                                  @Override
                                  public void onClick(View v){
                                      dsmsint.dismiss();
                                  }
                              }
        );
        dsmsint.show();
    }

    public void setphone(View v) {
        Button b1 = (Button) dphone.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dphone.findViewById(R.id.setmailbutton2);
        edtxtphone2 = (EditText)dphone.findViewById(R.id.editText4);
        edtxtphone2.setHint("enter phone");


  final int f;
        switch (v.getId()) {
            case R.id.tbrow163:
                f = 1; if (!phone1.equals(""))edtxtphone2.setText(phone1); else edtxtphone2.setHint("enter phone1");
                break;
            case R.id.tbrow173:
                f = 2; if (!phone2.equals(""))edtxtphone2.setText(phone2); else edtxtphone2.setHint("enter phone2");
                break;
            case R.id.tbrow183:
                f = 3; if (!phone3.equals(""))edtxtphone2.setText(phone3); else edtxtphone2.setHint("enter phone3");
                break;
            case R.id.tbrow193:
                f = 4; if (!phone4.equals(""))edtxtphone2.setText(phone4); else edtxtphone2.setHint("enter phone4");
                break;
            default: f=0; break;
        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone1 = edtxtphone2.getText().toString();
                switch (f){
                    case 1: SMS = "7a*"+phone1+"#"; txtviewphone1.setText("Phone 1: "+phone1); cv.put("phone1", phone1); break;
                    case 2: SMS = "7b*"+phone1+"#"; txtviewphone2.setText("Phone 2: "+phone1); cv.put("phone2", phone1); break;
                    case 3: SMS = "7c*"+phone1+"#"; txtviewphone3.setText("Phone 3: "+phone1); cv.put("phone3", phone1); break;
                    case 4: SMS = "7d*"+phone1+"#"; txtviewphone4.setText("Phone 4: "+phone1); cv.put("phone4", phone1); break;
                }
                dphone.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
        //        cv.put("phone2orEmail", phone2orEmail);
                db.update("sifar", cv, "pphone=?", args);
                db.close();
                sendSMS(pphone,SMS);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dphone.dismiss();
            }
        });
        dphone.show();
    }

   public void setmail (View v){
        Button b1 = (Button)demail.findViewById(R.id.setmailbutton1);
        Button b2 = (Button)demail.findViewById(R.id.setmailbutton2);
       edtxtemail = (EditText)demail.findViewById(R.id.edtxtemail);

       final int f;
       switch (v.getId()) {
           case R.id.tbrow203:
               f = 1; if (!email1.equals(""))edtxtemail.setText(email1); else edtxtemail.setHint("enter email1");
               break;
           case R.id.tbrow213:
               f = 2; if (!email2.equals(""))edtxtemail.setText(email2); else edtxtemail.setHint("enter email2");
               break;
           case R.id.tbrow223:
               f = 3; if (!email3.equals(""))edtxtemail.setText(email3); else edtxtemail.setHint("enter email3");
               break;
           case R.id.tbrow233:
               f = 4; if (!email4.equals(""))edtxtemail.setText(email4); else edtxtemail.setHint("enter email4");
               break;
           default:
               f = 0;
               break;
       }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email1 = edtxtemail.getText().toString();
                switch (f){
                    case 1: SMS = "8a*"+email1+"#"; txtviewmail1.setText("Email 1: "+ email1); cv.put("email1", email1); break;
                    case 2: SMS = "8b*"+email1+"#"; txtviewmail2.setText("Email 2: "+ email1); cv.put("email2", email1); break;
                    case 3: SMS = "8c*"+email1+"#"; txtviewmail3.setText("Email 3: "+ email1); cv.put("email3", email1); break;
                    case 4: SMS = "8d*"+email1+"#"; txtviewmail4.setText("Email 4: "+ email1); cv.put("email4", email1); break;
                }
                demail.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                db.update("sifar", cv, "pphone=?", args);
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
                db.delete("sifar", "pphone=?", new String[]{String.valueOf (pphone)});

                // если это последняя камера, имеющая включённый push, то останавливаем IMAP listener
                Cursor cursor1 = db.query("sifar", new String[]{"push"}, "push=?", new String[]{"enabled"}, null, null, null);
                if (cursor1.getCount()==0) {
                    startService(new Intent(SifarControl.this, IMAPListener.class).putExtra("action", "stopidle"));
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

                startActivity(new Intent(SifarControl.this, MainActivity.class));
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
