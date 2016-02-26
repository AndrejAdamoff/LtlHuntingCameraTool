package com.adamoff.andrej.ltlhuntingcameratool;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.t.ltlhuntingcameratool.R;

import java.util.Locale;

public class help extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView txt1 = (TextView)findViewById(R.id.txtimap);
        TextView txt2 = (TextView)findViewById(R.id.txtless);
    }

    public void imap (View view){

   //     Toast.makeText(getApplicationContext(), Locale.getDefault().getDisplayLanguage(), Toast.LENGTH_LONG).show();
        if (Locale.getDefault().getLanguage().equals("ru"))
        startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse("https://support.google.com/mail/troubleshooter/1668960?hl=ru&rd=2#ts=1665018")));
        else
        startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse("https://support.google.com/mail/troubleshooter/1668960?hl=en&amp;rd=2#ts=1665018%2C1665142")));
    }

    public void lesssecure (View view){
        if (Locale.getDefault().getLanguage().equals("ru"))
        startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse("https://www.google.com/settings/security/lesssecureapps")));
        else
        startActivity(new Intent().setAction(Intent.ACTION_VIEW).setData(Uri.parse("https://www.google.com/settings/security/lesssecureapps")));

    }
}
