package com.sliit.dailyselfie.Challenges;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.sliit.dailyselfie.AlertReciver.AlarmReciver;
import com.sliit.dailyselfie.Camera.CameraActivity;
import com.sliit.dailyselfie.DB.DBHelper;
import com.sliit.dailyselfie.R;

import java.util.GregorianCalendar;

public class NoshaveActivity extends AppCompatActivity {

    private EditText noShavename,noShaveDescription;
    private TextInputLayout inputLayoutName,inputLayoutDescription;
    private Button btnAdd;
    String userID;
    Button bset,bcancle;
    Spinner spn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noshave);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.noShavesetAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Long alertTime = new GregorianCalendar().getTimeInMillis();
                Intent alertIntent = new Intent(NoshaveActivity.this, AlarmReciver.class);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(NoshaveActivity.this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alertTime, alarmManager.INTERVAL_DAY * 7, PendingIntent.getBroadcast(NoshaveActivity.this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));


//                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NoshaveActivity.this);
//                mBuilder.setSmallIcon(R.drawable.ic_noti_dailyselfie);
//                mBuilder.setContentTitle("DailySelfie");
//                mBuilder.setContentText("Time to take a Selfie!");
//                mBuilder.setSound(alarmSound);
//                mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
//                Intent resultIntent = new Intent(NoshaveActivity.this, CameraActivity.class);
//                TaskStackBuilder stackBuilder = TaskStackBuilder.create(NoshaveActivity.this);
//                stackBuilder.addParentStack(CameraActivity.class);
//
//                stackBuilder.addNextIntent(resultIntent);
//                PendingIntent resultPendingIntent =
//                        stackBuilder.getPendingIntent(
//                                0,
//                                PendingIntent.FLAG_UPDATE_CURRENT
//                        );
//                mBuilder.setContentIntent(resultPendingIntent);
//                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.notify(1001, mBuilder.build());
            }
        });

        SharedPreferences userDetails = getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        userID = userDetails.getString("loggedUserId","");

        spn = (Spinner)findViewById(R.id.noshavedate);
        final String [] challengePeriodType = getResources().getStringArray(R.array.challengePeriodType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,challengePeriodType);
        spn.setAdapter(adapter);

        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_noShaveName);
        inputLayoutDescription = (TextInputLayout) findViewById(R.id.input_layout_noShavedescription);

        noShavename = (EditText)findViewById(R.id.noShaveName);
        noShaveDescription = (EditText)findViewById(R.id.noShavedescription);
        btnAdd = (Button) findViewById(R.id.noShave_submit_btn);

        noShavename.addTextChangedListener(new MyTextWatcher(noShavename));
        //fitDescription.addTextChangedListener(new MyTextWatcher(fitDescription));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (submitForm()) {

                    String noshaveChallangename = noShavename.getText().toString();
                    String noshavedescription = noShaveDescription.getText().toString();
                    String type = "NoShave";
                    int userId = Integer.parseInt(userID);

                    DBHelper helper = new DBHelper(NoshaveActivity.this);
                    String sql = "INSERT INTO challanges (type,name,description,userId)" +
                            " VALUES ('"+type+"','"+noshaveChallangename+"','"+noshavedescription+"','"+userId+"') ";

                    SQLiteDatabase db = helper.getWritableDatabase();

                    try {
                        db.execSQL(sql);
                        successfulAlert();

                        SharedPreferences cDetails = getSharedPreferences("cDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor1 = cDetails.edit();
                        editor1.putString("chName", noshaveChallangename);
                        editor1.apply();

                    } catch (SQLiteException e) {
                        AlertDialog.Builder a_builder = new AlertDialog.Builder(NoshaveActivity.this);
                        a_builder.setMessage("User already exist!")
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        noShavename.setText("");
                                        noShaveDescription.setText("");
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert = a_builder.create();
                        alert.setTitle("Alert");
                        alert.show();
                    }

                    //Toast.makeText(this, "Registed !", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void successfulAlert(){
        AlertDialog.Builder a_builder = new AlertDialog.Builder(NoshaveActivity.this);
        a_builder.setMessage("Successfully created No Shave challange")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noShavename.setText("");
                        noShaveDescription.setText("");
                        startActivity(new Intent(getApplicationContext(), CameraActivity.class).putExtra("Challenge", "noshave"));
                        dialog.cancel();
                    }
                });

        AlertDialog alert = a_builder.create();
        alert.setTitle("Congratulation!");
        alert.show();
    }

    private boolean submitForm() {
        if (!validateNSName()) {
            return false;
        }

        return true;
    }

    private boolean validateNSName() {
        if (noShavename.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError("Enter the Challange name");
            requestFocus(noShavename);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.noShaveName:
                    validateNSName();
                    break;

            }
        }
    }

}
