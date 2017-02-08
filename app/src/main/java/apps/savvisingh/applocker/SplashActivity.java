package apps.savvisingh.applocker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import apps.savvisingh.applocker.Services.AlarmReceiver;
import apps.savvisingh.applocker.Services.AppCheckServices;
import apps.savvisingh.applocker.Utils.AppLockConstants;

public class SplashActivity extends AppCompatActivity {


    private static int SPLASH_TIME_OUT = 1000;
    SharedPreferences sharedPreferences;
    Context context;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public static int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 12345;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_splash);


        checkPermissions();


    }


    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                OverlayPermissionDialogFragment dialogFragment = new OverlayPermissionDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "Overlay Permission");

            }else if(!hasUsageStatsPermission()){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    UsageAcessDialogFragment dialogFragment = new UsageAcessDialogFragment();
                    ft.add(dialogFragment, null);
                    ft.commitAllowingStateLoss();
            } else {
                    startService();
                }

            }
        }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super().
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
        Log.d("SplashActivity", "cp 1");
        checkPermissions();
//        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
//            // ** if so check once again if we have permission */
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                Log.d("SplashActivity", "cp 2");
//                if (Settings.canDrawOverlays(this)) {
//                    Log.d("SplashActivity", "cp 3");
//                       checkPermissions();
//                }
//            }
//        }else if(requestCode == MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS){
//            if (hasUsageStatsPermission()){
//                Log.d("SplashActivity", "cp 4");
//                checkPermissions();
//            }
//        }
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


    public static class OverlayPermissionDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.ovarlay_permission_description)
                    .setTitle("Overlay Permission")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + getActivity().getPackageName()));
                            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public static class UsageAcessDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.usage_data_access_description)
                    .setTitle("Usage Access Permission")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            startActivityForResult(
                                    new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                                    MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
