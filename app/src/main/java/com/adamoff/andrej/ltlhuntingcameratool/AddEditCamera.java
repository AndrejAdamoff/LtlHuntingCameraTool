package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.t.ltlhuntingcameratool.R;


public class AddEditCamera extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

 //   String pphone, phoneold, aphoneold, name, smtpFrom, smtpTo, smtpToPwd;
 //   String smtp, push, mms, smtprefresh, mmsrefresh, who, recreate = "";
 //   String who, recreate = "";
 //   Boolean bsmtp, bpush, bmms, bsmtprefresh, bmmsrefresh;
    SQLiteDatabase db;
 //   SharedPreferences settings;
    SharedPreferences sharedPref;
    Boolean flag = false;
    String who, recreate = "", phoneold="", aphoneold="", type;
  //  ImageView icon1;
  //  TextView sum1;
 //   AlarmManager alarmManager;
    PowerManager pm;
    PowerManager.WakeLock wl;
    boolean online = false;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
   //     String recreate="";
        Boolean bpush = false;
        if (key.equals("type")) {
            ListPreference listPreference = (ListPreference) findPreference("type");
            recreate = "enabled";
            type = listPreference.getValue();
            ImageView icon = (ImageView)findViewById(R.id.imageView2);
            TextView sum = (TextView)findViewById(R.id.summary);

         //   listPreference.setSummary(value);
            if (type.equals("acorn")) { icon.setImageResource(R.drawable.ltl6210_150); sum.setText("Ltl Acorn");}
            if (type.equals("sifar")) {icon.setImageResource(R.drawable.ltl7310_150); sum.setText("SiFar");}
            if (type.equals("other")) {icon.setImageResource(R.drawable.sg550150); sum.setText("Generic");}
        }

        if (key.equals("phone")) {Preference preference = findPreference("phone"); preference.setSummary(sharedPreferences.getString("phone", "")); recreate = "enabled";}
            if (key.equals("name")) {Preference preference = getPreferenceScreen().findPreference("name"); preference.setSummary(sharedPreferences.getString("name", ""));}
            if (key.equals("smtpTo")) {Preference preference = getPreferenceScreen().findPreference("smtpTo"); preference.setSummary(sharedPreferences.getString("smtpTo", ""));recreate = "enabled";}
            if (key.equals("smtpToPwd")) {Preference preference = getPreferenceScreen().findPreference("smtpToPwd"); preference.setSummary(sharedPreferences.getString("smtpToPwd", ""));recreate = "enabled";}
            if (key.equals("smtp")) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean bsmtp = sp.getBoolean("smtp", true);
                if (bsmtp) { recreate = "enabled"; }
            }
            if (key.equals("mms")) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
               boolean bmms = sp.getBoolean("mms", true);
                if (bmms) { recreate = "enabled"; }
            }
            if (key.equals("smtpFrom")) {Preference preference = getPreferenceScreen().findPreference("smtpFrom"); preference.setSummary(sharedPreferences.getString("smtpFrom", ""));recreate = "enabled";}
            if (key.equals("push")) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                bpush = sp.getBoolean("push", true);
                if (bpush){
                    warning();
                } else {
                    // ---------- сохраняем push --------------------
                    db = MainActivity.dbHelper.getWritableDatabase();
                    ContentValues cv0 = new ContentValues();
                    cv0.put ("push", "disabled");
                    db.update ("smtp", cv0, null, null);
                    ListPreference listPreference = (ListPreference) findPreference("type");
                    db.update(listPreference.getValue(), cv0, "pphone =?", new String[]{sp.getString("phone","")});
                    db.close();

                    // снимаем галочку AlwaysOn:
                    CheckBoxPreference online = (CheckBoxPreference)findPreference("alwaysOn");
                    online.setChecked(false);


               //         Toast.makeText(AddEditCamera.this, "Push deactivated", Toast.LENGTH_LONG).show();
                        // деактивировать обзёрвер
/*                    startService(new Intent(this, StartContentObserverService.class).putExtra("action", "stop"));
*/
  /*                  Intent intent3 = new Intent(getApplicationContext(), StartContentObserverService.class).putExtra("action","stop");
                    PendingIntent pi2 = PendingIntent.getService(this,0, intent3, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
                    alarmManager.cancel(pi2);
*/
                    // проверяем нужно ли останавливать alarm. Для этого проверяем существует ли уже pending intent:
  /*                  if (PendingIntent.getService(AddEditCamera.this, 0, new Intent(AddEditCamera.this, IMAPListener.class).putExtra("action", "restart"), PendingIntent.FLAG_NO_CREATE) == null) {
                        Intent intent2 = new Intent(AddEditCamera.this, IMAPListener.class).putExtra("action", "restart");
                        PendingIntent pi = PendingIntent.getService(AddEditCamera.this, 0, intent2, 0);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
                        //   alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 4*60*1000, 4*60*1000, pi);
                        alarmManager.cancel(pi);}
*/

                    //stopService(new Intent(updatedbWithSmtp.this,IMAPListener.class));
                    // ---------- останавливаем IMAP Listener , там же останавливается alarm ---------------------------
                    startService(new Intent(AddEditCamera.this, IMAPListener.class).putExtra("action", "stopidle"));
               }
            }

        if (key.equals("alwaysOn")) {
            Intent intent = new Intent(AddEditCamera.this, IMAPListener.class).putExtra("action", "wake");
            startService(intent);
          }

        flag = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//           setContentView(R.layout.activity_setting);

    //    RadioButton rb = (RadioButton)findViewById(R.id.radioButton);

    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); // запрещаем поворот экрана. Действует в пределах активити.

        // sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

  //      SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  //      SharedPreferences.Editor editor1 = settings.edit();
  //      editor1.putString("phone", "777");
  //      editor1.commit();
        String pphone, name, smtpFrom="", smtpTo="", smtpToPwd="";
        String smtp="", push="", mms="", sms="", smtprefresh="", mmsrefresh="";

        Intent intent = getIntent();
        who = intent.getStringExtra("who");
        if (who.equals("main")) {
            setTitle("New camera");
    // ----------- прописываем smtpTo и smtpToPwd ---------------------------------------
            db = MainActivity.dbHelper.getWritableDatabase();
            String[] columns1 = {"smtpToMail", "smtpToPassword"};
            Cursor curs1 = db.query("smtp", columns1, null, null, null, null, null);
            if (curs1.moveToFirst()) {
                do {
                    smtpTo = curs1.getString(curs1.getColumnIndex("smtpToMail"));
                    smtpToPwd = curs1.getString(curs1.getColumnIndex("smtpToPassword"));
                } while (curs1.moveToNext());
            }
            curs1.close();
            db.close();

            SharedPreferences sp1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor0 = sp1.edit();
            editor0.putString("smtpTo", smtpTo);
            editor0.putString("smtpToPwd", smtpToPwd);
            editor0.commit();

            addPreferencesFromResource(R.xml.preferences);

       //     icon = (ImageView)findViewById(R.id.imageView2);
       //     sum = (TextView)findViewById(R.id.summary);

            Preference preference = getPreferenceScreen().findPreference("smtpTo");
            preference.setSummary(smtpTo);
            preference = getPreferenceScreen().findPreference("smtpToPwd");
            preference.setSummary(smtpToPwd);
    // -------------------------------------------------------------------------------
        }

        if (who.equals("view")) {
            pphone = intent.getStringExtra("pphone");
            phoneold = pphone;                          // phoneold исп-ся для удаления камеры
            aphoneold = "a" + phoneold.substring(1);
            name = intent.getStringExtra("name");

            // -------------------------- читаем данные из db ----------------------------------------
            db = MainActivity.dbHelper.getWritableDatabase();

        // сначала определяем тип камеры, чтобы выбрать нужную таблицу
            String[] column = {"camtype"};
            Cursor cur1 = db.query(aphoneold, column, null, null, null, null, null);
            if (cur1.moveToFirst()) {
                do {
                type = cur1.getString(cur1.getColumnIndex("camtype"));
                if (type !=null) break;
                }  while (cur1.moveToNext());

            }
            cur1.close();

            String[] columns = {"smtp", "mms", "sms", "smtprefresh", "push", "mmsrefresh", "smtpFromMail"};
            Cursor curs = db.query(type, columns, "pphone=?", new String[]{pphone}, null, null, null);
            if (curs.moveToFirst()) {
               do {
                    smtp = curs.getString(curs.getColumnIndex("smtp"));
                    mms = curs.getString(curs.getColumnIndex("mms"));
                    sms = curs.getString(curs.getColumnIndex("sms"));
                    smtprefresh = curs.getString(curs.getColumnIndex("smtprefresh"));
              //      push = curs.getString(curs.getColumnIndex("push"));
                    mmsrefresh = curs.getString(curs.getColumnIndex("mmsrefresh"));
                    smtpFrom = curs.getString(curs.getColumnIndex("smtpFromMail"));
                } while (curs.moveToNext());
             }
            curs.close();

            String[] columns2 = {"smtpToMail", "smtpToPassword", "push"};
            Cursor curs2 = db.query("smtp", columns2, null, null, null, null, null);
            if (curs2.moveToFirst()){
                do {
                    smtpTo = curs2.getString(curs2.getColumnIndex("smtpToMail"));
                    smtpToPwd = curs2.getString(curs2.getColumnIndex("smtpToPassword"));
                    push = curs2.getString(curs2.getColumnIndex("push"));
                } while (curs2.moveToNext());
            } curs2.close();
            db.close();

            // -------- в преференс записываем данные из db ---------------------------------------------

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor1 = settings.edit();
            editor1.putString("phone", pphone);
        //  editor1.commit();
            editor1.putString("name", name);
            editor1.putString("smtpFrom", smtpFrom);
            editor1.putString("smtpTo", smtpTo);
            editor1.putString("smtpToPwd", smtpToPwd);
            if (smtp.equals("enabled")) editor1.putBoolean("smtp", true);
            else editor1.putBoolean("smtp", false);
            if (push.equals("enabled")) editor1.putBoolean("push", true);
            else editor1.putBoolean("push", false);
            if (sms.equals("enabled")) editor1.putBoolean("sms", true);
            else editor1.putBoolean("sms", false);
            if (mms.equals("enabled")) editor1.putBoolean("mms", true);
            else editor1.putBoolean("mms", false);
            if (smtprefresh.equals("enabled")) editor1.putBoolean("smtp_refresh", true);
            else editor1.putBoolean("smtp_refresh", false);
            if (mmsrefresh.equals("enabled")) editor1.putBoolean("mms_refresh", true);
            else editor1.putBoolean("mms_refresh", false);

            //editor1.apply();
            editor1.commit();

            View view = View.inflate(getApplicationContext(), R.layout.camtype, null);
            TextView sum1 = (TextView)view.findViewById(R.id.summary);
            ImageView icon1 = (ImageView)view.findViewById(R.id.imageView2);
            if (type.equals("acorn")) {
                icon1.setImageResource(R.drawable.ltl6210_150);
                sum1.setText("Ltl Acorn");}
            if (type.equals("sifar")) {
                icon1.setImageResource(R.drawable.ltl7310_150);
                sum1.setText("SiFar");}
            if (type.equals("other")) {icon1.setImageResource(R.drawable.sg550150); sum1.setText("Generic");}


          addPreferencesFromResource(R.xml.preferences);
//          setContentView(R.layout.camtype);
            //  SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            //      settings.StringSet("type", type);
            ListPreference lp = (ListPreference)findPreference("type");
            // System.out.println("777 type "+ lp.getValue());
            //         SharedPreferences.Editor edit = lp.getEditor();
            //        sefindIndexOfValue()getValue();
               lp.setValue(type);


       //     addPreferencesFromResource(R.xml.preferences);


            Preference preference = getPreferenceScreen().findPreference("phone");
            preference.setSummary(pphone);
            preference = getPreferenceScreen().findPreference("name");
            preference.setSummary(name);
            preference = getPreferenceScreen().findPreference("smtpFrom");
            preference.setSummary(smtpFrom);
            preference = getPreferenceScreen().findPreference("smtpTo");
            preference.setSummary(smtpTo);
            preference = getPreferenceScreen().findPreference("smtpToPwd");
            preference.setSummary(smtpToPwd);

  /*          ListPreference listPreference = (ListPreference)getPreferenceScreen().findPreference("type");
            type = listPreference.getValue();
             sum = (TextView)findViewById(R.id.summary);
             icon = (ImageView)findViewById(R.id.imageView2);
            //   listPreference.setSummary(value);
            if (type.equals("acorn")) {icon.setImageResource(R.drawable.ltl6210_150); sum.setText("Ltl Acorn");}
            if (type.equals("sifar")) {icon.setImageResource(R.drawable.ltl7310_150); sum.setText("SiFar");}
            if (type.equals("other")) {icon.setImageResource(R.drawable.sg550); sum.setText("Generic");}
*/
            //       pphone = settings.getString("phone", "");
     //       Toast.makeText(this, "MMS onCreate: "+mms, Toast.LENGTH_LONG).show();
        }
    }

        @Override
        protected void onResume() {
            super.onResume();
            // --- регистрируем листенер для обновления summary ----------------------------------
            PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        }

