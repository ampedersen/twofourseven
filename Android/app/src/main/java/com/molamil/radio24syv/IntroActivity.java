package com.molamil.radio24syv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

public class IntroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Button nextButton = (Button)findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firstRunCheck();
    }

    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish(); // Stop this activity
    }

    private void firstRunCheck()
    {
        String name = getApplicationContext().getPackageName();
        SharedPreferences settings = getSharedPreferences(name, Context.MODE_PRIVATE);

        if (settings.getBoolean("returningUser", false))
        {
            startMainActivity();
        }
        else
        {
            settings.edit().putBoolean("returningUser", true).commit();
        }
    }
}
