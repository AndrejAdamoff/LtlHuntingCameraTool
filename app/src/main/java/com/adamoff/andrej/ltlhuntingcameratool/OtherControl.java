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

public class OtherControl extends ActionBarActivity {

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

    Dialog dcommand; EditText editcommand, editvalue, editsms; String command, value, sms;
    Dialog dsms; TextView tvcommand, tvvalue, tvsms;
    Dialog dphone, demail; EditText esmsbgnphone, esmsendphone, esmsbgnmail, esmsendmail;
    Dialog dsendphone, dsendmail; EditText edphone, edmail;
    TextView txtcom1, txtcom2, txtcom3, txtcom4, txtcom5, txtcom6, txtcom7, txtcom8, txtcom9, txtcom10;
    TextView txtval1, txtval2,txtval3, txtval4, txtval5, txtval6, txtval7, txtval8, txtval9, txtval10;
    EditText edtxtphone,edtxtphone1,edtxtphone2,edtxtphone3,edtxtphone4,edtxtemail,edtxtemail1,edtxtemail2,edtxtemail3,edtxtemail4;
    TextView txtviewphone1,txtviewphone2,txtviewphone3,txtviewphone4,txtviewmail1,txtviewmail2,txtviewmail3,txtviewmail4;
    TableRow tbr1, tbr2, tbr3, tbr4, tbr5, tbr6, tbr7, tbr8, tbr9, tbr10, tbr11, tbr12, tbr13, tbr14, tbr15,tbr16,tbr17, tbr18;