/*            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            ppphone = sharedPref.getString("phone", "");
            Toast.makeText(this, "ppphone: "+ppphone, Toast.LENGTH_LONG).show();

// ---------- загружаем фрагмент-разметку ----------------------------------------
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.container, new SettingsFragment())
//                    .commit();
// -------------------------------------------------------------------------------
            Preference preference = getPreferenceScreen().findPreference("phone");
            preference.setSummary(pphone);
            preference = getPreferenceScreen().findPreference("name");
            preference.setSummary(name);
        }
    */

    @Override
    protected void onDestroy() {
         super.onDestroy();

        // ------------ разрегистрируем листенер ---------------------------------------------
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

  //      SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  //      ppphone = sharedPref.getString("phone", "");
  //      Toast.makeText(this, "ppphone: " + ppphone, Toast.LENGTH_LONG).show();

       // if (flag) {

        String pphone, name, smtpFrom="", smtpTo="", smtpToPwd="";
        String smtp="", push="", mms="", sms="", smtprefresh="", mmsrefresh="";
        Boolean bsmtp, bpush, bmms, bsms, bsmtprefresh, bmmsrefresh;

            // ------------- читаем новые значения из преференс -----------------------------------
            //  SharedPreferences
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            pphone = sharedPref.getString("phone", "");
        //    Toast.makeText(this, "On Stop pphone: "+pphone, Toast.LENGTH_LONG).show();
            name = sharedPref.getString("name", "");
            smtpFrom = sharedPref.getString("smtpFrom", "");
            smtpTo = sharedPref.getString("smtpTo", "");
            smtpToPwd = sharedPref.getString("smtpToPwd", "");

            bsmtp = sharedPref.getBoolean("smtp", false);
            if (bsmtp) smtp = "enabled";
            else smtp = "disabled";
            bsms = sharedPref.getBoolean("sms", false);
            if (bsms) sms = "enabled";
            else sms = "disabled";
            bpush = sharedPref.getBoolean("push", true);
            if (bpush) push = "enabled";
            else push = "disabled";
            bmms = sharedPref.getBoolean("mms", true);
            if (bmms) mms = "enabled";
            else mms = "disabled";
            bsmtprefresh = sharedPref.getBoolean("smtp_refresh", false);
            if (bsmtprefresh) smtprefresh = "enabled";
            else smtprefresh = "disabled";
            bmmsrefresh = sharedPref.getBoolean("mms_refresh", true);
            if (bmmsrefresh) mmsrefresh = "enabled";
            else mmsrefresh = "disabled";

            //     Toast.makeText(this, "Puse ppphone: " + pphone, Toast.LENGTH_LONG).show();

        if (!recreate.equals("enabled")) { // когда не нужно пересоздавать камеру
            // ------------- сохраняем эти значения в db ----------------------------------------------
            MainActivity.DBHelper dbHelper = new MainActivity.DBHelper(getBaseContext()); // без этого нет контекста, когда приложение закрыто
            db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            if (bsmtp) cv.put("smtp", "enabled");
            else cv.put("smtp", "disabled");
            if (bsms) cv.put("sms", "enabled");
            else cv.put("sms", "disabled");
            if (bpush) cv.put("push", "enabled");
            else cv.put("push", "disabled");
          //  if (bmms) cv.put("mms", "enabled");
          //  else cv.put("mms", "disabled");
            cv.put("mms", mms);
      //      Toast.makeText(this, "MMS onWrite: "+mms, Toast.LENGTH_LONG).show();
            if (bsmtprefresh) cv.put("smtprefresh", "enabled");
            else cv.put("smtprefresh", "disabled");
            if (bmmsrefresh) cv.put("mmsrefresh", "enabled");
            else cv.put("mmsrefresh", "disabled");

            ListPreference listPreference = (ListPreference) findPreference("type");
            db.update(listPreference.getValue(),cv, "pphone=?", new String[]{pphone});
       //     Toast.makeText(this, "Pphone onWrite: "+pphone, Toast.LENGTH_LONG).show();
            db.close();

        } else {
            // запускаем асинхр. addCameraAsync
            Intent intent = new Intent(this, addCameraAsync.class);
            if (who.equals("view")) intent.putExtra("action", "edit").putExtra("pphoneold", phoneold);
            if (who.equals("main")) intent.putExtra("action", "add");
            intent.putExtra("phone", pphone);
   //   Toast.makeText(this, "Pphone add onWrite: "+pphone, Toast.LENGTH_LONG).show();
              intent.putExtra("type",type)
                    .putExtra("tname", name)
                    .putExtra("smtp", smtp)
                    .putExtra("mms", mms)
                    .putExtra("sms", sms)
                    .putExtra("mmsrefresh", mmsrefresh);
              intent.putExtra("smtpToMail", smtpTo)
                    .putExtra("smtpToPwd", smtpToPwd)
                    .putExtra("smtpFromMail", smtpFrom)
                    .putExtra("push", push)
                    .putExtra("smtprefresh", smtprefresh);
            startActivity(intent);
        }
     }

    protected void warning() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Dialog));
        dialog.setTitle("Warning");
        dialog.setMessage(R.string.pushwarning);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // при ручном закрытии диалога снимаем push:
  /*              SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor2 = settings.edit();
                editor2.putBoolean("push", false);
                editor2.commit();
  */
                CheckBoxPreference push = (CheckBoxPreference)findPreference("push");
                push.setChecked(false);
            }
        });

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // --------------- сохраняем push --------------
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                db = MainActivity.dbHelper.getWritableDatabase();
                ContentValues cv0 = new ContentValues();
                cv0.put ("push", "enabled");
                db.update ("smtp", cv0, null, null);
                db.update(type, cv0, "pphone =?", new String[]{sp.getString("phone","")});
                db.close();

           //         Toast.makeText(AddEditCamera.this, "Push activated", Toast.LENGTH_LONG).show();

                // ---------------------- проверка, есть ли network connection ---------------------------
                // ------- если нет, то ничего не запускаем, ждём когда сработает NetworkReceiverState ---------
  //              final ConnectivityManager connectivityManager = (ConnectivityManager) AddEditCamera.this.getSystemService(Context.CONNECTIVITY_SERVICE);
  //             final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

  //              if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    // --------------- включаем IMAPListener ----------------------------------
                    Intent intent = new Intent(AddEditCamera.this, IMAPListener.class).putExtra("action", "startidle").putExtra("smtpToMail", sp.getString("smtpTo", "")).putExtra("smtpToPwd", sp.getString("smtpToPwd", ""));
                    startService(intent);
                System.out.println("7777 AddEdit start idle");
         /*           Intent intent2 = new Intent(AddEditCamera.this, IMAPListener.class).putExtra("action", "restart");
                    PendingIntent pi = PendingIntent.getService(AddEditCamera.this, 0, intent2, 0);
                    alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 4*60*1000, 4*60*1000, pi);
          */
                    // проверяем нужно ли запускать alarm. Для этого проверяем существует ли уже pending intent:
                //    if (PendingIntent.getService(AddEditCamera.this, 0, new Intent(AddEditCamera.this, IMAPListener.class).putExtra("action", "restart"), PendingIntent.FLAG_NO_CREATE) == null) {
                        if (PendingIntent.getService(AddEditCamera.this, 0, new Intent(AddEditCamera.this, IMAPListener.class).putExtra("action", "noop"), PendingIntent.FLAG_NO_CREATE) == null) {
                 //     Intent intent2 = new Intent(AddEditCamera.this, IMAPListener.class).putExtra("action", "restart");
                        Intent intent2 = new Intent(AddEditCamera.this, IMAPListener.class).putExtra("action", "noop");
                        PendingIntent pi = PendingIntent.getService(AddEditCamera.this, 0, intent2, 0);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
                     //   alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 4*60*1000, 4*60*1000, pi);
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 4 * 60 * 1000, 4 * 60 * 1000, pi);
          System.out.println("7777 AddEdit Alarm manager set");
       //    Toast.makeText(getApplicationContext(), "AddEdit Alarm created", Toast.LENGTH_LONG).show();
                    } // else
       //                     Toast.makeText(getApplicationContext(), "AddEdit Alarm NOTcreated", Toast.LENGTH_LONG).show();
  //              }
            }
        });
                dialog.setNegativeButton("Help", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

/*                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor2 = settings.edit();
                        editor2.putBoolean("push", false);
                        editor2.commit();
*/
                        CheckBoxPreference push = (CheckBoxPreference)findPreference("push");
                        push.setChecked(false);

                        startActivity(new Intent (AddEditCamera.this, help.class));
                    }
                });
        dialog.show();
    }
}

