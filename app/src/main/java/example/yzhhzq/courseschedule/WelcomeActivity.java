package example.yzhhzq.courseschedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.*;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {
    private SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        pref = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo("example.yzhhzq.courseschedule", 0);
            TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
            versionNumber.setText("Version " + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
      // if user check the remember checkbox ,the next time would directly into the Course Sechdule with out login in
        new Handler().postDelayed(new Runnable(){

            boolean key;
            @Override
            public void run() {
                key=pref.getBoolean("remember",false);
                if(key) {
                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
                else
                {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                }
            }

        }, 2500);
    }
}