    String command1, command2, command3, command4, command5, command6,
            command7,command8,command9,command10,value1,value2,value3,value4,value5,value6,value7,value8,value9, value10,
            sms1,sms2, sms3, sms4, sms5, sms6, sms7, sms8, sms9, sms10,
            smsbgnphone, smsendphone, phone1,phone2,phone3,phone4,email1,email2,email3,email4, smsbgnmail, smsendmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_control1);
        tbr1 = (TableRow)findViewById(R.id.Orow_1); tbr1.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setcommand(v);
                return false;
            }
        });
        tbr2 = (TableRow)findViewById(R.id.Orow_2); tbr2.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setcommand(v);
                return false;
            }
        });
        tbr3 = (TableRow)findViewById(R.id.Orow_3); tbr3.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setcommand(v);
                return false;
            }
        });
        tbr4 = (TableRow)findViewById(R.id.Orow_4); tbr4.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setcommand(v);
                return false;
            }
        });
        tbr5 = (TableRow)findViewById(R.id.Orow_5); tbr5.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setcommand(v);
                return false;
            }
        });
        tbr6 = (TableRow)findViewById(R.id.Orow_6); tbr6.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setcommand(v);
                return false;
            }
        });
        tbr7 = (TableRow)findViewById(R.id.Orow_7); tbr7.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setcommand(v);
                return false;
            }
        });
        tbr8 = (TableRow)findViewById(R.id.Orow_8); tbr8.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setcommand(v);
                return false;
            }
        });
        tbr9 = (TableRow)findViewById(R.id.Orow_9); tbr9.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setcommand(v);
                return false;
            }
        });
        tbr10 = (TableRow)findViewById(R.id.Orow_10); tbr10.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setcommand(v);
                return false;
            }
        });
        tbr11 = (TableRow)findViewById(R.id.Otbrphone1); tbr11.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setphone(v);
                return false;
            }
        });
        tbr12 = (TableRow)findViewById(R.id.Otbrphone2); tbr12.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setphone(v);
                return false;
            }
        });
        tbr13 = (TableRow)findViewById(R.id.Otbrphone3); tbr13.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setphone(v);
                return false;
            }
        });
        tbr14 = (TableRow)findViewById(R.id.Otbrphone4); tbr14.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setphone(v);
                return false;
            }
        });
        tbr15 = (TableRow)findViewById(R.id.Otbrmail1); tbr15.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setmail(v);
                return false;
            }
        });
        tbr16 = (TableRow)findViewById(R.id.Otbrmail2); tbr16.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setmail(v);
                return false;
            }
        });
        tbr17 = (TableRow)findViewById(R.id.Otbrmail3); tbr17.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setmail(v);
                return false;
            }
        });
        tbr18 = (TableRow)findViewById(R.id.Otbrmail4); tbr18.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                setmail(v);
                return false;
            }
        });

        Intent intent = getIntent();
        pphone = intent.getStringExtra("phone");
        aphone = "a" + (pphone.substring(1)); // удаляем +, ставим а
        table = intent.getStringExtra("name");
        cv = new ContentValues();

        readDB();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        // диалог set command
        dcommand = new Dialog(OtherControl.this, R.style.MyStyle);
        dcommand.setContentView(R.layout.other_set_command);
        lp.copyFrom(dcommand.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dcommand.getWindow().setAttributes(lp);
        dcommand.setTitle("Set command");
        txtcom1 = (TextView)findViewById(R.id.Otvcom1); txtcom1.setText(command1);
        txtcom2 = (TextView)findViewById(R.id.Otvcom2); txtcom2.setText(command2);
        txtcom3 = (TextView)findViewById(R.id.Otvcom3); txtcom3.setText(command3);
        txtcom4 = (TextView)findViewById(R.id.Otvcom4); txtcom4.setText(command4);
        txtcom5 = (TextView)findViewById(R.id.Otvcom5); txtcom5.setText(command5);
        txtcom6 = (TextView)findViewById(R.id.Otvcom6); txtcom6.setText(command6);
        txtcom7 = (TextView)findViewById(R.id.Otvcom7); txtcom7.setText(command7);
        txtcom8 = (TextView)findViewById(R.id.Otvcom8); txtcom8.setText(command8);
        txtcom9 = (TextView)findViewById(R.id.Otvcom9); txtcom9.setText(command9);
        txtcom10 = (TextView)findViewById(R.id.Otvcom10); txtcom10.setText(command10);

        txtval1 = (TextView)findViewById(R.id.Otvvalue1); txtval1.setText(value1);
        txtval2 = (TextView)findViewById(R.id.Otvvalue2); txtval2.setText(value2);
        txtval3 = (TextView)findViewById(R.id.Otvvalue3); txtval3.setText(value3);
        txtval4 = (TextView)findViewById(R.id.Otvvalue4); txtval4.setText(value4);
        txtval5 = (TextView)findViewById(R.id.Otvvalue5); txtval5.setText(value5);
        txtval6 = (TextView)findViewById(R.id.Otvvalue6); txtval6.setText(value6);
        txtval7 = (TextView)findViewById(R.id.Otvvalue7); txtval7.setText(value7);
        txtval8 = (TextView)findViewById(R.id.Otvvalue8); txtval8.setText(value8);
        txtval9 = (TextView)findViewById(R.id.Otvvalue9); txtval9.setText(value9);
        txtval10 = (TextView)findViewById(R.id.Otvvalue10); txtval10.setText(value10);

        // диалог phone:
        dphone = new Dialog(OtherControl.this, R.style.MyStyle);
        dphone.setContentView(R.layout.other_setphone);
        //     dphone2.getWindow().setLayout(300, 228);
        lp.copyFrom(dphone.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dphone.getWindow().setAttributes(lp);
        dphone.setTitle("Set phone command");

        txtviewphone1 = (TextView)findViewById(R.id.Otvphone1);
        txtviewphone2 = (TextView)findViewById(R.id.Otvphone2);
        txtviewphone3 = (TextView)findViewById(R.id.Otvphone3);
        txtviewphone4 = (TextView)findViewById(R.id.Otvphone4);
        txtviewphone1.setText("Phone 1: " + phone1);
        txtviewphone2.setText("Phone 2: " + phone2);
        txtviewphone3.setText("Phone 3: " + phone3);
        txtviewphone4.setText("Phone 4: " + phone4);
// диалог send phone:
        dsendphone = new Dialog(this, R.style.MyStyle);
        dsendphone.setContentView(R.layout.other_sendphone);
        lp.copyFrom(dsendphone.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dsendphone.getWindow().setAttributes(lp);
        dsendphone.setTitle("Send phone command");

        // диалог email:
        demail = new Dialog(OtherControl.this, R.style.MyStyle);
        demail.setContentView(R.layout.other_setmail);
        lp.copyFrom(demail.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        demail.getWindow().setAttributes(lp);
        demail.setTitle("Set email command");

        txtviewmail1 = (TextView)findViewById(R.id.Otvmail1);
        txtviewmail2 = (TextView)findViewById(R.id.Otvmail2);
        txtviewmail3 = (TextView)findViewById(R.id.Otvmail3);
        txtviewmail4 = (TextView)findViewById(R.id.Otvmail4);
        txtviewmail1.setText("Email 1: " + email1);
        txtviewmail2.setText("Email 2: " + email2);
        txtviewmail3.setText("Email 3: " + email3);
        txtviewmail4.setText("Email 4: " + email4);
        // диалог send mail:
        dsendmail = new Dialog(this, R.style.MyStyle);
        dsendmail.setContentView(R.layout.other_sendmail);
        lp.copyFrom(dsendmail.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dsendmail.getWindow().setAttributes(lp);
        dsendmail.setTitle("Send mail command");

       // диалог send sms
        dsms = new Dialog(OtherControl.this, R.style.MyStyle);
        dsms.setContentView(R.layout.other_sendsms);
        lp.copyFrom(dsms.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //  d.show();
        dsms.getWindow().setAttributes(lp);
        dsms.setTitle("Send command");
    }

    public void setcommand(final View v){

        Button b1 = (Button)dcommand.findViewById(R.id.setmailbutton1);
        Button b2 = (Button)dcommand.findViewById(R.id.setmailbutton2);
        editcommand = (EditText) dcommand.findViewById(R.id.editcommand);
        editvalue = (EditText) dcommand.findViewById(R.id.editvalue);
        editsms = (EditText) dcommand.findViewById(R.id.editsms);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                //  TextView txtmode = (TextView)findViewById(R.id.txtmode);
                command = editcommand.getText().toString();
                value = editvalue.getText().toString();
                sms = editsms.getText().toString();
 //               phone1 =
                switch (v.getId()){
                    case R.id.Orow_1: cv.put("command1", command ); cv.put("value1", value); cv.put("sms1", sms); ((TextView)findViewById(R.id.Otvcom1)).setText(command); ((TextView)findViewById(R.id.Otvvalue1)).setText(value); break;
                    case R.id.Orow_2: cv.put("command2", command ); cv.put("value2", value); cv.put("sms2", sms); ((TextView)findViewById(R.id.Otvcom2)).setText(command); ((TextView)findViewById(R.id.Otvvalue2)).setText(value); break;
                    case R.id.Orow_3: cv.put("command3", command ); cv.put("value3", value); cv.put("sms3", sms); ((TextView)findViewById(R.id.Otvcom3)).setText(command); ((TextView)findViewById(R.id.Otvvalue3)).setText(value); break;
                    case R.id.Orow_4: cv.put("command4", command ); cv.put("value4", value); cv.put("sms4", sms); ((TextView)findViewById(R.id.Otvcom4)).setText(command); ((TextView)findViewById(R.id.Otvvalue4)).setText(value); break;
                    case R.id.Orow_5: cv.put("command5", command ); cv.put("value5", value); cv.put("sms5", sms); ((TextView)findViewById(R.id.Otvcom5)).setText(command); ((TextView)findViewById(R.id.Otvvalue5)).setText(value); break;
                    case R.id.Orow_6: cv.put("command6", command ); cv.put("value6", value); cv.put("sms6", sms); ((TextView)findViewById(R.id.Otvcom6)).setText(command); ((TextView)findViewById(R.id.Otvvalue6)).setText(value); break;
                    case R.id.Orow_7: cv.put("command7", command ); cv.put("value7", value); cv.put("sms7", sms); ((TextView)findViewById(R.id.Otvcom7)).setText(command); ((TextView)findViewById(R.id.Otvvalue7)).setText(value); break;
                    case R.id.Orow_8: cv.put("command8", command ); cv.put("value8", value); cv.put("sms8", sms); ((TextView)findViewById(R.id.Otvcom8)).setText(command); ((TextView)findViewById(R.id.Otvvalue8)).setText(value); break;
                    case R.id.Orow_9: cv.put("command9", command ); cv.put("value9", value); cv.put("sms9", sms); ((TextView)findViewById(R.id.Otvcom9)).setText(command); ((TextView)findViewById(R.id.Otvvalue9)).setText(value); break;
                    case R.id.Orow_10: cv.put("command10", command ); cv.put("value10", value); cv.put("sms10", sms); ((TextView)findViewById(R.id.Otvcom10)).setText(command); ((TextView)findViewById(R.id.Otvvalue10)).setText(value); break;
     //               case R.id.Otbrphone1: cv.put("", command ); cv.put("value3", value); cv.put("sms3", sms); ((TextView)findViewById(R.id.Otvcom3)).setText(command); ((TextView)findViewById(R.id.Otvvalue3)).setText(value); break;
                }

                dcommand.dismiss();

                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
            //    cv.put("mode", mode);
                db.update("other", cv, "pphone=?", args);
                //              cv.clear();
                db.close();

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dcommand.dismiss();
            }
        });
        dcommand.show();
    }


    public void setphone(View v) {

        Button b1 = (Button) dphone.findViewById(R.id.setmailbutton1);
        Button b2 = (Button) dphone.findViewById(R.id.setmailbutton2);
        esmsbgnphone = (EditText)dphone.findViewById(R.id.smsbgn);
        esmsendphone = (EditText)dphone.findViewById(R.id.smsend);
        esmsbgnphone.setText(smsbgnphone);
        esmsendphone.setText(smsendphone);
/*        db = dbHelper.getWritableDatabase();
        Cursor c = db.query("other", new String[]{smsbgnphone, smsendphone}, "pphone=?", new String[]{pphone}, null,null, null);
        c.moveToFirst();
        smsbgnphone = c.getString(c.getColumnIndex(smsbgnphone));
        smsendphone = c.getString(c.getColumnIndex(smsendphone));
        esmsbgnphone.setText(smsbgnphone);
        esmsendphone.setText(smsendphone);
        c.close();
        db.close();
*/
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                smsbgnphone = esmsbgnphone.getText().toString();
                smsendphone = esmsendphone.getText().toString();
                cv.put("smsbgnphone", smsbgnphone ); cv.put("smsendphone", smsendphone);

                dphone.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                //        cv.put("phone2orEmail", phone2orEmail);
                db.update("other", cv, "pphone=?", args);
                db.close();
       //         sendSMS(pphone,SMS);
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
        esmsbgnmail = (EditText)demail.findViewById(R.id.smsbgn);
        esmsendmail = (EditText)demail.findViewById(R.id.smsend);
        esmsbgnmail.setText(smsbgnmail);
        esmsendmail.setText(smsendmail);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsbgnmail = esmsbgnmail.getText().toString();
                smsendmail = esmsendmail.getText().toString();
                cv.put("smsbgnmail", smsbgnmail ); cv.put("smsendmail", smsendmail);

                demail.dismiss();
                db = dbHelper.getWritableDatabase();
                String args[] = new String[]{pphone};
                //        cv.put("phone2orEmail", phone2orEmail);
                db.update("other", cv, "pphone=?", args);
                db.close();
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

    public void sendsms (View v){
        Button b1 = (Button)dsms.findViewById(R.id.setmailbutton1);
        Button b2 = (Button)dsms.findViewById(R.id.setmailbutton2);
        tvcommand = (TextView)dsms.findViewById(R.id.tvcommand);
        tvvalue = (TextView)dsms.findViewById(R.id.tvvalue);
        tvsms = (TextView)dsms.findViewById(R.id.tvsms);

        readDB();

        switch (v.getId()){

            case R.id.Orow_1:
                tvcommand.setText(command1);
                tvvalue.setText(value1);
                tvsms.setText(sms1);
                break;
            case R.id.Orow_2: tvcommand.setText(command2);  tvvalue.setText(value2);  tvsms.setText(sms2); break;
            case R.id.Orow_3: tvcommand.setText(command3);  tvvalue.setText(value3);  tvsms.setText(sms3); break;
            case R.id.Orow_4: tvcommand.setText(command4);  tvvalue.setText(value4);  tvsms.setText(sms4); break;
            case R.id.Orow_5: tvcommand.setText(command5);  tvvalue.setText(value5);  tvsms.setText(sms5); break;
            case R.id.Orow_6: tvcommand.setText(command6);  tvvalue.setText(value6);  tvsms.setText(sms6); break;
            case R.id.Orow_7: tvcommand.setText(command7);  tvvalue.setText(value7);  tvsms.setText(sms7); break;
            case R.id.Orow_8: tvcommand.setText(command8);  tvvalue.setText(value8);  tvsms.setText(sms8); break;
            case R.id.Orow_9: tvcommand.setText(command9);  tvvalue.setText(value9);  tvsms.setText(sms9); break;
            case R.id.Orow_10: tvcommand.setText(command10);  tvvalue.setText(value10);  tvsms.setText(sms10); break;

        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SMS = tvsms.getText().toString();
                if (SMS.equals("")) Toast.makeText(OtherControl.this, "Command is not configured", Toast.LENGTH_LONG).show();
                   else sendSMS(pphone,SMS);
                dsms.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dsms.dismiss();
            }
        });

        dsms.show();
    }

    public void sendphone(View v){
        final int f;
        Button b1 = (Button)dsendphone.findViewById(R.id.setmailbutton1);
        Button b2 = (Button)dsendphone.findViewById(R.id.setmailbutton2);
        edphone = (EditText)dsendphone.findViewById(R.id.sendphone);

        switch (v.getId()){

            case R.id.Otbrphone1: edphone.setText(phone1); f =1; break;

            case R.id.Otbrphone2: edphone.setText(phone2); f =2; break;

            case R.id.Otbrphone3: edphone.setText(phone3); f =3; break;

            case R.id.Otbrphone4: edphone.setText(phone4); f =4; break;

            default: f=0;
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (f){
                    case 1: phone1 = edphone.getText().toString(); SMS=smsbgnphone+phone1+smsendphone; cv.put("phone1", phone1); txtviewphone1.setText("Phone 1: " + phone1); break;
                    case 2: phone2 = edphone.getText().toString(); SMS=smsbgnphone+phone2+smsendphone; cv.put("phone2", phone2); txtviewphone2.setText("Phone 2: " + phone2); break;
                    case 3: phone3 = edphone.getText().toString(); SMS=smsbgnphone+phone3+smsendphone; cv.put("phone3", phone3); txtviewphone3.setText("Phone 3: " + phone3); break;
                    case 4: phone4 = edphone.getText().toString(); SMS=smsbgnphone+phone4+smsendphone; cv.put("phone4", phone4); txtviewphone4.setText("Phone 4: " + phone4); break;
                }
                db = dbHelper.getWritableDatabase();
                db.update("other", cv, "pphone=?",new String[]{pphone});
                db.close();
                sendSMS(pphone, SMS);
                dsendphone.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             dsendphone.dismiss();
            }
        });
        dsendphone.show();
    }

    public  void sendmail(View v){
        final int f;
        Button b1 = (Button)dsendmail.findViewById(R.id.setmailbutton1);
        Button b2 = (Button)dsendmail.findViewById(R.id.setmailbutton2);
        edmail = (EditText)dsendmail.findViewById(R.id.sendmail);

        switch (v.getId()){

            case R.id.Otbrmail1: edmail.setText(email1); f =1; break;

            case R.id.Otbrmail2: edmail.setText(email2); f =2; break;

            case R.id.Otbrmail3: edmail.setText(email3); f =3; break;

            case R.id.Otbrmail4: edmail.setText(email4); f =4; break;

            default: f=0;
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (f){
                    case 1: email1 = edmail.getText().toString(); SMS = smsbgnmail+email1+smsendmail; cv.put("mail1", email1); txtviewmail1.setText("Email 1: " + email1); break;
                    case 2: email2 = edmail.getText().toString(); SMS = smsbgnmail+email2+smsendmail; cv.put("mail2", email2); txtviewmail2.setText("Email 2: " + email2); break;
                    case 3: email3 = edmail.getText().toString(); SMS = smsbgnmail+email3+smsendmail; cv.put("mail3", email3); txtviewmail3.setText("Email 3: " + email3); break;
                    case 4: email4 = edmail.getText().toString(); SMS = smsbgnmail+email4+smsendmail; cv.put("mail4", email4); txtviewmail4.setText("Email 4: " + email4); break;
                }
                db = dbHelper.getWritableDatabase();
                db.update("other", cv, "pphone=?",new String[]{pphone});
                db.close();
                sendSMS(pphone, SMS);
                dsendmail.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsendmail.dismiss();
            }
        });
        dsendmail.show();
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

    }

    protected void readDB (){
        // читаем параметры из БД:
        dbHelper = new MainActivity.DBHelper(this);
        db = dbHelper.getWritableDatabase();
        String columns[] = {"pphone", "name","command1","command2","command3","command4","command5",
                "command6","command7","command8","command9","command10","value1","value2","value3","value4","value5","value6",
                "value7","value8", "value9", "value10", "sms1", "sms2", "sms3", "sms4","sms5","sms6", "sms7", "sms8", "sms9","sms10",
                "smsbgnphone","smsendphone","phone1","phone2","phone3","phone4","mail1","mail2","mail3","mail4","smsbgnmail", "smsendmail"};

        String args [] = {pphone};
        // читаем строку таблицы с помощью объекта cursor
        Cursor cursor = db.query("other", columns, "pphone=?", args, null, null, null);
        cursor.moveToFirst();
        command1 = cursor.getString(cursor.getColumnIndex("command1"));
        command2 = cursor.getString(cursor.getColumnIndex("command2"));
        command3 = cursor.getString(cursor.getColumnIndex("command3"));
        command4 = cursor.getString(cursor.getColumnIndex("command4"));
        command5  = cursor.getString(cursor.getColumnIndex("command5"));
        command6 = cursor.getString(cursor.getColumnIndex("command6"));
        command7 = cursor.getString(cursor.getColumnIndex("command7"));
        command8 = cursor.getString(cursor.getColumnIndex("command8"));
        command9 = cursor.getString(cursor.getColumnIndex("command9"));
        command10 = cursor.getString(cursor.getColumnIndex("command10"));
        value1 = cursor.getString(cursor.getColumnIndex("value1"));
        value2 = cursor.getString(cursor.getColumnIndex("value2"));
        value3 = cursor.getString(cursor.getColumnIndex("value3"));
        value4 = cursor.getString(cursor.getColumnIndex("value4"));
        value5 = cursor.getString(cursor.getColumnIndex("value5"));
        value6 = cursor.getString(cursor.getColumnIndex("value6"));
        value7 = cursor.getString(cursor.getColumnIndex("value7"));
        value8 = cursor.getString(cursor.getColumnIndex("value8"));
        value9 = cursor.getString(cursor.getColumnIndex("value9"));
        value10 = cursor.getString(cursor.getColumnIndex("value10"));
        sms1 = cursor.getString(cursor.getColumnIndex("sms1"));
        sms2 = cursor.getString(cursor.getColumnIndex("sms2"));
        sms3 = cursor.getString(cursor.getColumnIndex("sms3"));
        sms4 = cursor.getString(cursor.getColumnIndex("sms4"));
        sms5 = cursor.getString(cursor.getColumnIndex("sms5"));
        sms6 = cursor.getString(cursor.getColumnIndex("sms6"));
        sms7 = cursor.getString(cursor.getColumnIndex("sms7"));
        sms8 = cursor.getString(cursor.getColumnIndex("sms8"));
        sms9 = cursor.getString(cursor.getColumnIndex("sms9"));
        sms10 = cursor.getString(cursor.getColumnIndex("sms10"));
        smsbgnphone = cursor.getString(cursor.getColumnIndex("smsbgnphone"));
        smsendphone = cursor.getString(cursor.getColumnIndex("smsendphone"));
        phone1 = cursor.getString(cursor.getColumnIndex("phone1"));
        phone2 = cursor.getString(cursor.getColumnIndex("phone2"));
        phone3 = cursor.getString(cursor.getColumnIndex("phone3"));
        phone4 = cursor.getString(cursor.getColumnIndex("phone4"));
        email1 = cursor.getString(cursor.getColumnIndex("mail1"));
        email2 = cursor.getString(cursor.getColumnIndex("mail2"));
        email3 = cursor.getString(cursor.getColumnIndex("mail3"));
        email4 = cursor.getString(cursor.getColumnIndex("mail4"));
        smsbgnmail = cursor.getString(cursor.getColumnIndex("smsbgnmail"));
        smsendmail = cursor.getString(cursor.getColumnIndex("smsendmail"));

        cursor.close();
        db.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //   startActivity(new Intent(this, cameraView.class));
        //   finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_other_control, menu);
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
            case R.id.editcommands: {
    //            startActivity(new Intent(this, AddEditCamera.class).putExtra("who", "view").putExtra("name",table).putExtra("pphone", pphone));
    //            finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
