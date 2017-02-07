package apps.savvisingh.applocker;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import apps.savvisingh.applocker.Services.AlarmReceiver;
import apps.savvisingh.applocker.Services.AppCheckServices;
import apps.savvisingh.applocker.Utils.AppLockConstants;


/**
 * Created by amitshekhar on 30/04/15.
 */
public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 1000;
    SharedPreferences sharedPreferences;
    Context context;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public static int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 12345;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();


        checkPermissions();

        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(this);
        textView.setText(getResources().getString(R.string.app_name));
        textView.setTextSize(32);
        textView.setGravity(Gravity.CENTER);
        linearLayout.addView(textView);

        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.bg_splash));
        linearLayout.addView(imageView);

        setContentView(linearLayout);





    }


    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }else {
                if(!hasUsageStatsPermission()){
                    startActivityForResult(
                            new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                            MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                }else {
                    startService();
                }

            }
        }
    }


    public void startService(){
        /****************************** too much important don't miss it *****************************/
        startService(new Intent(SplashActivity.this, AppCheckServices.class));

        try {
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
            int interval = (86400 * 1000) / 4;
            if (manager != null) {
                manager.cancel(pendingIntent);
            }
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }


        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        final boolean isPasswordSet = sharedPreferences.getBoolean(AppLockConstants.IS_PASSWORD_SET, false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPasswordSet) {
                    Intent i = new Intent(SplashActivity.this, PasswordActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashActivity.this, PasswordSetActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
        /***************************************************************************************/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            // ** if so check once again if we have permission */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                       checkPermissions();
                }
            }
        }else if(requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS){
            if (hasUsageStatsPermission()){
                checkPermissions();
            }
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
