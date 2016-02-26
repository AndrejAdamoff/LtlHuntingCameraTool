package com.adamoff.andrej.ltlhuntingcameratool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.t.ltlhuntingcameratool.R;

public class ServiceDialog extends Activity {
    String dphone;
    String dname;
    String type;
    int n=0;
    AlertDialog alert;
    PendingIntent pi;
    ProgressBar progress;
    RelativeLayout.LayoutParams param;
    RelativeLayout rl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    setContentView(R.layout.activity_progress_bar);
     /*   rl = new RelativeLayout(this);
        param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        ProgressBar progress = new ProgressBar(this);
        progress.setVisibility(View.VISIBLE);
        rl.addView(progress,param);*/

        Intent intent=getIntent();
        n=0;
        onNewIntent(intent);
    //    progress = (ProgressBar)findViewById(R.id.progressBar3);
    //    progress.setVisibility(ProgressBar.INVISIBLE);
 //       progresstext = (TextView)findViewById(R.id.textView54);
//    progresstext.setText("rrntynty");
  //      progresstext.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (alert !=null) {alert.dismiss();}

        dname = intent.getStringExtra("name");
        dphone = intent.getStringExtra("phone");
        type = intent.getStringExtra("type");

        n++;
        String text = getString(R.string.received)+" "+n+" "+type+" "+getString(R.string.from)+" "+dname+" "+dphone;
        //   if(intent.hasExtra("text")) fphone = intent.getStringExtra("text");

        AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Dialog));
        dialog.setTitle("Hunting camera alert");
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setMessage(text);
        dialog.setPositiveButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        n=0;
                        ServiceDialog.this.finish();
                    }
                });
        dialog.setNegativeButton(R.string.read, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
              //   setContentView(R.layout.activity_progress_bar);

                       //         progress.setVisibility(ProgressBar.VISIBLE);
                n=0;
                if (type.equals("MMS")){

             //       Toast toast = Toast.makeText(ServiceDialog.this, R.string.downloadMMS, Toast.LENGTH_LONG);
            //        toast.show();

           //     Intent intent1 = new Intent (getApplicationContext(), updatedbWithMMS.class);
                Intent intent1 = new Intent (getApplicationContext(), cameraView.class);
                intent1.putExtra("tname", dname);
                intent1.putExtra("phone", dphone);
                startActivity(intent1);

          //    getApplicationContext().startService(intent1);
              //      Toast.makeText(getBaseContext(), R.string.download, Toast.LENGTH_SHORT).show();


          //      startActivity(intent1);
                }
 /*               if (type.equals("smtp")){
          //          Toast toast = Toast.makeText(ServiceDialog.this, R.string.downloadSMTP, Toast.LENGTH_LONG);

             // вызов dbUpdateWithSMTP
                    Intent intent = new Intent (getApplicationContext(), cameraView.class);
                    intent.putExtra("tname", dname);
                    intent.putExtra("phone", dphone);
           //         getApplicationContext().startService(intent);
                    startActivity(intent);
                }
*/
                else  {Intent intent2 = new Intent (getApplicationContext(), cameraView.class);
                intent2.putExtra("tname", dname);
                intent2.putExtra("phone", dphone);
                startActivity(intent2);
                }

                ServiceDialog.this.finish();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                ServiceDialog.this.finish();
            }
        });

        alert = dialog.create();
        alert.show();

    }

}