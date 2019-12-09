package com.dhananjay.test;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.clevertap.android.sdk.CleverTapAPI;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private CleverTapAPI clevertapDefaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CleverTapAPI.createNotificationChannel(getApplicationContext(), "1", "Channel", "Description", NotificationManager.IMPORTANCE_MAX, true);
        }
        String fcmRegId = FirebaseInstanceId.getInstance().getToken();
        clevertapDefaultInstance.pushFcmRegistrationId(fcmRegId, true);

        Button btnStart = findViewById(R.id.btnStart);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> profileUpdate = new HashMap<String, Object>();

                //Update pre-defined profile properties
                profileUpdate.put("Name", "Dhananjay");
                profileUpdate.put("Email", "dk+dkulkarni331@clevertap.com");
                //Update custom profile properties
                profileUpdate.put("Product ID", 1);
                profileUpdate.put("Product Image", "https://d35fo82fjcw0y8.cloudfront.net/2018/07/26020307/customer-success-clevertap.jpg");
                profileUpdate.put("Product Name", "CleverTap");

                profileUpdate.put("User ID", "1A7DABE0-5BD1-7D4F-7F27-B292CE9FA20E");
                profileUpdate.put("Location", "Chennai");
                profileUpdate.put("Customer Type", "L");
                profileUpdate.put("Subcription ID", "B53B21CF-951B-7E01-5A61-CCEE7C5531B3");
                profileUpdate.put("Gender", "Female");

                clevertapDefaultInstance.pushProfile(profileUpdate);

                clevertapDefaultInstance.pushEvent("Product viewed");
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        try {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String actionId = extras.getString("actionId");
                if (actionId != null) {
                    Log.d("ACTION_ID", actionId);
                    boolean autoCancel = extras.getBoolean("autoCancel", true);
                    int notificationId = extras.getInt("notificationId", -1);
                    if (autoCancel && notificationId > -1) {
                        NotificationManager notifyMgr =
                                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        if (notifyMgr != null) {
                            notifyMgr.cancel(notificationId);  // the bit that cancels the notification
                        }
                    }
                    Toast.makeText(getBaseContext(), "Action ID is: " + actionId,
                            Toast.LENGTH_SHORT).show();
                }
            }

            clevertapDefaultInstance.pushNotificationClickedEvent(intent.getExtras());
            clevertapDefaultInstance.pushNotificationViewedEvent(intent.getExtras());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        super.onNewIntent(intent);
    }
}
