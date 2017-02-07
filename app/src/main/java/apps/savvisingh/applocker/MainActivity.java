package apps.savvisingh.applocker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;

import java.util.ArrayList;
import java.util.List;

import apps.savvisingh.applocker.Data.AppInfo;
import apps.savvisingh.applocker.Fragments.AllAppFragment;
import apps.savvisingh.applocker.Fragments.PasswordFragment;
import apps.savvisingh.applocker.Prefrence.SharedPreference;
import apps.savvisingh.applocker.Utils.AppLockConstants;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FragmentManager fragmentManager;

    private Drawer.Result result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        fragmentManager = getSupportFragmentManager();
        //Create the drawer
        result = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("All Applications").withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName("Locked Applications").withIcon(FontAwesome.Icon.faw_lock),
                        new PrimaryDrawerItem().withName("Unlocked Applications").withIcon(FontAwesome.Icon.faw_unlock),
                        new PrimaryDrawerItem().withName("Change Password").withIcon(FontAwesome.Icon.faw_exchange),
                        new PrimaryDrawerItem().withName("Allow Access").withIcon(FontAwesome.Icon.faw_share)
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {

                            if (position == 0) {
                                getSupportActionBar().setTitle("All Applications");
                                Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();

                                  }

                            if (position == 1) {
                                getSupportActionBar().setTitle("Locked Applications");
                                Fragment f = AllAppFragment.newInstance(AppLockConstants.LOCKED);
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();

                                  }

                            if (position == 2) {
                                getSupportActionBar().setTitle("Unlocked Applications");
                                Fragment f = AllAppFragment.newInstance(AppLockConstants.UNLOCKED);
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();

                                 }

                            if (position == 3) {
                                getSupportActionBar().setTitle("Change Password");
                                Fragment f = PasswordFragment.newInstance();
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();

                                    }

                            if (position == 4) {
                                final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "If you have not allowed , allow App Lock so that it can work properly", Toast.LENGTH_LONG).show();
                                result.setSelection(0);
                            }

                        }
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        KeyboardUtil.hideKeyboard(MainActivity.this);
                    }


                    @Override
                    public void onDrawerClosed(View drawerView) {


                    }
                })
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();

        //react on the keyboard
        result.keyboardSupportEnabled(this, true);

    }

    /**
     * get the list of all installed applications in the device
     *
     * @return ArrayList of installed applications or null
     */
    public static List<AppInfo> getListOfInstalledApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<AppInfo> installedApps = new ArrayList();
        List<PackageInfo> apps = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        if (apps != null && !apps.isEmpty()) {

            for (int i = 0; i < apps.size(); i++) {
                PackageInfo p = apps.get(i);
                ApplicationInfo appInfo = null;
                    try {
                        if (null != packageManager.getLaunchIntentForPackage(p.packageName)) {
                           // appInfo = packageManager.getApplicationInfo(p.packageName, 0);
                            AppInfo app = new AppInfo();
                            app.setName(p.applicationInfo.loadLabel(packageManager).toString());
                            app.setPackageName(p.packageName);
                            app.setVersionName(p.versionName);
                            app.setVersionCode(p.versionCode);
                            app.setIcon(p.applicationInfo.loadIcon(packageManager));

                            installedApps.add(app);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }


            return installedApps;
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            if (getCurrentFragment() instanceof AllAppFragment) {
                super.onBackPressed();
            } else {
                fragmentManager.popBackStack();
                getSupportActionBar().setTitle("AllAppFragment");
                Fragment f = AllAppFragment.newInstance(AppLockConstants.ALL_APPS);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, f).commit();
                result.setSelection(0);
            }
        }
    }


    /**
     * Returns currentfragment
     *
     * @return
     */
    public Fragment getCurrentFragment() {
        // TODO Auto-generated method stub
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
