package com.whatsappcleaner;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                Log.v("","Permission is granted");
                textView = (TextView) findViewById(R.id.textView1);
                Utilities utilities = new Utilities(MainActivity.this);
                float size = utilities.getSize();
                if(size == -1){
                    textView.setText("No additional Backups Created.");
                    textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    fab.hide();
                }else{
                    textView.setText("You can clear upto " + size/(1024*1024) + " MB.");
                    textView.setTextColor(Color.RED);
                    fab.show();
                }
            } else {
                Log.v("","Permission is revoked");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("","Permission is granted");
            textView = (TextView) findViewById(R.id.textView1);
            Utilities utilities = new Utilities(MainActivity.this);
            float size = utilities.getSize();
            if(size == -1){
                textView.setText("No additional Backups Created.");
                textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                fab.hide();
            }else{
                textView.setText("You can clear upto " + size/(1024*1024) + " MB.");
                textView.setTextColor(Color.RED);
                fab.show();
            }
        }



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LongDeleteOperation longDeleteOperation = new LongDeleteOperation(MainActivity.this);
                longDeleteOperation.execute();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    public void launchTestService() {
        // Construct our Intent specifying the Service
        Intent i = new Intent(this, PeriodicService.class);
        startService(i);
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(this, PeriodicService.class);
        PendingIntent pintent = PendingIntent
                .getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                8* 1000, pintent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
       /* SharedPreferences settings = getSharedPreferences("whatscrap_settings", 0);
        boolean isChecked = settings.getBoolean("daily_checkbox", false);
        MenuItem item = menu.findItem(R.id.action_check);
        item.setChecked(isChecked);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            final String appPackageName = getPackageName();
            final String promoText = "Hey! I'm using this app! Check it out! \n\n";

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, promoText + "http://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }
       /* else if (id == R.id.action_check) {
            item.setChecked(!item.isChecked());
            SharedPreferences settings = getSharedPreferences("whatscrap_settings", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("daily_checkbox", item.isChecked());
            editor.commit();
            if(item.isChecked()){
                launchTestService();
            }else{
                stopService(new Intent(getBaseContext(), PeriodicService.class));
            }
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Log.v("","Permission: "+permissions[0]+ "was "+grantResults[0]);
                Toast.makeText(MainActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
                textView = (TextView) findViewById(R.id.textView1);
                Utilities utilities = new Utilities(MainActivity.this);
                float size = utilities.getSize();
                if(size == -1){
                    textView.setText("No additional Backups Created.");
                    textView.setTextColor(Color.GREEN);
                    fab.hide();
                }else{
                    textView.setText("You can clear upto " + size/(1024*1024) + " MB.");
                    textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    fab.show();
                }
            }
            else{
                Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
