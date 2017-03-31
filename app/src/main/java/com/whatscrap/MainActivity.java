package com.whatscrap;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.getbase.floatingactionbutton.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);
        //Floating action menu
        final FloatingActionsMenu leftlabels = (FloatingActionsMenu) findViewById(R.id.right_labels);
        leftlabels.bringToFront();

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
                    //fab.hide();
                }else{
                    int files = utilities.getNumberOfFiles();
                    textView.setText(files + " old backups found. \n You can clear upto " + size/(1024*1024) + " MB.");
                    textView.setTextColor(Color.RED);
                    //fab.show();
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

            }else{
                int files = utilities.getNumberOfFiles();
                textView.setText(files + " old backups found. \n You can clear upto " + size/(1024*1024) + " MB.");
                textView.setTextColor(Color.RED);
                //fab.show();
            }
        }

        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.app_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AppRate.with(this)
                .setInstallDays(1) // default 10, 0 means install day.
                .setLaunchTimes(2) // default 10
                .setRemindInterval(1) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);

        FloatingActionButton all = new FloatingActionButton(this);
        all.bringToFront();
        all.setTitle("Clear all");
        all.setColorNormal(android.R.color.white);
        all.setColorNormalResId(R.color.colorAccent);
        all.setIcon(R.mipmap.ic_delete_sweep_black_24dp);
        all.setSize(FloatingActionButton.SIZE_NORMAL);
        leftlabels.addButton(all);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftlabels.collapse();
                Utilities utilities = new Utilities(MainActivity.this);
                float num = utilities.getNumberOfFiles();
                if(num == 0){
                    Toast.makeText(MainActivity.this, "No Backups found", Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Delete All?");
                    builder1.setCancelable(false);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    LongDeleteOperation longDeleteOperation = new LongDeleteOperation(MainActivity.this);
                                    longDeleteOperation.execute(true);
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });

        FloatingActionButton old = new FloatingActionButton(this);
        old.bringToFront();
        old.setTitle("Clear old");
        old.setIcon(R.mipmap.ic_delete_black_24dp);
        old.setColorNormalResId(R.color.colorAccent);
        old.setSize(FloatingActionButton.SIZE_NORMAL);
        leftlabels.addButton(old);
        old.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftlabels.collapse();
                Utilities utilities = new Utilities(MainActivity.this);
                float num = utilities.getNumberOfFiles();
                if(num <= 1){
                    Toast.makeText(MainActivity.this, "No Old Backups found", Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Confirm Deletion");
                    builder1.setCancelable(false);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    LongDeleteOperation longDeleteOperation = new LongDeleteOperation(MainActivity.this);
                                    longDeleteOperation.execute(false);
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
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
                    textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }else{
                    int files = utilities.getNumberOfFiles();
                    textView.setText(files + " old backups found. \n You can clear upto " + size/(1024*1024) + " MB.");
                    textView.setTextColor(Color.RED);
                }
            }
            else{
                Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
